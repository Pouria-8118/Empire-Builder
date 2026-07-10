package civgame.model.building;

import civgame.model.map.HexCoordinate;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;

public class Farm extends Building {

    public Farm(String id, HexCoordinate coordinate) {
        super(id, coordinate, Constants.FARM_WORKER_CAPACITY);
    }

    @Override
    public BuildingType getBuildingType() {
        return BuildingType.FARM;
    }

    @Override
    public ResourceType getResourceProduced() {
        return ResourceType.FOOD;
    }

    @Override
    public int getBaseProductionPerWorker() {
        return Constants.FARM_PRODUCTION_PER_WORKER;
    }
}