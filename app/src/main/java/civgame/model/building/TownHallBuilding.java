package civgame.model.building;

import civgame.model.map.HexCoordinate;
import civgame.model.resource.ResourceType;

public class TownHallBuilding extends Building {

    public TownHallBuilding(String id, HexCoordinate coordinate) {
        super(id, coordinate, 0);
    }

    @Override
    public int getBaseProductionPerWorker() {
        return 0;
    }

    @Override
    public BuildingType getBuildingType() {
        return BuildingType.TOWN_HALL;
    }

    @Override
    public ResourceType getResourceProduced() {
        return null;
    }
}