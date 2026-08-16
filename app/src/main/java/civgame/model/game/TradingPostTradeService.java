package civgame.model.game;

import civgame.model.map.TradingPost;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;

public class TradingPostTradeService {

    public boolean canTrade(GameSession gameSession, TradingPost tradingPost, ResourceType offeredType, ResourceType requestedType, int offeredAmount) {
        if (offeredType == requestedType) {
            return false;
        }
        if (offeredAmount <= 0) {
            return false;
        }
        if (!gameSession.isWithinBorder(tradingPost.getCoordinate())) {
            return false;
        }
        if (gameSession.hasTradingPostTradedThisTurn()) {
            return false;
        }
        return gameSession.getResourceStorage().hasEnough(offeredType, offeredAmount);
    }

    public boolean trade(GameSession gameSession, TradingPost tradingPost, ResourceType offeredType, ResourceType requestedType, int offeredAmount) {
        if (!canTrade(gameSession, tradingPost, offeredType, requestedType, offeredAmount)) {
            return false;
        }

        int receivedAmount = (int) (offeredAmount * Constants.TRADING_POST_RATE);

        gameSession.getResourceStorage().removeResource(offeredType, offeredAmount);
        gameSession.getResourceStorage().addResource(requestedType, receivedAmount);
        gameSession.markTradingPostTradedThisTurn();

        return true;
    }
}