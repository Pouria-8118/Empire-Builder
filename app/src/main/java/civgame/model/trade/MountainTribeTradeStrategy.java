package civgame.model.trade;

import civgame.model.resource.ResourceType;
import civgame.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class MountainTribeTradeStrategy implements TribeTradeStrategy {

    @Override
    public double getRate() {
        return Constants.MOUNTAIN_TRIBE_TRADE_RATE;
    }

    @Override
    public List<ResourceType> getAllowedRequestedTypes() {
        List<ResourceType> allowed = new ArrayList<ResourceType>();
        allowed.add(ResourceType.STONE);
        allowed.add(ResourceType.IRON);
        return allowed;
    }
}