package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;

import java.util.List;

public class ResourceProductionService {

    public void applyProduction(GameSession gameSession) {
        applyBuildingProduction(gameSession);
        applyTownHallSafeguardProduction(gameSession);
    }

    private void applyBuildingProduction(GameSession gameSession) {
        List<Building> allBuildings = gameSession.getAllBuildings();

        for (int i = 0; i < allBuildings.size(); i++) {
            Building building = allBuildings.get(i);
            if (building.isDestroyed()) {
                continue;
            }

            int producedAmount = building.calculateProduction();
            gameSession.getResourceStorage().addResource(building.getResourceProduced(), producedAmount);
        }
    }

    private void applyTownHallSafeguardProduction(GameSession gameSession) {
        gameSession.getResourceStorage().addResource(ResourceType.FOOD, Constants.TOWN_HALL_SAFEGUARD_FOOD);
        gameSession.getResourceStorage().addResource(ResourceType.WOOD, Constants.TOWN_HALL_SAFEGUARD_WOOD);
    }

    // TODO : advance the Town Hall production queue (unit/upgrade in progress) here as well
}