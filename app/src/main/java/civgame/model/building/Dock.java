package civgame.model.building;

import civgame.model.map.HexCoordinate;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;

public class Dock extends Building {

    public Dock(String id, HexCoordinate coordinate) {
        super(id, coordinate, Constants.DOCK_WORKER_CAPACITY);
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