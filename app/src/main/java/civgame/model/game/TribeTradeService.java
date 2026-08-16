package civgame.model.game;

import civgame.model.resource.ResourceType;
import civgame.model.tribe.Tribe;
import civgame.model.trade.TribeTradeStrategy;
import civgame.model.trade.TribeTradeStrategyProvider;
import civgame.util.Constants;

public class TribeTradeService {

    public boolean canTrade(GameSession gameSession, Tribe tribe, ResourceType offeredType, ResourceType requestedType, int offeredAmount) {
        if (offeredType == requestedType) {
            return false;
        }
        if (offeredAmount <= 0) {
            return false;
        }
        if (!tribe.getRelationState().isTradeAllowed()) {
            return false;
        }
        if (tribe.getRelationshipValue() < Constants.TRIBE_TRADE_MIN_RELATIONSHIP) {
            return false;
        }
        if (tribe.hasTradedThisTurn()) {
            return false;
        }

        TribeTradeStrategy strategy = TribeTradeStrategyProvider.getStrategy(tribe.getTribeType());
        if (strategy == null) {
            return false;
        }
        if (!strategy.getAllowedRequestedTypes().contains(requestedType)) {
            return false;
        }

        return gameSession.getResourceStorage().hasEnough(offeredType, offeredAmount);
    }

    public boolean trade(GameSession gameSession, Tribe tribe, ResourceType offeredType, ResourceType requestedType, int offeredAmount) {
        if (!canTrade(gameSession, tribe, offeredType, requestedType, offeredAmount)) {
            return false;
        }

        TribeTradeStrategy strategy = TribeTradeStrategyProvider.getStrategy(tribe.getTribeType());
        int receivedAmount = (int) (offeredAmount * strategy.getRate());

        gameSession.getResourceStorage().removeResource(offeredType, offeredAmount);
        gameSession.getResourceStorage().addResource(requestedType, receivedAmount);
        tribe.setTradedThisTurn(true);

        return true;
    }
}