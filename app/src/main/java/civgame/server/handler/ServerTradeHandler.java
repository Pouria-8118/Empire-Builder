package civgame.server.handler;

import civgame.network.message.Message;

import civgame.network.message.item.ItemProduceRequest;
import civgame.network.message.item.ItemUseRequest;
import civgame.network.message.trade.TradeAcceptRequest;
import civgame.network.message.trade.TradeCancelRequest;
import civgame.network.message.trade.TradeOfferRequest;
import civgame.network.message.trade.TradeRejectRequest;
import civgame.server.game.MultiplayerGameState;
import civgame.server.game.MultiplayerGameSessionManager;
import civgame.server.game.PlayerState;

public class ServerTradeHandler {

    private final TradeMessageHandler tradeHandler;
    private final ItemMessageHandler itemHandler;
    private final MultiplayerGameSessionManager sessionManager;
    private final MessageBroadcaster broadcaster;

    public ServerTradeHandler(MultiplayerGameSessionManager sessionManager,
                              MessageBroadcaster broadcaster) {
        this.sessionManager = sessionManager;
        this.broadcaster = broadcaster;
        this.tradeHandler = new TradeMessageHandler();
        this.itemHandler = new ItemMessageHandler();
    }

    public boolean handleMessage(String clientId, Message message) {
        MultiplayerGameState game = sessionManager.getGameForClient(clientId);
        if (game == null) {
            return false;
        }
        PlayerState sender = game.getPlayer(clientId);
        if (sender == null || !sender.isActive()) {
            return false;
        }

        if (message instanceof TradeOfferRequest) {
            tradeHandler.handleTradeOffer(game, sender, (TradeOfferRequest) message, broadcaster);
            return true;
        }
        if (message instanceof TradeAcceptRequest) {
            tradeHandler.handleTradeAccept(game, sender, (TradeAcceptRequest) message, broadcaster);
            return true;
        }
        if (message instanceof TradeRejectRequest) {
            tradeHandler.handleTradeReject(game, sender, (TradeRejectRequest) message, broadcaster);
            return true;
        }
        if (message instanceof TradeCancelRequest) {
            tradeHandler.handleTradeCancel(game, sender, (TradeCancelRequest) message, broadcaster);
            return true;
        }
        if (message instanceof ItemProduceRequest) {
            itemHandler.handleItemProduce(game, sender, (ItemProduceRequest) message, broadcaster);
            return true;
        }
        if (message instanceof ItemUseRequest) {
            itemHandler.handleItemUse(game, sender, (ItemUseRequest) message, broadcaster);
            return true;
        }
        return false;
    }
}