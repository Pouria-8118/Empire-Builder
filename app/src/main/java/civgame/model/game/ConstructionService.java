package civgame.model.game;

import java.util.List;

import civgame.model.building.Building;
import civgame.model.building.BuildingCostProvider;
import civgame.model.building.BuildingFactory;
import civgame.model.building.BuildingPlacementValidator;
import civgame.model.building.BuildingType;
import civgame.model.building.TownHallLevel;
import civgame.model.building.TownHallUpgradeType;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexTile;
import civgame.model.resource.ResourceType;
import civgame.model.tribe.Tribe;
import civgame.model.unit.Builder;
import civgame.util.Constants;
import civgame.model.event.BuildingPlacedEvent;
public class ConstructionService {

    private final BuildingFactory buildingFactory;

    public ConstructionService() {
        this.buildingFactory = new BuildingFactory();
    }

    public boolean canBuild(GameSession gameSession, Builder builder, BuildingType buildingType, HexCoordinate targetCoordinate) {
        if (builder.isOutOfCharges()) {
            return false;
        }
        if (!builder.getCoordinate().equals(targetCoordinate)) {
            return false;
        }
        if (gameSession.getBuildingAt(targetCoordinate) != null) {
            return false;
        }
        if (!gameSession.isWithinBorder(targetCoordinate)) {
            return false;
        }
        if (isWithinAnyTribeTerritory(gameSession, targetCoordinate)) {
            return false;
        }
        if (!isTechnologyUnlockedFor(gameSession, buildingType)) {
            return false;
        }

        HexTile targetTile = gameSession.getHexMap().getTile(targetCoordinate);
        if (targetTile == null) {
            return false;
        }
        if (!BuildingPlacementValidator.isTerrainValid(buildingType, targetTile, gameSession.getHexMap())) {
            return false;
        }

        int actionPointCost = BuildingCostProvider.getConstructionActionPointCost(buildingType);
        if (!builder.hasEnoughActionPoints(actionPointCost)) {
            return false;
        }

        int woodCost = getEffectiveWoodCost(gameSession, buildingType);
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.WOOD, woodCost)) {
            return false;
        }

        int stoneCost = BuildingCostProvider.getStoneCost(buildingType);
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.STONE, stoneCost)) {
            return false;
        }

        return true;
    }

    private boolean isTechnologyUnlockedFor(GameSession gameSession, BuildingType buildingType) {
        if (buildingType == BuildingType.STONE_MINE) {
            return gameSession.getTownHallUpgradeService().isUnlocked(TownHallUpgradeType.STONE_MINE_TECH);
        }
        if (buildingType == BuildingType.IRON_MINE) {
            return gameSession.getTownHallUpgradeService().isUnlocked(TownHallUpgradeType.IRON_MINE_TECH);
        }
        if (buildingType == BuildingType.DOCK) {
            return gameSession.getTownHallLevelService().isAtLeastLevel(gameSession, TownHallLevel.SETTLEMENT);
        }
        if (buildingType == BuildingType.BAZAAR) {
            return gameSession.getTownHallLevelService().isAtLeastLevel(gameSession, TownHallLevel.SETTLEMENT);
        }
        return true;
    }

    public boolean build(GameSession gameSession, Builder builder, BuildingType buildingType, HexCoordinate targetCoordinate) {
        if (!canBuild(gameSession, builder, buildingType, targetCoordinate)) {
            return false;
        }

        int actionPointCost = BuildingCostProvider.getConstructionActionPointCost(buildingType);
        int woodCost = getEffectiveWoodCost(gameSession, buildingType);
        int stoneCost = BuildingCostProvider.getStoneCost(buildingType);

        builder.consumeActionPoints(actionPointCost);
        gameSession.getResourceStorage().removeResource(ResourceType.WOOD, woodCost);
        gameSession.getResourceStorage().removeResource(ResourceType.STONE, stoneCost);

        if (buildingType == BuildingType.DOCK && gameSession.hasPendingDockDiscount()) {
            gameSession.setPendingDockDiscount(false);
        }

        Building newBuilding = buildingFactory.createBuilding(buildingType, targetCoordinate);
        gameSession.addBuilding(newBuilding);
        gameSession.getEventBus().publish(new BuildingPlacedEvent(newBuilding));

        builder.consumeCharge();
        if (builder.isOutOfCharges()) {
            gameSession.removeUnit(builder);
        }

        return true;
    }
    
    private boolean isWithinAnyTribeTerritory(GameSession gameSession, HexCoordinate coordinate) {
        List<Tribe> tribes = gameSession.getTribes();
        for (int i = 0; i < tribes.size(); i++) {
            Tribe tribe = tribes.get(i);
            if (tribe.isAllied()) {
                continue;
            }
            if (tribe.getCampCoordinate().equals(coordinate) || tribe.getCampCoordinate().getNeighbors().contains(coordinate)) {
                return true;
            }
        }
        return false;
    }

    private int getEffectiveWoodCost(GameSession gameSession, BuildingType buildingType) {
        int baseCost = BuildingCostProvider.getWoodCost(buildingType);
        if (buildingType == BuildingType.DOCK && gameSession.hasPendingDockDiscount()) {
            int discounted = baseCost - Constants.COASTAL_MISSION_DOCK_DISCOUNT;
            return Math.max(0, discounted);
        }
        return baseCost;
    }
    
}