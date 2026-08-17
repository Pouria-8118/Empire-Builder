package civgame.model.game;

import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.resource.ResourceType;
import civgame.model.unit.FishingBoat;
import civgame.util.Constants;

public class FishingService {

    public boolean canHarvest(GameSession gameSession, FishingBoat boat) {
        if (!boat.hasEnoughActionPoints(Constants.FISHING_BOAT_HARVEST_AP_COST)) {
            return false;
        }
        HexTile tile = gameSession.getHexMap().getTile(boat.getCoordinate());
        return tile != null && tile.getTerrainType() == TerrainType.SEA && tile.hasResource();
    }

    public boolean harvest(GameSession gameSession, FishingBoat boat) {
        if (!canHarvest(gameSession, boat)) {
            return false;
        }

        HexTile tile = gameSession.getHexMap().getTile(boat.getCoordinate());
        boat.consumeActionPoints(Constants.FISHING_BOAT_HARVEST_AP_COST);
        gameSession.getResourceStorage().addResource(ResourceType.FOOD, Constants.FISHING_BOAT_HARVEST_AMOUNT);
        tile.consumeResourceCapacity(Constants.FISHING_BOAT_HARVEST_AMOUNT);

        return true;
    }
}