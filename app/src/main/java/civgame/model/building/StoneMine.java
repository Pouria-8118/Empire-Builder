package civgame.model.building;

import civgame.model.map.HexCoordinate;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;

public class StoneMine extends Building {

    public StoneMine(String id, HexCoordinate coordinate) {
        super(id, coordinate, Constants.STONE_MINE_WORKER_CAPACITY);
    }

    @Override
    public BuildingType getBuildingType() {
        return BuildingType.STONE_MINE;
    }

    @Override
    public ResourceType getResourceProduced() {
        return ResourceType.STONE;
    }

    @Override
    public int getBaseProductionPerWorker() {
        return Constants.STONE_MINE_PRODUCTION_PER_WORKER;
    }
}