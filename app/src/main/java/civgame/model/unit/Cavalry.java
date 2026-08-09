package civgame.model.unit;

import civgame.model.map.HexCoordinate;
import civgame.util.Constants;

public class Cavalry extends MilitaryUnit {

    public Cavalry(String id, HexCoordinate coordinate) {
        super(id, coordinate, Constants.CAVALRY_MAX_ACTION_POINTS, Constants.CAVALRY_VISION_RADIUS,
                Constants.CAVALRY_MAX_HIT_POINTS, Constants.CAVALRY_STRUCTURE_ATTACK_POWER, Constants.CAVALRY_ATTACK_RANGE);
    }

    @Override
    public UnitType getUnitType() {
        return UnitType.CAVALRY;
    }
}