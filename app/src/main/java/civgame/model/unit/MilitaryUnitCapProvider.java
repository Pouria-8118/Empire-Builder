package civgame.model.unit;

import civgame.model.building.TownHallLevel;
import civgame.util.Constants;

public class MilitaryUnitCapProvider {

    private MilitaryUnitCapProvider() {
    }

    public static int getCap(TownHallLevel townHallLevel) {
        if (townHallLevel == TownHallLevel.BASE_CAMP) {
            return Constants.MILITARY_UNIT_CAP_LEVEL_1;
        }
        if (townHallLevel == TownHallLevel.SETTLEMENT) {
            return Constants.MILITARY_UNIT_CAP_LEVEL_2;
        }
        return Constants.MILITARY_UNIT_CAP_LEVEL_3;
    }

    public static boolean isMilitaryUnitType(UnitType unitType) {
        return unitType == UnitType.SWORDSMAN || unitType == UnitType.ARCHER || unitType == UnitType.CAVALRY;
    }
}