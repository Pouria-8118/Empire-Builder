package civgame.util;

import civgame.model.map.TerrainType;

import java.awt.Color;

public class TerrainColorProvider {

    private static final double EXPLORED_DARKEN_FACTOR = 0.5;
    private static final Color UNSEEN_TILE_COLOR = new Color(15, 15, 20);

    private TerrainColorProvider() {
    }

    public static Color getColorForTerrain(TerrainType terrainType) {
        if (terrainType == TerrainType.FOREST) {
            return new Color(34, 102, 51);
        }
        if (terrainType == TerrainType.MOUNTAIN) {
            return new Color(120, 120, 120);
        }
        if (terrainType == TerrainType.GRASSLAND) {
            return new Color(210, 180, 80);
        }
        return new Color(140, 200, 100);
    }

    public static Color getExploredColor(TerrainType terrainType) {
        Color baseColor = getColorForTerrain(terrainType);
        int red = (int) (baseColor.getRed() * EXPLORED_DARKEN_FACTOR);
        int green = (int) (baseColor.getGreen() * EXPLORED_DARKEN_FACTOR);
        int blue = (int) (baseColor.getBlue() * EXPLORED_DARKEN_FACTOR);
        return new Color(red, green, blue);
    }

    public static Color getUnseenColor() {
        return UNSEEN_TILE_COLOR;
    }
}