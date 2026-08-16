package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.building.BuildingType;
import civgame.model.building.Dock;
import civgame.model.map.HexTile;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class ResourceProductionService {

    public List<String> applyProduction(GameSession gameSession) {
        List<String> depletedDescriptions = applyBuildingProduction(gameSession);
        applyFarmSynergyBonus(gameSession);
        applyTownHallSafeguardProduction(gameSession);
        return depletedDescriptions;
    }

    private List<String> applyBuildingProduction(GameSession gameSession) {
        List<String> depletedDescriptions = new ArrayList<String>();
        List<Building> allBuildings = gameSession.getAllBuildings();
        HappinessLevel happinessLevel = gameSession.getHappinessService().getCurrentLevel();
        SeasonType currentSeason = gameSession.getCurrentSeason();

        for (int i = 0; i < allBuildings.size(); i++) {
            Building building = allBuildings.get(i);
            if (building.isDestroyed()) {
                continue;
            }

            HexTile resourceTile = resolveResourceTile(gameSession, building);
            if (resourceTile == null || !resourceTile.hasResource()) {
                continue;
            }

            int baseProduced = applyWorkerStrikePenalty(building, happinessLevel);

            double multiplier = gameSession.getTownHallUpgradeService().getProductionMultiplier(building.getBuildingType());
            multiplier = multiplier * getHappinessProductionMultiplier(happinessLevel);

            int producedAmount = (int) (baseProduced * multiplier);
            if (producedAmount <= 0) {
                continue;
            }

            int seasonAdjustedAmount = applySeasonalAdjustment(building, currentSeason, producedAmount);
            int adjacencyBonus = gameSession.getAdjacencyBonusService().getBuildingBonus(building);
            int totalProducedAmount = seasonAdjustedAmount + adjacencyBonus;

            gameSession.getResourceStorage().addResource(building.getResourceProduced(), totalProducedAmount);
            resourceTile.consumeResourceCapacity(producedAmount);

            if (!resourceTile.hasResource()) {
                building.clearAllStationedWorkers();
                depletedDescriptions.add(building.getBuildingType().name() + " ran out of resources");
            }
        }

        return depletedDescriptions;
    }

    private int applySeasonalAdjustment(Building building, SeasonType season, int producedAmount) {
        BuildingType buildingType = building.getBuildingType();
        boolean isFarmOrStable = buildingType == BuildingType.FARM || buildingType == BuildingType.STABLE;

        if (season == SeasonType.SPRING && isFarmOrStable) {
            return producedAmount + Constants.SPRING_FARM_STABLE_BONUS;
        }
        if (season == SeasonType.WINTER && buildingType == BuildingType.FARM) {
            int adjusted = producedAmount - Constants.WINTER_FARM_PENALTY;
            return Math.max(0, adjusted);
        }

        return producedAmount;
    }

    private int applyWorkerStrikePenalty(Building building, HappinessLevel happinessLevel) {
        int baseProduced = building.calculateProduction();

        if (happinessLevel == HappinessLevel.DISCONTENT || happinessLevel == HappinessLevel.REBELLION) {
            int strikePenalty = building.getStationedWorkers().size() * Constants.WORKER_STRIKE_PRODUCTION_PENALTY;
            baseProduced = baseProduced - strikePenalty;
            if (baseProduced < 0) {
                baseProduced = 0;
            }
        }

        return baseProduced;
    }

    private double getHappinessProductionMultiplier(HappinessLevel happinessLevel) {
        if (happinessLevel == HappinessLevel.GOLDEN_AGE) {
            return Constants.GOLDEN_AGE_PRODUCTION_MULTIPLIER;
        }
        return 1.0;
    }

    private HexTile resolveResourceTile(GameSession gameSession, Building building) {
        if (building instanceof Dock) {
            return DockResourceFinder.findAdjacentFishTile(gameSession, building.getCoordinate());
        }
        return gameSession.getHexMap().getTile(building.getCoordinate());
    }

    private void applyFarmSynergyBonus(GameSession gameSession) {
        int bonus = gameSession.getAdjacencyBonusService().getFarmSynergyBonus();
        if (bonus > 0) {
            gameSession.getResourceStorage().addResource(ResourceType.FOOD, bonus);
        }
    }

    private void applyTownHallSafeguardProduction(GameSession gameSession) {
        gameSession.getResourceStorage().addResource(ResourceType.FOOD, Constants.TOWN_HALL_SAFEGUARD_FOOD);
        gameSession.getResourceStorage().addResource(ResourceType.WOOD, Constants.TOWN_HALL_SAFEGUARD_WOOD);
    }
}