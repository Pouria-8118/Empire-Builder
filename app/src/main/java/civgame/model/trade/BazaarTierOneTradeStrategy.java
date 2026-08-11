package civgame.model.trade;

import civgame.util.Constants;

public class BazaarTierOneTradeStrategy implements TradeStrategy {

    @Override
    public int getRequiredOfferedAmount() {
        return Constants.BAZAAR_TIER_1_AMOUNT;
    }

    @Override
    public int calculateReceivedAmount(int offeredAmount) {
        return (int) (offeredAmount * Constants.BAZAAR_TIER_1_RATE);
    }
}