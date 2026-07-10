package civgame.model.building;

import civgame.model.map.HexCoordinate;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;

public class IronMine extends Building {

    public IronMine(String id, HexCoordinate coordinate) {
        super(id, coordinate, Constants.IRON_MINE_WORKER_CAPACITY);
    }

    @Override
    public BuildingType getBuildingType() {
        return BuildingType.IRON_MINE;
    }

    @Override
    public ResourceType getResourceProduced() {
        return ResourceType.IRON;
    }

    @Override
    public int getBaseProductionPerWorker() {
        return Constants.IRON_MINE_PRODUCTION_PER_WORKER;
    }
}