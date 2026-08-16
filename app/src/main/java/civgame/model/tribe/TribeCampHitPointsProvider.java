package civgame.model.tribe;

import civgame.util.Constants;

public class TribeCampHitPointsProvider {

    private TribeCampHitPointsProvider() {
    }

    public static int getMaxHitPoints(TribeType tribeType) {
        if (tribeType == TribeType.FARMER) {
            return Constants.TRIBE_CAMP_FARMER_MAX_HIT_POINTS;
        }
        if (tribeType == TribeType.WARRIOR) {
            return Constants.TRIBE_CAMP_WARRIOR_MAX_HIT_POINTS;
        }
        return Constants.TRIBE_CAMP_DEFAULT_MAX_HIT_POINTS;
    }
}