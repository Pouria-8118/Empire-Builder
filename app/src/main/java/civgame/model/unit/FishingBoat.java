package civgame.model.unit;

import civgame.model.map.HexCoordinate;
import civgame.util.Constants;

public class FishingBoat extends Unit {

    public FishingBoat(String id, HexCoordinate coordinate) {
        super(id, coordinate, Constants.FISHING_BOAT_MAX_ACTION_POINTS, Constants.FISHING_BOAT_VISION_RADIUS);
    }

    @Override
    public UnitType getUnitType() {
        return UnitType.FISHING_BOAT;
    }
}