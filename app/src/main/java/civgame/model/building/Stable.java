package civgame.model.building;

import civgame.model.map.HexCoordinate;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;

public class Stable extends Building {

    public Stable(String id, HexCoordinate coordinate) {
        super(id, coordinate, Constants.STABLE_WORKER_CAPACITY);
    }

    @Override
    public BuildingType getBuildingType() {
        return BuildingType.STABLE;
    }

    @Override
    public ResourceType getResourceProduced() {
        return ResourceType.FOOD;
    }

    @Override
    public int getBaseProductionPerWorker() {
        return Constants.STABLE_PRODUCTION_PER_WORKER;
    }
}