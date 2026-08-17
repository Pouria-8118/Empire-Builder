package civgame.model.building;

import civgame.model.map.HexCoordinate;
import civgame.model.resource.ResourceType;
import civgame.model.game.TownHallProductionQueue;
import civgame.util.Constants;

public class Dock extends Building {

    private final TownHallProductionQueue navalProductionQueue;

    public Dock(String id, HexCoordinate coordinate) {
        super(id, coordinate, Constants.DOCK_WORKER_CAPACITY);
        this.navalProductionQueue = new TownHallProductionQueue();
    }

    public TownHallProductionQueue getNavalProductionQueue() {
        return navalProductionQueue;
    }

    @Override
    public BuildingType getBuildingType() {
        return BuildingType.DOCK;
    }

    @Override
    public ResourceType getResourceProduced() {
        return ResourceType.FOOD;
    }

    @Override
    public int getBaseProductionPerWorker() {
        return Constants.DOCK_PRODUCTION_PER_WORKER;
    }
}