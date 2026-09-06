package civgame.server.combat;

import civgame.model.building.Building;
import civgame.model.building.TownHall;
import civgame.model.game.CombatDamageApplier;
import civgame.model.game.CombatResolutionService;
import civgame.model.game.CombatResult;
import civgame.model.game.GameSession;
import civgame.model.map.HexCoordinate;
import civgame.model.unit.MilitaryUnitCapProvider;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;
import civgame.network.message.Broadcast;
import civgame.network.message.combat.CombatResultBroadcast;
import civgame.network.message.game.GameOverBroadcast;
import civgame.network.message.game.PlayerEliminatedBroadcast;
import civgame.server.game.MultiplayerGameState;
import civgame.server.game.PlayerState;
import civgame.util.Constants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PvPCombatService {

    private final CombatResolutionService combatResolutionService;
    private final CombatDamageApplier combatDamageApplier;

    public PvPCombatService() {
        this.combatResolutionService = new CombatResolutionService();
        this.combatDamageApplier = new CombatDamageApplier();
    }

    public PvPCombatResult executePvPAttack(MultiplayerGameState game,
                                             PlayerState attackerPlayer,
                                             PlayerState defenderPlayer,
                                             HexCoordinate attackerCoordinate,
                                             HexCoordinate defenderCoordinate) {
        GameSession attackerSession = attackerPlayer.getSession();
        GameSession defenderSession = defenderPlayer.getSession();

        List<Unit> attackerUnits = findMilitaryUnitsAt(attackerSession, attackerCoordinate);
        List<Unit> defenderUnits = findMilitaryUnitsAt(defenderSession, defenderCoordinate);

        if (attackerUnits.isEmpty()) {
            return new PvPCombatResult(false, "No military units at attacker position");
        }

        Building targetBuilding = defenderSession.getBuildingAt(defenderCoordinate);
        TownHall targetTownHall = null;
        if (defenderSession.getTownHall().getCoordinate().equals(defenderCoordinate)) {
            targetTownHall = defenderSession.getTownHall();
        }

        boolean hasDefenderTarget = !defenderUnits.isEmpty() || targetBuilding != null || targetTownHall != null;
        if (!hasDefenderTarget) {
            return new PvPCombatResult(false, "No valid target at defender position");
        }

        consumeAttackActionPoints(attackerUnits);

        List<Unit> attackerLosses = new ArrayList<>();
        List<Unit> defenderLosses = new ArrayList<>();
        CombatResult combatResult = null;
        int buildingDamage = 0;
        boolean buildingDestroyed = false;

        if (!defenderUnits.isEmpty()) {
            int attackerDiceCount = countDiceParticipantTypes(attackerUnits);
            int defenderDiceCount = countDiceParticipantTypes(defenderUnits);
            int wallBonus = defenderSession.getHexMap().hasActiveWall(attackerCoordinate, defenderCoordinate)
                    ? Constants.WALL_DEFENSE_DICE_BONUS : 0;
            int galleyBonus = (hasUnitType(attackerUnits, UnitType.GALLEY) && hasFloatingUnit(defenderUnits))
                    ? Constants.GALLEY_VS_FLOATING_DIE_BONUS : 0;
            
            int attackerCombatBonus = countCombatBoostedUnits(attackerUnits);
            
            combatResult = combatResolutionService.resolveCombat(
                    attackerDiceCount, defenderDiceCount, attackerCombatBonus + galleyBonus, wallBonus);

            attackerLosses = combatDamageApplier.applyHitsToUnits(
                    attackerSession, attackerUnits, combatResult.getAttackerHits());
            defenderLosses = combatDamageApplier.applyHitsToUnits(
                    defenderSession, defenderUnits, combatResult.getDefenderHits());
        }

        if (targetBuilding != null && !targetBuilding.isDestroyed()) {
            int fixedDamage = calculateBuildingDamage(attackerUnits);
            targetBuilding.applyCombatDamage(fixedDamage);
            buildingDamage += fixedDamage;
            if (targetBuilding.isDestroyed()) {
                buildingDestroyed = true;
            }
        }

        List<Broadcast> additionalBroadcasts = new ArrayList<>();

        if (targetTownHall != null && !targetTownHall.isDestroyedByCombat()) {
            int fixedDamage = calculateBuildingDamage(attackerUnits);
            targetTownHall.applyCombatDamage(fixedDamage);
            buildingDamage += fixedDamage;
            if (targetTownHall.isDestroyedByCombat()) {
                buildingDestroyed = true;
                
                if (defenderPlayer.countActiveTownHalls() == 0) {
                    game.eliminatePlayer(defenderPlayer.getClientId());
                    
                    defenderSession.clearAllUnits();
                    defenderSession.clearAllBuildings();
                    
                    PlayerEliminatedBroadcast elimBroadcast = new PlayerEliminatedBroadcast(
                            "server",
                            defenderPlayer.getClientId(),
                            defenderPlayer.getUsername(),
                            attackerPlayer.getClientId(),
                            attackerPlayer.getUsername()
                    );
                    additionalBroadcasts.add(elimBroadcast);
                    
                    if (game.isFinished()) {
                        PlayerState winner = game.getWinner();
                        if (winner != null) {
                            GameOverBroadcast gameOver = new GameOverBroadcast(
                                    "server",
                                    winner.getClientId(),
                                    winner.getUsername()
                            );
                            additionalBroadcasts.add(gameOver);
                        }
                    }
                }
            }
        }

        CombatResultBroadcast broadcast = buildCombatBroadcast(
                attackerPlayer, defenderPlayer,
                attackerCoordinate, defenderCoordinate,
                combatResult, attackerLosses, defenderLosses,
                buildingDamage, buildingDestroyed);

        return new PvPCombatResult(true, "Combat resolved", broadcast, additionalBroadcasts);
    }

    private int countCombatBoostedUnits(List<Unit> units) {
        int count = 0;
        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).isCombatBoosted()) {
                count = count + 1;
            }
        }
        return count;
    }

    private int calculateBuildingDamage(List<Unit> attackerUnits) {
        int totalDamage = 0;
        for (int i = 0; i < attackerUnits.size(); i++) {
            Unit unit = attackerUnits.get(i);
            UnitType type = unit.getUnitType();
            int baseDamage = 0;
            
            if (type == UnitType.SWORDSMAN) {
                baseDamage = 10;
            } else if (type == UnitType.ARCHER) {
                baseDamage = 6;
            } else if (type == UnitType.CAVALRY) {
                baseDamage = 9;
            } else if (type == UnitType.CATAPULT) {
                baseDamage = 20;
            }
            
            if (unit.isCombatBoosted()) {
                baseDamage = baseDamage + 5;
            }
            
            totalDamage += baseDamage;
        }
        return totalDamage;
    }

    private CombatResultBroadcast buildCombatBroadcast(PlayerState attackerPlayer,
                                                        PlayerState defenderPlayer,
                                                        HexCoordinate attackerCoordinate,
                                                        HexCoordinate defenderCoordinate,
                                                        CombatResult combatResult,
                                                        List<Unit> attackerLosses,
                                                        List<Unit> defenderLosses,
                                                        int buildingDamage,
                                                        boolean buildingDestroyed) {
        CombatResultBroadcast broadcast = new CombatResultBroadcast("server");
        broadcast.setAttackerClientId(attackerPlayer.getClientId());
        broadcast.setAttackerUsername(attackerPlayer.getUsername());
        broadcast.setDefenderClientId(defenderPlayer.getClientId());
        broadcast.setDefenderUsername(defenderPlayer.getUsername());
        broadcast.setAttackerQ(attackerCoordinate.getQ());
        broadcast.setAttackerR(attackerCoordinate.getR());
        broadcast.setDefenderQ(defenderCoordinate.getQ());
        broadcast.setDefenderR(defenderCoordinate.getR());
        broadcast.setAutoCaptured(false);
        broadcast.setBuildingDamage(buildingDamage);
        broadcast.setBuildingDestroyed(buildingDestroyed);

        if (combatResult != null) {
            CombatResultBroadcast.CombatResultDTO resultDTO = new CombatResultBroadcast.CombatResultDTO();
            resultDTO.setAttackerDice(combatResult.getAttackerDice());
            resultDTO.setDefenderDice(combatResult.getDefenderDice());
            resultDTO.setAttackerHits(combatResult.getAttackerHits());
            resultDTO.setDefenderHits(combatResult.getDefenderHits());
            broadcast.setCombatResult(resultDTO);
        }

        List<CombatResultBroadcast.UnitLossDTO> attackerLossDTOs = new ArrayList<>();
        for (int i = 0; i < attackerLosses.size(); i++) {
            Unit unit = attackerLosses.get(i);
            attackerLossDTOs.add(new CombatResultBroadcast.UnitLossDTO(
                    unit.getId(), attackerPlayer.getClientId(), unit.getUnitType().name()));
        }
        broadcast.setAttackerLosses(attackerLossDTOs);

        List<CombatResultBroadcast.UnitLossDTO> defenderLossDTOs = new ArrayList<>();
        for (int i = 0; i < defenderLosses.size(); i++) {
            Unit unit = defenderLosses.get(i);
            defenderLossDTOs.add(new CombatResultBroadcast.UnitLossDTO(
                    unit.getId(), defenderPlayer.getClientId(), unit.getUnitType().name()));
        }
        broadcast.setDefenderLosses(defenderLossDTOs);

        return broadcast;
    }

    private List<Unit> findMilitaryUnitsAt(GameSession session, HexCoordinate coordinate) {
        List<Unit> result = new ArrayList<>();
        List<Unit> allUnits = session.getUnits();

        for (int i = 0; i < allUnits.size(); i++) {
            Unit unit = allUnits.get(i);
            if (unit.getCoordinate().equals(coordinate)
                    && MilitaryUnitCapProvider.isMilitaryUnitType(unit.getUnitType())) {
                result.add(unit);
            }
        }

        return result;
    }

    private void consumeAttackActionPoints(List<Unit> participants) {
        for (int i = 0; i < participants.size(); i++) {
            participants.get(i).consumeActionPoints(Constants.ATTACK_ACTION_POINT_COST);
        }
    }

    private int countDiceParticipantTypes(List<Unit> units) {
        Set<UnitType> types = new HashSet<>();
        for (int i = 0; i < units.size(); i++) {
            UnitType unitType = units.get(i).getUnitType();
            if (MilitaryUnitCapProvider.isDiceParticipant(unitType)) {
                types.add(unitType);
            }
        }
        return types.size();
    }

    private boolean hasUnitType(List<Unit> units, UnitType unitType) {
        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getUnitType() == unitType) {
                return true;
            }
        }
        return false;
    }

    private boolean hasFloatingUnit(List<Unit> units) {
        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).isFloating()) {
                return true;
            }
        }
        return false;
    }

    public static class PvPCombatResult {
        private final boolean success;
        private final String message;
        private final CombatResultBroadcast broadcast;
        private final List<Broadcast> additionalBroadcasts;

        public PvPCombatResult(boolean success, String message) {
            this(success, message, null, new ArrayList<>());
        }

        public PvPCombatResult(boolean success, String message, CombatResultBroadcast broadcast) {
            this(success, message, broadcast, new ArrayList<>());
        }

        public PvPCombatResult(boolean success, String message, CombatResultBroadcast broadcast, List<Broadcast> additionalBroadcasts) {
            this.success = success;
            this.message = message;
            this.broadcast = broadcast;
            this.additionalBroadcasts = additionalBroadcasts != null ? additionalBroadcasts : new ArrayList<>();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public CombatResultBroadcast getBroadcast() {
            return broadcast;
        }

        public List<Broadcast> getAdditionalBroadcasts() {
            return additionalBroadcasts;
        }
    }
}