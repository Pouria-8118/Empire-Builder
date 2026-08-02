package civgame.model.event;

import civgame.model.building.Building;

public class BuildingDestroyedEvent implements GameEvent {

    private final Building building;

    public BuildingDestroyedEvent(Building building) {
        this.building = building;
    }

    public Building getBuilding() {
        return building;
    }
}