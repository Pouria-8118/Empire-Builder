package civgame.util;

import civgame.model.map.TerrainType;

import java.awt.Color;

public class TerrainColorProvider {

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

    // TODO : add a darker/desaturated variant for tiles hidden behind fog of war
}