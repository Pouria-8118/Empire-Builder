package civgame.model.trade;

public class BazaarTradeStrategyProvider {

    private BazaarTradeStrategyProvider() {
    }

    public static TradeStrategy getStrategy(BazaarTradeTier tier) {
        if (tier == BazaarTradeTier.TIER_1) {
            return new BazaarTierOneTradeStrategy();
        }
        if (tier == BazaarTradeTier.TIER_2) {
            return new BazaarTierTwoTradeStrategy();
        }
        return new BazaarTierThreeTradeStrategy();
    }
}