package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.resource.ResourceType;
import civgame.model.unit.Builder;
import civgame.util.Constants;

public class RoadConstructionService {

    public boolean canBuildRoad(GameSession gameSession, Builder builder, HexCoordinate targetCoordinate) {
        if (!builder.getCoordinate().equals(targetCoordinate)) {
            return false;
        }
        if (!gameSession.isWithinBorder(targetCoordinate)) {
            return false;
        }

        HexTile targetTile = gameSession.getHexMap().getTile(targetCoordinate);
        if (targetTile == null) {
            return false;
        }
        if (targetTile.isRoadPresent()) {
            return false;
        }
        if (targetTile.getTerrainType() == TerrainType.SEA || targetTile.getTerrainType() == TerrainType.MOUNTAIN_RANGE) {
            return false;
        }
        if (!builder.hasEnoughActionPoints(Constants.ROAD_CONSTRUCTION_AP_COST)) {
            return false;
        }
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.WOOD, Constants.ROAD_CONSTRUCTION_WOOD_COST)) {
            return false;
        }

        return true;
    }

    public boolean buildRoad(GameSession gameSession, Builder builder, HexCoordinate targetCoordinate) {
        if (!canBuildRoad(gameSession, builder, targetCoordinate)) {
            return false;
        }

        builder.consumeActionPoints(Constants.ROAD_CONSTRUCTION_AP_COST);
        gameSession.getResourceStorage().removeResource(ResourceType.WOOD, Constants.ROAD_CONSTRUCTION_WOOD_COST);

        HexTile targetTile = gameSession.getHexMap().getTile(targetCoordinate);
        targetTile.setRoadPresent(true);

        return true;
    }
}