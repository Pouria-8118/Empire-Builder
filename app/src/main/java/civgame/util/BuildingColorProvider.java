package civgame.util;

import civgame.model.building.BuildingType;

import java.awt.Color;

public class BuildingColorProvider {

    private BuildingColorProvider() {
    }

    public static Color getColorForBuildingType(BuildingType buildingType) {
        if (buildingType == BuildingType.LUMBER_MILL) {
            return new Color(101, 67, 33);
        }
        if (buildingType == BuildingType.STONE_MINE) {
            return new Color(90, 90, 100);
        }
        if (buildingType == BuildingType.IRON_MINE) {
            return new Color(160, 80, 30);
        }
        if (buildingType == BuildingType.FARM) {
            return new Color(220, 200, 60);
        }
        return new Color(180, 130, 90);
    }

    // TODO (bonus - visual polish): replace these colored squares with per-building-type icons
}