package civgame.model.building;

import civgame.model.map.HexCoordinate;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;

public class LumberMill extends Building {

    public LumberMill(String id, HexCoordinate coordinate) {
        super(id, coordinate, Constants.LUMBER_MILL_WORKER_CAPACITY);
    }

    @Override
    public BuildingType getBuildingType() {
        return BuildingType.LUMBER_MILL;
    }

    @Override
    public ResourceType getResourceProduced() {
        return ResourceType.WOOD;
    }

    @Override
    public int getBaseProductionPerWorker() {
        return Constants.LUMBER_MILL_PRODUCTION_PER_WORKER;
    }
}