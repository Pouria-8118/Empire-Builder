package civgame.model.building;

import civgame.model.map.HexCoordinate;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;

public class Monument extends Building {

    public Monument(String id, HexCoordinate coordinate) {
        super(id, coordinate, Constants.MONUMENT_WORKER_CAPACITY);
    }

    @Override
    public BuildingType getBuildingType() {
        return BuildingType.MONUMENT;
    }

    @Override
    public ResourceType getResourceProduced() {
        return ResourceType.FOOD;
    }

    @Override
    public int getBaseProductionPerWorker() {
        return 0;
    }
}