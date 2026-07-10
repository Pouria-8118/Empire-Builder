package civgame.model.building;

import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.map.TileResourceType;

import java.util.ArrayList;
import java.util.List;

public class BuildingPlacementValidator {

    private BuildingPlacementValidator() {
    }

    public static boolean isTerrainValid(BuildingType buildingType, HexTile tile) {
        if (buildingType == BuildingType.LUMBER_MILL) {
            return tile.getTerrainType() == TerrainType.FOREST;
        }
        if (buildingType == BuildingType.STONE_MINE) {
            return tile.getTerrainType() == TerrainType.MOUNTAIN;
        }
        if (buildingType == BuildingType.IRON_MINE) {
            return tile.getTerrainType() == TerrainType.MOUNTAIN && tile.isIronDepositPresent();
        }
        if (buildingType == BuildingType.FARM) {
            return isFarmResourcePresent(tile);
        }
        return isStableResourcePresent(tile);
    }

    private static boolean isFarmResourcePresent(HexTile tile) {
        if (tile.getTerrainType() != TerrainType.GRASSLAND) {
            return false;
        }
        return tile.getResourceType() == TileResourceType.WHEAT || tile.getResourceType() == TileResourceType.RICE;
    }

    private static boolean isStableResourcePresent(HexTile tile) {
        if (tile.getTerrainType() != TerrainType.PLAIN) {
            return false;
        }
        return tile.getResourceType() == TileResourceType.CATTLE || tile.getResourceType() == TileResourceType.SHEEP;
    }

    public static List<BuildingType> getAvailableBuildingTypes(HexTile tile) {
        List<BuildingType> availableTypes = new ArrayList<BuildingType>();
        BuildingType[] allTypes = BuildingType.values();

        for (int i = 0; i < allTypes.length; i++) {
            if (isTerrainValid(allTypes[i], tile)) {
                availableTypes.add(allTypes[i]);
            }
        }

        return availableTypes;
    }

}