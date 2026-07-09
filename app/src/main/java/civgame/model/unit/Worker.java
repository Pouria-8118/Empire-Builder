package civgame.model.unit;

import civgame.model.map.HexCoordinate;

public class Worker extends Unit {

    public Worker(String id, HexCoordinate coordinate, int maxActionPoints, int visionRadius) {
        super(id, coordinate, maxActionPoints, visionRadius);
    }

    @Override
    public UnitType getUnitType() {
        return UnitType.WORKER;
    }

    // TODO : add a reference to the building this worker is currently stationed in
}