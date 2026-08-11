package civgame.model.game;

import civgame.model.resource.ResourceType;
import civgame.model.trade.BazaarTradeStrategyProvider;
import civgame.model.trade.BazaarTradeTier;
import civgame.model.trade.TradeStrategy;

public class BazaarTradeService {

    public boolean canTrade(GameSession gameSession, ResourceType offeredType, ResourceType requestedType, BazaarTradeTier tier) {
        if (offeredType == requestedType) {
            return false;
        }
        if (!gameSession.hasActiveBazaar()) {
            return false;
        }
        if (gameSession.hasBazaarTradedThisTurn()) {
            return false;
        }

        TradeStrategy strategy = BazaarTradeStrategyProvider.getStrategy(tier);
        int requiredAmount = strategy.getRequiredOfferedAmount();
        return gameSession.getResourceStorage().hasEnough(offeredType, requiredAmount);
    }

    public boolean trade(GameSession gameSession, ResourceType offeredType, ResourceType requestedType, BazaarTradeTier tier) {
        if (!canTrade(gameSession, offeredType, requestedType, tier)) {
            return false;
        }

        TradeStrategy strategy = BazaarTradeStrategyProvider.getStrategy(tier);
        int requiredAmount = strategy.getRequiredOfferedAmount();
        int receivedAmount = strategy.calculateReceivedAmount(requiredAmount);

        gameSession.getResourceStorage().removeResource(offeredType, requiredAmount);
        gameSession.getResourceStorage().addResource(requestedType, receivedAmount);
        gameSession.markBazaarTradedThisTurn();

        return true;
    }
}