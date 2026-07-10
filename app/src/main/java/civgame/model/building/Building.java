package civgame.model.building;

import civgame.model.map.HexCoordinate;
import civgame.model.resource.ResourceType;
import civgame.model.unit.Worker;

import java.util.ArrayList;
import java.util.List;

public abstract class Building {

    private final String id;
    private final HexCoordinate coordinate;
    private final int workerCapacity;
    private final List<Worker> stationedWorkers;
    private int missedUpkeepTurns;
    private boolean destroyed;

    public Building(String id, HexCoordinate coordinate, int workerCapacity) {
        this.id = id;
        this.coordinate = coordinate;
        this.workerCapacity = workerCapacity;
        this.stationedWorkers = new ArrayList<Worker>();
        this.missedUpkeepTurns = 0;
        this.destroyed = false;
    }

    public abstract BuildingType getBuildingType();

    public abstract ResourceType getResourceProduced();

    public abstract int getBaseProductionPerWorker();

    public String getId() {
        return id;
    }

    public HexCoordinate getCoordinate() {
        return coordinate;
    }

    public int getWorkerCapacity() {
        return workerCapacity;
    }

    public List<Worker> getStationedWorkers() {
        return stationedWorkers;
    }

    public boolean isFull() {
        return stationedWorkers.size() >= workerCapacity;
    }

    public boolean isWorkerStationed(Worker worker) {
        return stationedWorkers.contains(worker);
    }

    public void stationWorker(Worker worker) {
        stationedWorkers.add(worker);
    }

    public void unstationWorker(Worker worker) {
        stationedWorkers.remove(worker);
    }

    public int calculateProduction() {
        return stationedWorkers.size() * getBaseProductionPerWorker();
    }

    public int getMissedUpkeepTurns() {
        return missedUpkeepTurns;
    }

    public void registerMissedUpkeep() {
        missedUpkeepTurns = missedUpkeepTurns + 1;
    }

    public void resetMissedUpkeep() {
        missedUpkeepTurns = 0;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        this.destroyed = true;
    }

    public void clearAllStationedWorkers() {
        stationedWorkers.clear();
    }
    
    // TODO : apply the "professional tools" tech multiplier (1.5x) here
    // for StoneMine and IronMine production, once the tech system exists
}