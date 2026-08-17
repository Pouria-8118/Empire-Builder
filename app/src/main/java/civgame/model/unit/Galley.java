package civgame.model.unit;

import civgame.model.map.HexCoordinate;
import civgame.util.Constants;

public class Galley extends MilitaryUnit {

    public Galley(String id, HexCoordinate coordinate) {
        super(id, coordinate, Constants.GALLEY_MAX_ACTION_POINTS, Constants.GALLEY_VISION_RADIUS,
                Constants.GALLEY_MAX_HIT_POINTS, Constants.GALLEY_STRUCTURE_ATTACK_POWER, Constants.GALLEY_ATTACK_RANGE);
    }

    @Override
    public UnitType getUnitType() {
        return UnitType.GALLEY;
    }
}