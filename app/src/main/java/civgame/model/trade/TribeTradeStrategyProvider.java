package civgame.model.trade;

import civgame.model.tribe.TribeType;

public class TribeTradeStrategyProvider {

    private TribeTradeStrategyProvider() {
    }

    public static TribeTradeStrategy getStrategy(TribeType tribeType) {
        if (tribeType == TribeType.FARMER) {
            return new FarmerTribeTradeStrategy();
        }
        if (tribeType == TribeType.MOUNTAIN) {
            return new MountainTribeTradeStrategy();
        }
        if (tribeType == TribeType.TRADER) {
            return new TraderTribeTradeStrategy();
        }
        if (tribeType == TribeType.COASTAL) {
            return new CoastalTribeTradeStrategy();
        }
        return null;
    }
}