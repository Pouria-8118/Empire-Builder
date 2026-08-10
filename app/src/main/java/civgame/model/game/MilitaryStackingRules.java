package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;
import civgame.util.Constants;

import java.util.List;

public class MilitaryStackingRules {

    private MilitaryStackingRules() {
    }

    public static boolean canAcceptUnit(GameSession gameSession, HexCoordinate coordinate, UnitType unitType) {
        int maxAllowed = getMaxAllowed(unitType);
        int currentCount = countUnitsOfTypeAt(gameSession, coordinate, unitType);
        return currentCount < maxAllowed;
    }

    private static int getMaxAllowed(UnitType unitType) {
        if (unitType == UnitType.SWORDSMAN) {
            return Constants.HEX_MAX_SWORDSMEN;
        }
        if (unitType == UnitType.ARCHER) {
            return Constants.HEX_MAX_ARCHERS;
        }
        if (unitType == UnitType.CAVALRY) {
            return Constants.HEX_MAX_CAVALRY;
        }
        if (unitType == UnitType.CATAPULT) {
            return Constants.HEX_MAX_CATAPULTS;
        }
        return Integer.MAX_VALUE;
    }

    private static int countUnitsOfTypeAt(GameSession gameSession, HexCoordinate coordinate, UnitType unitType) {
        List<Unit> units = gameSession.getUnits();
        int count = 0;

        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (unit.getCoordinate().equals(coordinate) && unit.getUnitType() == unitType) {
                count = count + 1;
            }
        }

        return count;
    }
}