package civgame.model.trade;

import civgame.model.resource.ResourceType;

import java.util.List;

public interface TribeTradeStrategy {

    double getRate();

    List<ResourceType> getAllowedRequestedTypes();
}