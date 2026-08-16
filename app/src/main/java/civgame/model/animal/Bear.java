package civgame.model.animal;

import civgame.model.map.HexCoordinate;
import civgame.util.Constants;

public class Bear extends WildAnimal {

    public Bear(String id, HexCoordinate coordinate, HexCoordinate denCoordinate) {
        super(id, coordinate, denCoordinate, Constants.BEAR_MAX_HIT_POINTS, Constants.BEAR_ATTACK_POWER,
                Constants.BEAR_MAX_ACTION_POINTS, Constants.BEAR_ATTACK_RANGE);
    }
}