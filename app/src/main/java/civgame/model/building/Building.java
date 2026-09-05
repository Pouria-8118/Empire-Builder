package civgame.model.building;

import civgame.model.combat.Attackable;
import civgame.model.map.HexCoordinate;
import civgame.model.resource.ResourceType;
import civgame.model.unit.Worker;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public abstract class Building implements Attackable, java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final LocalDateTime createdAt;
    private final HexCoordinate coordinate;
    private final int workerCapacity;
    private final List<Worker> stationedWorkers;
    private int missedUpkeepTurns;
    private boolean destroyed;
    private int currentHitPoints;
    private int maxHitPoints;
    private int productionStoppedUntilTurn;

    public Building(String id, HexCoordinate coordinate, int workerCapacity) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
        this.coordinate = coordinate;
        this.workerCapacity = workerCapacity;
        this.stationedWorkers = new ArrayList<Worker>();
        this.missedUpkeepTurns = 0;
        this.destroyed = false;
        this.maxHitPoints = BuildingHitPointsProvider.getMaxHitPoints(getBuildingType());
        this.currentHitPoints = maxHitPoints;
        this.productionStoppedUntilTurn = 0;
    }

    public abstract BuildingType getBuildingType();

    public abstract ResourceType getResourceProduced();

    public abstract int getBaseProductionPerWorker();

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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

    public int getProductionStoppedUntilTurn() {
        return productionStoppedUntilTurn;
    }

    public void setProductionStoppedUntilTurn(int productionStoppedUntilTurn) {
        this.productionStoppedUntilTurn = productionStoppedUntilTurn;
    }

    public boolean isProductionStopped(int currentTurn) {
        return productionStoppedUntilTurn >= currentTurn;
    }

    @Override
    public int getCurrentHitPoints() {
        return currentHitPoints;
    }

    @Override
    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    @Override
    public boolean isDestroyedByCombat() {
        return currentHitPoints <= 0;
    }

    @Override
    public void applyCombatDamage(int amount) {
        currentHitPoints = currentHitPoints - amount;
        if (currentHitPoints < 0) {
            currentHitPoints = 0;
        }

        if (currentHitPoints <= 0) {
            clearAllStationedWorkers();
            destroy();
        }
    }
}