package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.map.HexEdge;
import civgame.model.map.Wall;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;
import civgame.model.event.BuildingDestroyedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpkeepService {

    public List<String> applyBuildingUpkeep(GameSession gameSession) {
        List<String> destroyedBuildingIds = new ArrayList<String>();
        List<Building> allBuildings = gameSession.getAllBuildings();

        for (int i = 0; i < allBuildings.size(); i++) {
            Building building = allBuildings.get(i);
            if (building.isDestroyed()) {
                continue;
            }

            boolean upkeepPaid = gameSession.getResourceStorage().removeResource(
                    ResourceType.WOOD, Constants.BUILDING_UPKEEP_WOOD_AMOUNT);

            if (upkeepPaid) {
                building.resetMissedUpkeep();
                continue;
            }

            building.registerMissedUpkeep();
            if (building.getMissedUpkeepTurns() >= Constants.MAX_MISSED_UPKEEP_TURNS_BEFORE_COLLAPSE) {
                collapseBuilding(gameSession, building);
                destroyedBuildingIds.add(building.getId());
            }
        }

        return destroyedBuildingIds;
    }

    private void collapseBuilding(GameSession gameSession, Building building) {
        building.clearAllStationedWorkers();
        building.destroy();
        gameSession.removeBuildingAt(building.getCoordinate());
        gameSession.getEventBus().publish(new BuildingDestroyedEvent(building));
    }

    public List<HexEdge> applyWallUpkeep(GameSession gameSession) {
        List<HexEdge> destroyedWallEdges = new ArrayList<HexEdge>();
        Map<HexEdge, Wall> activeWalls = gameSession.getHexMap().getAllActiveWalls();

        for (Map.Entry<HexEdge, Wall> entry : activeWalls.entrySet()) {
            HexEdge edge = entry.getKey();
            Wall wall = entry.getValue();

            boolean upkeepPaid = gameSession.getResourceStorage().removeResource(ResourceType.WOOD, Constants.WALL_UPKEEP_WOOD_AMOUNT);

            if (upkeepPaid) {
                wall.resetMissedUpkeep();
                continue;
            }

            wall.registerMissedUpkeep();
            if (wall.getMissedUpkeepTurns() >= Constants.MAX_MISSED_UPKEEP_TURNS_BEFORE_COLLAPSE) {
                gameSession.getHexMap().removeWall(edge);
                destroyedWallEdges.add(edge);
            }
        }

        return destroyedWallEdges;
    }

    public boolean applyUnitFoodConsumption(GameSession gameSession) {
        int totalConsumption = gameSession.getUnits().size() * Constants.UNIT_FOOD_CONSUMPTION_PER_TURN;
        int shortfall = gameSession.getResourceStorage().consumeAsMuchAsPossible(ResourceType.FOOD, totalConsumption);
        return shortfall > 0;
    }

}