package civgame.model.trade;

import civgame.util.Constants;

public class BazaarTierTwoTradeStrategy implements TradeStrategy {

    @Override
    public int getRequiredOfferedAmount() {
        return Constants.BAZAAR_TIER_2_AMOUNT;
    }

    @Override
    public int calculateReceivedAmount(int offeredAmount) {
        return (int) (offeredAmount * Constants.BAZAAR_TIER_2_RATE);
    }
}