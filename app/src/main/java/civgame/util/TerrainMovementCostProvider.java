package civgame.util;

import civgame.model.game.SeasonType;
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
        if (terrainType == TerrainType.MOUNTAIN || terrainType == TerrainType.VOLCANO) {
            return MOUNTAIN_MOVEMENT_COST;
        }
        if (terrainType == TerrainType.SEA) {
            return SEA_MOVEMENT_COST;
        }
        return DEFAULT_MOVEMENT_COST;
    }

    public static int getMovementCost(TerrainType terrainType, SeasonType season) {
        int cost = getMovementCost(terrainType);

        if (season == SeasonType.AUTUMN && terrainType == TerrainType.SEA) {
            cost = cost + 1;
        }
        if (season == SeasonType.WINTER && terrainType != TerrainType.SEA) {
            cost = cost + 1;
        }

        return cost;
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