package civgame.util;

import civgame.model.map.TerrainType;

public class TerrainMovementCostProvider {

    private static final int FOREST_MOVEMENT_COST = 2;
    private static final int MOUNTAIN_MOVEMENT_COST = 4;
    private static final int DEFAULT_MOVEMENT_COST = 1;

    private TerrainMovementCostProvider() {
    }

    public static int getMovementCost(TerrainType terrainType) {
        if (terrainType == TerrainType.FOREST) {
            return FOREST_MOVEMENT_COST;
        }
        if (terrainType == TerrainType.MOUNTAIN) {
            return MOUNTAIN_MOVEMENT_COST;
        }
        return DEFAULT_MOVEMENT_COST;
    }
}