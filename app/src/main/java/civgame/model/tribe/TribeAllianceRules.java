package civgame.model.tribe;

import java.util.List;

public class TribeAllianceRules {

    private TribeAllianceRules() {
    }

    public static boolean canFormAlliance(List<Tribe> allTribes, Tribe targetTribe) {
        boolean warriorAllied = false;
        boolean farmerAllied = false;
        boolean mountainAllied = false;
        boolean anyAllied = false;

        for (int i = 0; i < allTribes.size(); i++) {
            Tribe tribe = allTribes.get(i);
            if (!tribe.isAllied()) {
                continue;
            }
            anyAllied = true;
            if (tribe.getTribeType() == TribeType.WARRIOR) {
                warriorAllied = true;
            }
            if (tribe.getTribeType() == TribeType.FARMER) {
                farmerAllied = true;
            }
            if (tribe.getTribeType() == TribeType.MOUNTAIN) {
                mountainAllied = true;
            }
        }

        if (warriorAllied) {
            return false;
        }
        if (targetTribe.getTribeType() == TribeType.WARRIOR) {
            return !anyAllied;
        }
        if (targetTribe.getTribeType() == TribeType.FARMER && mountainAllied) {
            return false;
        }
        if (targetTribe.getTribeType() == TribeType.MOUNTAIN && farmerAllied) {
            return false;
        }

        return true;
    }
}