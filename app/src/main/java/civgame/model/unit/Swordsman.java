package civgame.model.unit;

import civgame.model.map.HexCoordinate;
import civgame.util.Constants;

public class Swordsman extends MilitaryUnit {

    public Swordsman(String id, HexCoordinate coordinate) {
        super(id, coordinate, Constants.SWORDSMAN_MAX_ACTION_POINTS, Constants.SWORDSMAN_VISION_RADIUS,
                Constants.SWORDSMAN_MAX_HIT_POINTS, Constants.SWORDSMAN_STRUCTURE_ATTACK_POWER, Constants.SWORDSMAN_ATTACK_RANGE);
    }

    @Override
    public UnitType getUnitType() {
        return UnitType.SWORDSMAN;
    }
}