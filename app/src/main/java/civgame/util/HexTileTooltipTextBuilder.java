package civgame.util;

import civgame.model.building.BuildingPlacementValidator;
import civgame.model.building.BuildingType;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;

import java.util.List;

public class HexTileTooltipTextBuilder {

    private HexTileTooltipTextBuilder() {
    }

    public static String build(HexTile tile) {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("<html>");
        textBuilder.append("Terrain: ").append(tile.getTerrainType().name()).append("<br>");
        textBuilder.append(buildMovementLine(tile.getTerrainType()));
        textBuilder.append(buildResourceLine(tile));
        if (tile.isRoadPresent()) {
            textBuilder.append("Road: Yes<br>");
        }
        textBuilder.append(buildBuildableLine(tile));
        textBuilder.append("</html>");
        return textBuilder.toString();
    }

    private static String buildMovementLine(TerrainType terrainType) {
        if (terrainType == TerrainType.MOUNTAIN_RANGE) {
            return "Movement: Impassable<br>";
        }
        if (terrainType == TerrainType.SEA) {
            return "Movement: Sea (requires Naval Techniques)<br>";
        }
        int cost = TerrainMovementCostProvider.getMovementCost(terrainType);
        return "Movement cost: " + cost + " AP<br>";
    }

    private static String buildResourceLine(HexTile tile) {
        if (!tile.hasResource()) {
            return "";
        }
        return "Resource: " + tile.getResourceType().name() + "<br>";
    }

    private static String buildBuildableLine(HexTile tile) {
        List<BuildingType> availableTypes = BuildingPlacementValidator.getAvailableBuildingTypes(tile);
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