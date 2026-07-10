package civgame.model.building;

import civgame.util.Constants;

public class BuildingCostProvider {

    private BuildingCostProvider() {
    }

    public static int getWoodCost(BuildingType buildingType) {
        if (buildingType == BuildingType.LUMBER_MILL) {
            return Constants.LUMBER_MILL_WOOD_COST;
        }
        if (buildingType == BuildingType.STONE_MINE) {
            return Constants.STONE_MINE_WOOD_COST;
        }
        if (buildingType == BuildingType.IRON_MINE) {
            return Constants.IRON_MINE_WOOD_COST;
        }
        if (buildingType == BuildingType.FARM) {
            return Constants.FARM_WOOD_COST;
        }
        return Constants.STABLE_WOOD_COST;
    }

    public static int getConstructionActionPointCost(BuildingType buildingType) {
        if (buildingType == BuildingType.LUMBER_MILL) {
            return Constants.LUMBER_MILL_CONSTRUCTION_AP_COST;
        }
        if (buildingType == BuildingType.STONE_MINE) {
            return Constants.STONE_MINE_CONSTRUCTION_AP_COST;
        }
        if (buildingType == BuildingType.IRON_MINE) {
            return Constants.IRON_MINE_CONSTRUCTION_AP_COST;
        }
        if (buildingType == BuildingType.FARM) {
            return Constants.FARM_CONSTRUCTION_AP_COST;
        }
        return Constants.STABLE_CONSTRUCTION_AP_COST;
    }

    // TODO : add getStoneCost / getIronCost here if higher-tier
    // buildings (Hamlet/Village) require multiple resource types
}