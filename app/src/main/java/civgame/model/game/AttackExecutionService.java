package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.combat.Combatant;
import civgame.model.event.BuildingDestroyedEvent;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexEdgeFeatures;
import civgame.model.map.Wall;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;
import civgame.model.unit.MilitaryUnitCapProvider;
import civgame.util.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: add dedicated entry points for wild-animal and barbarian
// defenders, which reuse CombatResolutionService/CombatDamageApplier but pass a
// fixed defender dice count (1 or 2) instead of countDistinctTypes(...).
public class AttackExecutionService {

    private final CombatTargetService combatTargetService;
    private final CombatResolutionService combatResolutionService;
    private final CombatDamageApplier combatDamageApplier;

    public AttackExecutionService() {
        this.combatTargetService = new CombatTargetService();
        this.combatResolutionService = new CombatResolutionService();
        this.combatDamageApplier = new CombatDamageApplier();
    }

    public AttackOutcome executeAttack(GameSession gameSession, HexCoordinate attackerCoordinate, HexCoordinate defenderCoordinate) {
        if (!combatTargetService.isValidAttack(gameSession, attackerCoordinate, defenderCoordinate)) {
            return null;
        }

        List<Unit> attackerParticipants = combatTargetService.resolveParticipatingUnits(gameSession, attackerCoordinate, defenderCoordinate);
        consumeAttackActionPoints(attackerParticipants);

        List<Unit> defenderUnits = findMilitaryUnitsAt(gameSession, defenderCoordinate);
        Building defenderBuilding = gameSession.getBuildingAt(defenderCoordinate);

        if (defenderUnits.isEmpty()) {
            if (defenderBuilding != null) {
                return executeStructureDamage(gameSession, attackerParticipants, defenderBuilding);
            }
            gameSession.claimBorderTile(defenderCoordinate);
            return new AttackOutcome(null, true, new ArrayList<Unit>(), new ArrayList<Unit>());
        }

        int attackerDiceCount = countDistinctTypes(attackerParticipants);
        int defenderDiceCount = countDistinctTypes(defenderUnits);
        int wallBonus = gameSession.getHexMap().hasActiveWall(attackerCoordinate, defenderCoordinate) ? Constants.WALL_DEFENSE_DICE_BONUS : 0;

        CombatResult combatResult = combatResolutionService.resolveCombat(attackerDiceCount, defenderDiceCount, wallBonus);

        List<Unit> attackerLosses = combatDamageApplier.applyHitsToUnits(gameSession, attackerParticipants, combatResult.getAttackerHits());
        List<Unit> defenderLosses = combatDamageApplier.applyHitsToUnits(gameSession, defenderUnits, combatResult.getDefenderHits());

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

    private int countDistinctTypes(List<Unit> units) {
        Set<UnitType> types = new HashSet<UnitType>();
        for (int i = 0; i < units.size(); i++) {
            types.add(units.get(i).getUnitType());
        }
        return types.size();
    }
}