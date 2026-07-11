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
        return "X";
    }
}