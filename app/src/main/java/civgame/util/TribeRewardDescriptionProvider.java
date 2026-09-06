package civgame.util;

import civgame.model.tribe.TribeType;

public class TribeRewardDescriptionProvider {

    private TribeRewardDescriptionProvider() {
    }

    public static String describe(TribeType tribeType) {
        if (tribeType == TribeType.FARMER) {
            return "Friendly: trade for food. Allied: permanent food production bonus.";
        }
        if (tribeType == TribeType.WARRIOR) {
            return "Allied: a free military unit and a combat bonus near their camp.";
        }
        if (tribeType == TribeType.TRADER) {
            return "Friendly: trade at a better rate for any resource.";
        }
        if (tribeType == TribeType.MOUNTAIN) {
            return "Friendly: trade for stone or iron. Allied: faster mining nearby.";
        }
        return "Friendly: trade for food. Allied: cheaper Dock construction.";
    }
}