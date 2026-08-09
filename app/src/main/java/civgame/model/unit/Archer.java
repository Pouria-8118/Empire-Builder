package civgame.model.unit;

import civgame.model.map.HexCoordinate;
import civgame.util.Constants;

public class Archer extends MilitaryUnit {

    public Archer(String id, HexCoordinate coordinate) {
        super(id, coordinate, Constants.ARCHER_MAX_ACTION_POINTS, Constants.ARCHER_VISION_RADIUS,
                Constants.ARCHER_MAX_HIT_POINTS, Constants.ARCHER_STRUCTURE_ATTACK_POWER, Constants.ARCHER_ATTACK_RANGE);
    }

    @Override
    public UnitType getUnitType() {
        return UnitType.ARCHER;
    }
}