package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;
import civgame.util.Constants;
import civgame.model.unit.MilitaryUnitCapProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CombatTargetService {

    public Set<HexCoordinate> getAttackableTargets(GameSession gameSession, HexCoordinate attackerCoordinate) {
        Set<HexCoordinate> targets = new HashSet<HexCoordinate>();
        boolean hasRangedUnit = hasRangedUnit(gameSession, attackerCoordinate);
        HexMap hexMap = gameSession.getHexMap();

        for (HexCoordinate coordinate : hexMap.getAllTiles().keySet()) {
            int distance = attackerCoordinate.distanceTo(coordinate);
            if (distance == 1) {
                targets.add(coordinate);
            } else if (distance == 2 && hasRangedUnit) {
                targets.add(coordinate);
            }
        }

        return targets;
    }

    public boolean isValidAttack(GameSession gameSession, HexCoordinate attackerCoordinate, HexCoordinate defenderCoordinate) {
        if (attackerCoordinate.equals(defenderCoordinate)) {
            return false;
        }
        if (gameSession.isWithinBorder(defenderCoordinate)) {
            return false;
        }
        if (!hasAnyMilitaryUnit(gameSession, attackerCoordinate)) {
            return false;
        }

        int distance = attackerCoordinate.distanceTo(defenderCoordinate);
        if (distance != 1 && distance != 2) {
            return false;
        }
        if (distance == 2 && !hasRangedUnit(gameSession, attackerCoordinate)) {
            return false;
        }

        List<Unit> participants = resolveParticipatingUnits(gameSession, attackerCoordinate, defenderCoordinate);
        if (participants.isEmpty()) {
            return false;
        }
        for (int i = 0; i < participants.size(); i++) {
            if (!participants.get(i).hasEnoughActionPoints(Constants.ATTACK_ACTION_POINT_COST)) {
                return false;
            }
        }

        return true;
    }

    public List<Unit> resolveParticipatingUnits(GameSession gameSession, HexCoordinate attackerCoordinate, HexCoordinate defenderCoordinate) {
        List<Unit> participants = new ArrayList<Unit>();
        int distance = attackerCoordinate.distanceTo(defenderCoordinate);
        List<Unit> units = gameSession.getUnits();

        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (!unit.getCoordinate().equals(attackerCoordinate)) {
                continue;
            }
            if (!MilitaryUnitCapProvider.isMilitaryUnitType(unit.getUnitType())) {
                continue;
            }

            if (distance == 1) {
                participants.add(unit);
            } else if (distance == 2 && isRangedType(unit.getUnitType())) {
                participants.add(unit);
            }
        }

        return participants;
    }

    private boolean isRangedType(UnitType unitType) {
        return unitType == UnitType.ARCHER || unitType == UnitType.CATAPULT;
    }

    private boolean hasRangedUnit(GameSession gameSession, HexCoordinate coordinate) {
        return hasUnitOfType(gameSession, coordinate, UnitType.ARCHER) || hasUnitOfType(gameSession, coordinate, UnitType.CATAPULT);
    }

    private boolean hasAnyMilitaryUnit(GameSession gameSession, HexCoordinate coordinate) {
        List<Unit> units = gameSession.getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (unit.getCoordinate().equals(coordinate) && MilitaryUnitCapProvider.isMilitaryUnitType(unit.getUnitType())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasUnitOfType(GameSession gameSession, HexCoordinate coordinate, UnitType unitType) {
        List<Unit> units = gameSession.getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (unit.getCoordinate().equals(coordinate) && unit.getUnitType() == unitType) {
                return true;
            }
        }
        return false;
    }
}