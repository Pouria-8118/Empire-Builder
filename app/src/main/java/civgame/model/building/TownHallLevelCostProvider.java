package civgame.model.building;

import civgame.util.Constants;

public class TownHallLevelCostProvider {

    private TownHallLevelCostProvider() {
    }

    public static int getWoodCost(TownHallLevel targetLevel) {
        if (targetLevel == TownHallLevel.SETTLEMENT) {
            return Constants.TOWN_HALL_LEVEL_2_WOOD_COST;
        }
        return 0;
    }

    public static int getStoneCost(TownHallLevel targetLevel) {
        if (targetLevel == TownHallLevel.SETTLEMENT) {
            return Constants.TOWN_HALL_LEVEL_2_STONE_COST;
        }
        if (targetLevel == TownHallLevel.CAPITAL) {
            return Constants.TOWN_HALL_LEVEL_3_STONE_COST;
        }
        return 0;
    }

    public static int getIronCost(TownHallLevel targetLevel) {
        if (targetLevel == TownHallLevel.CAPITAL) {
            return Constants.TOWN_HALL_LEVEL_3_IRON_COST;
        }
        return 0;
    }

    public static int getDurationInTurns(TownHallLevel targetLevel) {
        if (targetLevel == TownHallLevel.SETTLEMENT) {
            return Constants.TOWN_HALL_LEVEL_2_TURNS;
        }
        if (targetLevel == TownHallLevel.CAPITAL) {
            return Constants.TOWN_HALL_LEVEL_3_TURNS;
        }
        return 0;
    }

    public static int getStorageCapacityBonus(TownHallLevel targetLevel) {
        if (targetLevel == TownHallLevel.SETTLEMENT) {
            return Constants.TOWN_HALL_LEVEL_2_STORAGE_CAPACITY_BONUS;
        }
        if (targetLevel == TownHallLevel.CAPITAL) {
            return Constants.TOWN_HALL_LEVEL_3_STORAGE_CAPACITY_BONUS;
        }
        return 0;
    }
}