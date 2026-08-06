package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexEdgeFeatures;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.map.TileVisibility;
import civgame.model.map.Wall;
import civgame.model.resource.ResourceType;
import civgame.model.unit.Builder;
import civgame.util.Constants;

public class WallConstructionService {

    public boolean canBuildWall(GameSession gameSession, Builder builder, HexCoordinate fromCoordinate, HexCoordinate toCoordinate) {
        if (!builder.getCoordinate().equals(fromCoordinate)) {
            return false;
        }

        HexMap hexMap = gameSession.getHexMap();
        if (!hexMap.containsCoordinate(fromCoordinate) || !hexMap.containsCoordinate(toCoordinate)) {
            return false;
        }

        HexTile fromTile = hexMap.getTile(fromCoordinate);
        HexTile toTile = hexMap.getTile(toCoordinate);

        if (fromTile.getVisibility() == TileVisibility.UNSEEN || toTile.getVisibility() == TileVisibility.UNSEEN) {
            return false;
        }
        if (isSeaOrMountainRange(fromTile) || isSeaOrMountainRange(toTile)) {
            return false;
        }
        if (!gameSession.isWithinBorder(fromCoordinate) && !gameSession.isWithinBorder(toCoordinate)) {
            return false;
        }
        if (hexMap.hasActiveWall(fromCoordinate, toCoordinate)) {
            return false;
        }
        if (!builder.hasEnoughActionPoints(Constants.WALL_CONSTRUCTION_AP_COST)) {
            return false;
        }
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.STONE, Constants.WALL_STONE_COST)) {
            return false;
        }
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.WOOD, Constants.WALL_WOOD_COST)) {
            return false;
        }

        return true;
    }

    private boolean isSeaOrMountainRange(HexTile tile) {
        return tile.getTerrainType() == TerrainType.SEA || tile.getTerrainType() == TerrainType.MOUNTAIN_RANGE;
    }

    public boolean buildWall(GameSession gameSession, Builder builder, HexCoordinate fromCoordinate, HexCoordinate toCoordinate) {
        if (!canBuildWall(gameSession, builder, fromCoordinate, toCoordinate)) {
            return false;
        }

        builder.consumeActionPoints(Constants.WALL_CONSTRUCTION_AP_COST);
        gameSession.getResourceStorage().removeResource(ResourceType.STONE, Constants.WALL_STONE_COST);
        gameSession.getResourceStorage().removeResource(ResourceType.WOOD, Constants.WALL_WOOD_COST);

        HexEdgeFeatures edgeFeatures = gameSession.getHexMap().getOrCreateEdgeFeatures(fromCoordinate, toCoordinate);
        edgeFeatures.setWall(new Wall());

        return true;
    }
}