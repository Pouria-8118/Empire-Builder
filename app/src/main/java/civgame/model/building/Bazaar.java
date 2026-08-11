package civgame.model.building;

import civgame.model.map.HexCoordinate;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;

public class Bazaar extends Building {

    public Bazaar(String id, HexCoordinate coordinate) {
        super(id, coordinate, Constants.BAZAAR_WORKER_CAPACITY);
    }

    @Override
    public BuildingType getBuildingType() {
        return BuildingType.BAZAAR;
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