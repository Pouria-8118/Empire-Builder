package civgame.model.building;

import civgame.util.Constants;

public class BuildingHitPointsProvider {

    private BuildingHitPointsProvider() {
    }

    public static int getMaxHitPoints(BuildingType buildingType) {
        if (buildingType == BuildingType.FARM) {
            return Constants.FARM_MAX_HIT_POINTS;
        }
        if (buildingType == BuildingType.LUMBER_MILL) {
            return Constants.LUMBER_MILL_MAX_HIT_POINTS;
        }
        if (buildingType == BuildingType.STONE_MINE) {
            return Constants.STONE_MINE_MAX_HIT_POINTS;
        }
        if (buildingType == BuildingType.IRON_MINE) {
            return Constants.IRON_MINE_MAX_HIT_POINTS;
        }
        if (buildingType == BuildingType.STABLE) {
            return Constants.STABLE_MAX_HIT_POINTS;
        }
        if (buildingType == BuildingType.DOCK) {
            return Constants.DOCK_MAX_HIT_POINTS;
        }
        if (buildingType == BuildingType.MONUMENT) {
            return Constants.MONUMENT_MAX_HIT_POINTS;
        }
        if (buildingType == BuildingType.BAZAAR) {
            return Constants.BAZAAR_MAX_HIT_POINTS;
        }
        if (buildingType == BuildingType.TOWN_HALL) {
            return Constants.NEW_TOWN_HALL_MAX_HIT_POINTS;
        }
        return Constants.DEFAULT_BUILDING_MAX_HIT_POINTS;
    }
}