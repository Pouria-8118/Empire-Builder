package civgame.util;

import civgame.model.building.BuildingType;

public class BuildingIconProvider {

    private BuildingIconProvider() {
    }

    public static String getIconPath(BuildingType buildingType) {
        if (buildingType == BuildingType.LUMBER_MILL) {
            return Constants.LUMBER_MILL_ICON_PATH;
        }
        if (buildingType == BuildingType.STONE_MINE) {
            return Constants.STONE_MINE_ICON_PATH;
        }
        if (buildingType == BuildingType.IRON_MINE) {
            return Constants.IRON_MINE_ICON_PATH;
        }
        if (buildingType == BuildingType.FARM) {
            return Constants.FARM_ICON_PATH;
        }
        if (buildingType == BuildingType.DOCK) {
            return Constants.DOCK_ICON_PATH;
        }
        if (buildingType == BuildingType.MONUMENT) {
            return Constants.MONUMENT_ICON_PATH;
        }
        return Constants.STABLE_ICON_PATH;
    }

    public static String getFallbackLabel(BuildingType buildingType) {
        if (buildingType == BuildingType.LUMBER_MILL) {
            return "LM";
        }
        if (buildingType == BuildingType.STONE_MINE) {
            return "SM";
        }
        if (buildingType == BuildingType.IRON_MINE) {
            return "IM";
        }
        if (buildingType == BuildingType.FARM) {
            return "FM";
        }
        if (buildingType == BuildingType.DOCK) {
            return "DK";
        }
        if (buildingType == BuildingType.MONUMENT) {
            return "MN";
        }
        return "ST";
    }
}