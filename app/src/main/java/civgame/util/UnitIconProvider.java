package civgame.util;

import civgame.model.unit.UnitType;

public class UnitIconProvider {

    private UnitIconProvider() {
    }

    public static String getIconPath(UnitType unitType) {
        if (unitType == UnitType.EXPLORER) {
            return Constants.EXPLORER_ICON_PATH;
        }
        if (unitType == UnitType.BUILDER) {
            return Constants.BUILDER_ICON_PATH;
        }
        if (unitType == UnitType.WORKER) {
            return Constants.WORKER_ICON_PATH;
        }
        if (unitType == UnitType.SWORDSMAN) {
            return Constants.SWORDSMAN_ICON_PATH;
        }
        if (unitType == UnitType.ARCHER) {
            return Constants.ARCHER_ICON_PATH;
        }
        if (unitType == UnitType.CAVALRY) {
            return Constants.CAVALRY_ICON_PATH;
        }
        if (unitType == UnitType.CATAPULT) {
            return Constants.CATAPULT_ICON_PATH;
        }
        if (unitType == UnitType.FISHING_BOAT) {
            return Constants.FISHING_BOAT_ICON_PATH;
        }
        if (unitType == UnitType.GALLEY) {
            return Constants.GALLEY_ICON_PATH;
        }
        return Constants.BORDER_EXPANDER_ICON_PATH;
    }

    public static String getFallbackLetter(UnitType unitType) {
        if (unitType == UnitType.EXPLORER) {
            return "E";
        }
        if (unitType == UnitType.BUILDER) {
            return "B";
        }
        if (unitType == UnitType.WORKER) {
            return "W";
        }
        if (unitType == UnitType.SWORDSMAN) {
            return "S";
        }
        if (unitType == UnitType.ARCHER) {
            return "A";
        }
        if (unitType == UnitType.CAVALRY) {
            return "C";
        }
        if (unitType == UnitType.CATAPULT) {
            return "P";
        }
        if (unitType == UnitType.FISHING_BOAT) {
            return "F";
        }
        if (unitType == UnitType.GALLEY) {
            return "G";
        }
        return "X";
    }
}