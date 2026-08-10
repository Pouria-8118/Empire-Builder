package civgame.model.unit;

import civgame.model.map.HexCoordinate;
import civgame.util.Constants;

public class Catapult extends MilitaryUnit {

    public Catapult(String id, HexCoordinate coordinate) {
        super(id, coordinate, Constants.CATAPULT_MAX_ACTION_POINTS, Constants.CATAPULT_VISION_RADIUS,
                Constants.CATAPULT_MAX_HIT_POINTS, Constants.CATAPULT_STRUCTURE_ATTACK_POWER, Constants.CATAPULT_ATTACK_RANGE);
    }

    @Override
    public UnitType getUnitType() {
        return UnitType.CATAPULT;
    }
}