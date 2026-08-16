package civgame.model.trade;

import civgame.model.resource.ResourceType;
import civgame.util.Constants;

import java.util.Arrays;
import java.util.List;

public class TraderTribeTradeStrategy implements TribeTradeStrategy {

    @Override
    public double getRate() {
        return Constants.TRADER_TRIBE_TRADE_RATE;
    }

    @Override
    public List<ResourceType> getAllowedRequestedTypes() {
        return Arrays.asList(ResourceType.values());
    }
}