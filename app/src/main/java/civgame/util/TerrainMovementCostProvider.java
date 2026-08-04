package civgame.util;

import civgame.model.map.TerrainType;

public class TerrainMovementCostProvider {

    private static final int FOREST_MOVEMENT_COST = 2;
    private static final int MOUNTAIN_MOVEMENT_COST = 4;
    private static final int SEA_MOVEMENT_COST = 1;
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
        if (terrainType == TerrainType.SEA) {
            return SEA_MOVEMENT_COST;
        }
        return DEFAULT_MOVEMENT_COST;
    }

    public static boolean isPassable(TerrainType terrainType, boolean navalTechUnlocked) {
        if (terrainType == TerrainType.MOUNTAIN_RANGE) {
            return false;
        }
        if (terrainType == TerrainType.SEA) {
            return navalTechUnlocked;
        }
        return true;
    }
}