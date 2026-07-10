package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceRateCalculator {

    public Map<ResourceType, Integer> calculateNetRates(GameSession gameSession) {
        Map<ResourceType, Integer> netRates = createZeroedRateMap();

        applyBuildingProductionRates(gameSession, netRates);
        applyTownHallSafeguardRates(netRates);
        applyBuildingUpkeepRates(gameSession, netRates);
        applyUnitFoodConsumptionRate(gameSession, netRates);

        return netRates;
    }

    private Map<ResourceType, Integer> createZeroedRateMap() {
        Map<ResourceType, Integer> rates = new HashMap<ResourceType, Integer>();
        rates.put(ResourceType.FOOD, 0);
        rates.put(ResourceType.WOOD, 0);
        rates.put(ResourceType.STONE, 0);
        rates.put(ResourceType.IRON, 0);
        return rates;
    }

    private void applyBuildingProductionRates(GameSession gameSession, Map<ResourceType, Integer> netRates) {
        List<Building> allBuildings = gameSession.getAllBuildings();

        for (int i = 0; i < allBuildings.size(); i++) {
            Building building = allBuildings.get(i);
            if (building.isDestroyed()) {
                continue;
            }

            ResourceType producedType = building.getResourceProduced();
            int currentValue = netRates.get(producedType);
            netRates.put(producedType, currentValue + building.calculateProduction());
        }
    }

    private void applyTownHallSafeguardRates(Map<ResourceType, Integer> netRates) {
        int currentFood = netRates.get(ResourceType.FOOD);
        netRates.put(ResourceType.FOOD, currentFood + Constants.TOWN_HALL_SAFEGUARD_FOOD);

        int currentWood = netRates.get(ResourceType.WOOD);
        netRates.put(ResourceType.WOOD, currentWood + Constants.TOWN_HALL_SAFEGUARD_WOOD);
    }

    private void applyBuildingUpkeepRates(GameSession gameSession, Map<ResourceType, Integer> netRates) {
        int activeBuildingCount = countActiveBuildings(gameSession);
        int totalUpkeep = activeBuildingCount * Constants.BUILDING_UPKEEP_WOOD_AMOUNT;

        int currentWood = netRates.get(ResourceType.WOOD);
        netRates.put(ResourceType.WOOD, currentWood - totalUpkeep);
    }

    private int countActiveBuildings(GameSession gameSession) {
        List<Building> allBuildings = gameSession.getAllBuildings();
        int count = 0;

        for (int i = 0; i < allBuildings.size(); i++) {
            if (!allBuildings.get(i).isDestroyed()) {
                count = count + 1;
            }
        }

        return count;
    }

    private void applyUnitFoodConsumptionRate(GameSession gameSession, Map<ResourceType, Integer> netRates) {
        int totalConsumption = gameSession.getUnits().size() * Constants.UNIT_FOOD_CONSUMPTION_PER_TURN;
        int currentFood = netRates.get(ResourceType.FOOD);
        netRates.put(ResourceType.FOOD, currentFood - totalConsumption);
    }

    // TODO : factor in Town Hall production queue upkeep once implemented
}