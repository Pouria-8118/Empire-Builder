package civgame.model.building;

import civgame.model.map.HexCoordinate;
import civgame.model.resource.ResourceType;

public class Apothecary extends Building {

    private static final long serialVersionUID = 1L;

    public Apothecary(String id, HexCoordinate coordinate) {
        super(id, coordinate, 0);
    }

    @Override
    public BuildingType getBuildingType() {
        return BuildingType.APOTHECARY;
    }

    @Override
    public ResourceType getResourceProduced() {
        return null;
    }

    @Override
    public int getBaseProductionPerWorker() {
        return 0;
    }
}