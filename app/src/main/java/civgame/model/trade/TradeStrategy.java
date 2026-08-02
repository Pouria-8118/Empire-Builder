package civgame.model.trade;

import civgame.model.resource.ResourceType;

public interface TradeStrategy {

    int calculateReceivedAmount(ResourceType offeredType, int offeredAmount, ResourceType requestedType);
}