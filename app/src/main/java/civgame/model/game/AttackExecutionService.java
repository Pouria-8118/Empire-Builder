package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.combat.Combatant;
import civgame.model.event.BuildingDestroyedEvent;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexEdgeFeatures;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.map.Wall;
import civgame.model.resource.ResourceType;
import civgame.model.tribe.Tribe;
import civgame.model.tribe.TribeGuardUnit;
import civgame.model.tribe.TribeType;
import civgame.model.unit.MilitaryUnitCapProvider;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;
import civgame.util.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttackExecutionService {

    private final CombatTargetService combatTargetService;
    private final CombatResolutionService combatResolutionService;
    private final CombatDamageApplier combatDamageApplier;
    private final TribeMissionService tribeMissionService;
    private final TribeDiplomacyService tribeDiplomacyService;

    public AttackExecutionService() {
        this.combatTargetService = new CombatTargetService();
        this.combatResolutionService = new CombatResolutionService();
        this.combatDamageApplier = new CombatDamageApplier();
        this.tribeMissionService = new TribeMissionService();
        this.tribeDiplomacyService = new TribeDiplomacyService();
    }

    public AttackOutcome executeAttack(GameSession gameSession, HexCoordinate attackerCoordinate, HexCoordinate defenderCoordinate) {
        if (!combatTargetService.isValidAttack(gameSession, attackerCoordinate, defenderCoordinate)) {
            return null;
        }

        List<Unit> attackerParticipants = combatTargetService.resolveParticipatingUnits(gameSession, attackerCoordinate, defenderCoordinate);
        consumeAttackActionPoints(attackerParticipants);

        Tribe targetTribe = gameSession.getTribeAt(defenderCoordinate);
        if (targetTribe != null && !targetTribe.isDefeated()) {
            return executeAttackOnTribe(gameSession, attackerParticipants, targetTribe);
        }

        List<Unit> defenderUnits = findMilitaryUnitsAt(gameSession, defenderCoordinate);
        Building defenderBuilding = gameSession.getBuildingAt(defenderCoordinate);

        if (defenderUnits.isEmpty()) {
            if (defenderBuilding != null) {
                return executeStructureDamage(gameSession, attackerParticipants, defenderBuilding);
            }
            gameSession.claimBorderTile(defenderCoordinate);
            return new AttackOutcome(null, true, new ArrayList<Unit>(), new ArrayList<Unit>());
        }

        int attackerDiceCount = countDiceParticipantTypes(attackerParticipants);
        int defenderDiceCount = countDiceParticipantTypes(defenderUnits);
        int wallBonus = gameSession.getHexMap().hasActiveWall(attackerCoordinate, defenderCoordinate) ? Constants.WALL_DEFENSE_DICE_BONUS : 0;

        CombatResult combatResult = combatResolutionService.resolveCombat(attackerDiceCount, defenderDiceCount, wallBonus);

        List<Unit> attackerLosses = combatDamageApplier.applyHitsToUnits(gameSession, attackerParticipants, combatResult.getAttackerHits());
        List<Unit> defenderLosses = combatDamageApplier.applyHitsToUnits(gameSession, defenderUnits, combatResult.getDefenderHits());

        if (!defenderLosses.isEmpty()) {
            tribeMissionService.registerWarriorMissionKill(gameSession, defenderCoordinate);
        }

        return new AttackOutcome(combatResult, false, attackerLosses, defenderLosses);
    }

    public AttackOutcome executeWallAttack(GameSession gameSession, HexCoordinate attackerCoordinate, HexCoordinate defenderCoordinate) {
        if (!combatTargetService.isValidAttack(gameSession, attackerCoordinate, defenderCoordinate)) {
            return null;
        }

        HexEdgeFeatures edgeFeatures = gameSession.getHexMap().getEdgeFeatures(attackerCoordinate, defenderCoordinate);
        if (edgeFeatures == null || !edgeFeatures.hasActiveWall()) {
            return null;
        }

        List<Unit> attackerParticipants = combatTargetService.resolveParticipatingUnits(gameSession, attackerCoordinate, defenderCoordinate);
        consumeAttackActionPoints(attackerParticipants);

        int totalDamage = sumStructureAttackPower(attackerParticipants);
        Wall wall = edgeFeatures.getWall();
        wall.applyCombatDamage(totalDamage);

        if (wall.isDestroyedByCombat()) {
            edgeFeatures.setWall(null);
        }

        return new AttackOutcome(null, false, new ArrayList<Unit>(), new ArrayList<Unit>());
    }

    private AttackOutcome executeAttackOnTribe(GameSession gameSession, List<Unit> attackerParticipants, Tribe tribe) {
        if (!tribe.getRelationState().isAtWar()) {
            tribeDiplomacyService.declareWar(gameSession, tribe);
        }

        List<TribeGuardUnit> guards = tribe.getGuards();
        if (!guards.isEmpty()) {
            return resolveCombatAgainstGuards(gameSession, attackerParticipants, tribe, guards);
        }

        return damageTribeCamp(gameSession, attackerParticipants, tribe);
    }

    private AttackOutcome resolveCombatAgainstGuards(GameSession gameSession, List<Unit> attackerParticipants, Tribe tribe, List<TribeGuardUnit> guards) {
        int attackerDiceCount = countDiceParticipantTypes(attackerParticipants);
        int defenderDiceCount = countDistinctGuardTypes(guards);

        CombatResult combatResult = combatResolutionService.resolveCombat(attackerDiceCount, defenderDiceCount, 0);

        List<Unit> attackerLosses = combatDamageApplier.applyHitsToUnits(gameSession, attackerParticipants, combatResult.getAttackerHits());
        applyHitsToGuards(tribe, combatResult.getDefenderHits());

        return new AttackOutcome(combatResult, false, attackerLosses, new ArrayList<Unit>());
    }

    private void applyHitsToGuards(Tribe tribe, int hitCount) {
        List<TribeGuardUnit> guards = tribe.getGuards();
        List<TribeGuardUnit> ordered = new ArrayList<TribeGuardUnit>();
        addGuardsByType(guards, ordered, UnitType.SWORDSMAN);
        addGuardsByType(guards, ordered, UnitType.ARCHER);
        addGuardsByType(guards, ordered, UnitType.CAVALRY);

        int remainingHits = hitCount;
        List<TribeGuardUnit> killed = new ArrayList<TribeGuardUnit>();

        for (int i = 0; i < ordered.size() && remainingHits > 0; i++) {
            TribeGuardUnit guard = ordered.get(i);
            guard.applyCombatDamage(1);
            remainingHits = remainingHits - 1;
            if (guard.isDestroyedByCombat()) {
                killed.add(guard);
            }
        }

        guards.removeAll(killed);
    }

    private void addGuardsByType(List<TribeGuardUnit> source, List<TribeGuardUnit> destination, UnitType unitType) {
        for (int i = 0; i < source.size(); i++) {
            if (source.get(i).getCombatCategory() == unitType) {
                destination.add(source.get(i));
            }
        }
    }

    private int countDistinctGuardTypes(List<TribeGuardUnit> guards) {
        Set<UnitType> types = new HashSet<UnitType>();
        for (int i = 0; i < guards.size(); i++) {
            types.add(guards.get(i).getCombatCategory());
        }
        return types.size();
    }

    private AttackOutcome damageTribeCamp(GameSession gameSession, List<Unit> attackerParticipants, Tribe tribe) {
        int totalDamage = sumStructureAttackPower(attackerParticipants);
        tribe.applyCombatDamage(totalDamage);

        if (tribe.isDestroyedByCombat()) {
            resolveTribeDefeat(gameSession, tribe);
        }

        return new AttackOutcome(null, false, new ArrayList<Unit>(), new ArrayList<Unit>());
    }

    private void resolveTribeDefeat(GameSession gameSession, Tribe tribe) {
        gameSession.claimBorderTile(tribe.getCampCoordinate());

        List<HexCoordinate> neighbors = tribe.getCampCoordinate().getNeighbors();
        for (int i = 0; i < neighbors.size(); i++) {
            HexCoordinate neighbor = neighbors.get(i);
            HexTile tile = gameSession.getHexMap().getTile(neighbor);
            if (tile != null && tile.getTerrainType() != TerrainType.SEA && tile.getTerrainType() != TerrainType.MOUNTAIN_RANGE) {
                gameSession.claimBorderTile(neighbor);
            }
        }

        grantTribeDefeatLoot(gameSession, tribe);
        tribe.setDefeated(true);
    }

    private void grantTribeDefeatLoot(GameSession gameSession, Tribe tribe) {
        TribeType tribeType = tribe.getTribeType();

        if (tribeType == TribeType.FARMER) {
            gameSession.getResourceStorage().addResource(ResourceType.FOOD, Constants.TRIBE_DEFEAT_LOOT_AMOUNT);
        } else if (tribeType == TribeType.MOUNTAIN) {
            gameSession.getResourceStorage().addResource(ResourceType.STONE, Constants.TRIBE_DEFEAT_LOOT_AMOUNT);
        } else if (tribeType == TribeType.TRADER) {
            gameSession.getResourceStorage().addResource(ResourceType.WOOD, Constants.TRIBE_DEFEAT_LOOT_AMOUNT / 2);
            gameSession.getResourceStorage().addResource(ResourceType.STONE, Constants.TRIBE_DEFEAT_LOOT_AMOUNT / 2);
        } else if (tribeType == TribeType.WARRIOR) {
            gameSession.getResourceStorage().addResource(ResourceType.IRON, Constants.TRIBE_DEFEAT_LOOT_AMOUNT);
        } else {
            gameSession.getResourceStorage().addResource(ResourceType.FOOD, Constants.TRIBE_DEFEAT_LOOT_AMOUNT / 2);
            gameSession.getResourceStorage().addResource(ResourceType.WOOD, Constants.TRIBE_DEFEAT_LOOT_AMOUNT / 2);
        }
    }

    private AttackOutcome executeStructureDamage(GameSession gameSession, List<Unit> attackerParticipants, Building building) {
        int totalDamage = sumStructureAttackPower(attackerParticipants);

        building.applyCombatDamage(totalDamage);

        if (building.isDestroyedByCombat()) {
            gameSession.removeBuildingAt(building.getCoordinate());
            gameSession.getEventBus().publish(new BuildingDestroyedEvent(building));
        }

        return new AttackOutcome(null, false, new ArrayList<Unit>(), new ArrayList<Unit>());
    }

    private void consumeAttackActionPoints(List<Unit> participants) {
        for (int i = 0; i < participants.size(); i++) {
            participants.get(i).consumeActionPoints(Constants.ATTACK_ACTION_POINT_COST);
        }
    }

    private int sumStructureAttackPower(List<Unit> units) {
        int total = 0;
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (unit instanceof Combatant) {
                total = total + ((Combatant) unit).getAttackPower();
            }
        }
        return total;
    }

    private List<Unit> findMilitaryUnitsAt(GameSession gameSession, HexCoordinate coordinate) {
        List<Unit> result = new ArrayList<Unit>();
        List<Unit> allUnits = gameSession.getUnits();
        for (int i = 0; i < allUnits.size(); i++) {
            Unit unit = allUnits.get(i);
            if (unit.getCoordinate().equals(coordinate) && MilitaryUnitCapProvider.isMilitaryUnitType(unit.getUnitType())) {
                result.add(unit);
            }
        }
        return result;
    }

    private int countDiceParticipantTypes(List<Unit> units) {
        Set<UnitType> types = new HashSet<UnitType>();
        for (int i = 0; i < units.size(); i++) {
            UnitType unitType = units.get(i).getUnitType();
            if (MilitaryUnitCapProvider.isDiceParticipant(unitType)) {
                types.add(unitType);
            }
        }
        return types.size();
    }
}