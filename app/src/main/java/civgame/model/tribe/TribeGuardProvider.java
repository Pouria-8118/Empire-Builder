package civgame.model.tribe;

import civgame.model.unit.UnitType;
import civgame.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class TribeGuardProvider {

    private TribeGuardProvider() {
    }

    public static List<TribeGuardUnit> createDefaultGuards(TribeType tribeType) {
        List<TribeGuardUnit> guards = new ArrayList<TribeGuardUnit>();

        if (tribeType == TribeType.WARRIOR) {
            guards.add(new TribeGuardUnit(UnitType.SWORDSMAN, Constants.TRIBE_GUARD_SWORDSMAN_HIT_POINTS));
            guards.add(new TribeGuardUnit(UnitType.ARCHER, Constants.TRIBE_GUARD_ARCHER_HIT_POINTS));
        } else {
            guards.add(new TribeGuardUnit(UnitType.SWORDSMAN, Constants.TRIBE_GUARD_SWORDSMAN_HIT_POINTS));
        }

        return guards;
    }
}