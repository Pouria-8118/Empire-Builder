package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.map.HexTile;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class ResourceProductionService {

    public List<String> applyProduction(GameSession gameSession) {
        List<String> depletedDescriptions = applyBuildingProduction(gameSession);
        applyTownHallSafeguardProduction(gameSession);
        return depletedDescriptions;
    }

    private List<String> applyBuildingProduction(GameSession gameSession) {
        List<String> depletedDescriptions = new ArrayList<String>();
        List<Building> allBuildings = gameSession.getAllBuildings();

        for (int i = 0; i < allBuildings.size(); i++) {
            Building building = allBuildings.get(i);
            if (building.isDestroyed()) {
                continue;
            }

            HexTile tile = gameSession.getHexMap().getTile(building.getCoordinate());
            if (tile == null || !tile.hasResource()) {
                continue;
            }

            double multiplier = gameSession.getTownHallUpgradeService().getProductionMultiplier(building.getBuildingType());
            int producedAmount = (int) (building.calculateProduction() * multiplier);

            if (producedAmount <= 0) {
                continue;
            }

            gameSession.getResourceStorage().addResource(building.getResourceProduced(), producedAmount);
            tile.consumeResourceCapacity(producedAmount);

            if (!tile.hasResource()) {
                building.clearAllStationedWorkers();
                depletedDescriptions.add(building.getBuildingType().name() + " ran out of resources");
            }
        }

        return depletedDescriptions;
    }

    private void applyTownHallSafeguardProduction(GameSession gameSession) {
        gameSession.getResourceStorage().addResource(ResourceType.FOOD, Constants.TOWN_HALL_SAFEGUARD_FOOD);
        gameSession.getResourceStorage().addResource(ResourceType.WOOD, Constants.TOWN_HALL_SAFEGUARD_WOOD);
    }
}