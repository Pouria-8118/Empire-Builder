package civgame.model.event;

import civgame.model.building.Building;

public class BuildingPlacedEvent implements GameEvent {

    private final Building building;

    public BuildingPlacedEvent(Building building) {
        this.building = building;
    }

    public Building getBuilding() {
        return building;
    }
}