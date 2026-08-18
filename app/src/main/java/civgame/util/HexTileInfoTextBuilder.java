package civgame.util;

import civgame.model.building.BuildingPlacementValidator;
import civgame.model.building.BuildingType;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;

import java.util.ArrayList;
import java.util.List;

public class HexTileInfoTextBuilder {

    private HexTileInfoTextBuilder() {
    }

    public static List<String> build(HexTile tile, HexMap hexMap) {
        List<String> lines = new ArrayList<String>();
        lines.add("Terrain: " + tile.getTerrainType().name());
        lines.add(buildMovementLine(tile.getTerrainType()));

        String resourceLine = buildResourceLine(tile);
        if (!resourceLine.isEmpty()) {
            lines.add(resourceLine);
        }
        if (tile.isRoadPresent()) {
            lines.add("Road: Yes");
        }

        lines.add(buildBuildableLine(tile, hexMap));
        return lines;
    }

    private static String buildMovementLine(TerrainType terrainType) {
        if (terrainType == TerrainType.MOUNTAIN_RANGE) {
            return "Movement: Impassable";
        }
        if (terrainType == TerrainType.SEA) {
            return "Movement: Sea (requires Naval Techniques)";
        }
        int cost = TerrainMovementCostProvider.getMovementCost(terrainType);
        return "Movement cost: " + cost + " AP";
    }

    private static String buildResourceLine(HexTile tile) {
        if (!tile.hasResource()) {
            return "";
        }
        return "Resource: " + tile.getResourceType().name();
    }

    private static String buildBuildableLine(HexTile tile, HexMap hexMap) {
        List<BuildingType> availableTypes = BuildingPlacementValidator.getAvailableBuildingTypes(tile, hexMap);
        if (availableTypes.isEmpty()) {
            return "Buildable: None";
        }

        StringBuilder namesBuilder = new StringBuilder();
        for (int i = 0; i < availableTypes.size(); i++) {
            if (i > 0) {
                namesBuilder.append(", ");
            }
            namesBuilder.append(availableTypes.get(i).name());
        }
        return "Buildable: " + namesBuilder.toString();
    }
}