package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.event.BuildingDestroyedEvent;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexEdgeFeatures;
import civgame.model.map.HexTile;
import civgame.model.unit.Builder;
import civgame.model.map.TradingPost;
import civgame.model.tribe.Tribe;
import civgame.util.Constants;

import java.util.List;

public class DemolitionService {

    public boolean canDemolishBuilding(GameSession gameSession, Builder builder, HexCoordinate targetCoordinate) {
        if (!isBuilderNear(builder, targetCoordinate)) {
            return false;
        }

        Building building = gameSession.getBuildingAt(targetCoordinate);
        if (building == null || building.isDestroyed()) {
            return false;
        }
        if (!builder.hasEnoughActionPoints(Constants.DEMOLITION_AP_COST)) {
            return false;
        }

        if (isProtectedStructure(gameSession, targetCoordinate)) {
            return false;
        }

        return true;
    }

    private boolean isProtectedStructure(GameSession gameSession, HexCoordinate coordinate) {
        if (coordinate.equals(gameSession.getTownHall().getCoordinate())) {
            return true;
        }

        TradingPost tradingPost = gameSession.getTradingPostAt(coordinate);
        if (tradingPost != null) {
            return true;
        }

        Tribe tribe = gameSession.getTribeAt(coordinate);
        if (tribe != null && !tribe.isDefeated()) {
            return true;
        }

        return false;
    }

    public boolean demolishBuilding(GameSession gameSession, Builder builder, HexCoordinate targetCoordinate) {
        if (!canDemolishBuilding(gameSession, builder, targetCoordinate)) {
            return false;
        }

        Building building = gameSession.getBuildingAt(targetCoordinate);

        builder.consumeActionPoints(Constants.DEMOLITION_AP_COST);
        building.clearAllStationedWorkers();
        building.destroy();
        gameSession.removeBuildingAt(targetCoordinate);
        gameSession.getEventBus().publish(new BuildingDestroyedEvent(building));

        return true;
    }

    public boolean canDemolishRoad(GameSession gameSession, Builder builder, HexCoordinate targetCoordinate) {
        if (!isBuilderNear(builder, targetCoordinate)) {
            return false;
        }

        HexTile tile = gameSession.getHexMap().getTile(targetCoordinate);
        if (tile == null || !tile.isRoadPresent()) {
            return false;
        }
        if (!builder.hasEnoughActionPoints(Constants.DEMOLITION_AP_COST)) {
            return false;
        }

        return true;
    }

    public boolean demolishRoad(GameSession gameSession, Builder builder, HexCoordinate targetCoordinate) {
        if (!canDemolishRoad(gameSession, builder, targetCoordinate)) {
            return false;
        }

        builder.consumeActionPoints(Constants.DEMOLITION_AP_COST);
        gameSession.getHexMap().getTile(targetCoordinate).setRoadPresent(false);

        return true;
    }

    public boolean canDemolishWall(GameSession gameSession, Builder builder, HexCoordinate fromCoordinate, HexCoordinate toCoordinate) {
        boolean builderOnEdge = builder.getCoordinate().equals(fromCoordinate) || builder.getCoordinate().equals(toCoordinate);
        if (!builderOnEdge) {
            return false;
        }
        if (!gameSession.getHexMap().hasActiveWall(fromCoordinate, toCoordinate)) {
            return false;
        }
        if (!builder.hasEnoughActionPoints(Constants.DEMOLITION_AP_COST)) {
            return false;
        }

        return true;
    }

    public boolean demolishWall(GameSession gameSession, Builder builder, HexCoordinate fromCoordinate, HexCoordinate toCoordinate) {
        if (!canDemolishWall(gameSession, builder, fromCoordinate, toCoordinate)) {
            return false;
        }

        builder.consumeActionPoints(Constants.DEMOLITION_AP_COST);
        HexEdgeFeatures edgeFeatures = gameSession.getHexMap().getEdgeFeatures(fromCoordinate, toCoordinate);
        if (edgeFeatures != null) {
            edgeFeatures.setWall(null);
        }

        return true;
    }

    private boolean isBuilderNear(Builder builder, HexCoordinate targetCoordinate) {
        if (builder.getCoordinate().equals(targetCoordinate)) {
            return true;
        }
        List<HexCoordinate> neighbors = builder.getCoordinate().getNeighbors();
        return neighbors.contains(targetCoordinate);
    }
}