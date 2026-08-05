package civgame.util;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexEdgeFeatures;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;

public class EdgeMovementCostProvider {

    private EdgeMovementCostProvider() {
    }

    public static int getStepCost(HexMap hexMap, HexCoordinate fromCoordinate, HexCoordinate toCoordinate, int baseCost) {
        HexTile fromTile = hexMap.getTile(fromCoordinate);
        HexTile toTile = hexMap.getTile(toCoordinate);
        boolean bridgedByRoad = fromTile != null && toTile != null && fromTile.isRoadPresent() && toTile.isRoadPresent();

        int cost = bridgedByRoad ? Constants.ROAD_MOVEMENT_COST : baseCost;
        cost = cost + getRiverCrossingPenalty(hexMap, fromCoordinate, toCoordinate, bridgedByRoad);

        return cost;
    }

    private static int getRiverCrossingPenalty(HexMap hexMap, HexCoordinate fromCoordinate, HexCoordinate toCoordinate, boolean bridgedByRoad) {
        HexEdgeFeatures edgeFeatures = hexMap.getEdgeFeatures(fromCoordinate, toCoordinate);
        if (edgeFeatures == null || !edgeFeatures.isRiverPresent()) {
            return 0;
        }
        if (bridgedByRoad) {
            return Constants.RIVER_CROSSING_COST_WITH_ROAD;
        }
        return Constants.RIVER_CROSSING_EXTRA_COST;
    }
}