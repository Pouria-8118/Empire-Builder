package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.unit.Worker;
import civgame.util.Constants;

import java.util.List;

public class StationingService {

    public boolean isWorkerStationedAnywhere(GameSession gameSession, Worker worker) {
        return findBuildingContainingWorker(gameSession, worker) != null;
    }

    public Building findBuildingContainingWorker(GameSession gameSession, Worker worker) {
        List<Building> allBuildings = gameSession.getAllBuildings();

        for (int i = 0; i < allBuildings.size(); i++) {
            Building building = allBuildings.get(i);
            if (building.isWorkerStationed(worker)) {
                return building;
            }
        }

        return null;
    }

    public boolean canStation(Worker worker, Building building) {
        if (building.isFull()) {
            return false;
        }
        if (building.isWorkerStationed(worker)) {
            return false;
        }
        if (!worker.getCoordinate().equals(building.getCoordinate())) {
            return false;
        }
        return worker.hasEnoughActionPoints(Constants.WORKER_STATION_AP_COST);
    }

    public boolean station(Worker worker, Building building) {
        if (!canStation(worker, building)) {
            return false;
        }

        worker.consumeActionPoints(Constants.WORKER_STATION_AP_COST);
        building.stationWorker(worker);
        return true;
    }

    public void unstation(Worker worker, Building building) {
        building.unstationWorker(worker);
    }

    // TODO : when a building's resource tile becomes depleted,
    // automatically unstation all its workers
}