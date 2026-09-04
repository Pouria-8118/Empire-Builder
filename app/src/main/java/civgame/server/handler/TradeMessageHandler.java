package civgame.server.handler;

import civgame.model.resource.ResourceType;
import civgame.model.trade.TradeOffer;
import civgame.network.message.trade.TradeAcceptRequest;
import civgame.network.message.trade.TradeCancelRequest;
import civgame.network.message.trade.TradeInboxUpdateBroadcast;
import civgame.network.message.trade.TradeOfferBroadcast;
import civgame.network.message.trade.TradeOfferRequest;
import civgame.network.message.trade.TradeRejectRequest;
import civgame.server.game.MultiplayerGameState;
import civgame.server.game.PlayerState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeMessageHandler {

    public TradeMessageHandler() {
    }

    public void handleTradeOffer(MultiplayerGameState game, PlayerState sender,
                                 TradeOfferRequest request, MessageBroadcaster broadcaster) {
        PlayerState receiver = game.getPlayer(request.getReceiverClientId());
        if (receiver == null || !receiver.isActive()) {
            broadcaster.sendError(sender.getClientId(), request.getRequestId(),
                    "Target player not available");
            return;
        }
        Map<ResourceType, Integer> offered = toResourceMap(request.getOfferedResources());
        Map<ResourceType, Integer> requested = toResourceMap(request.getRequestedResources());

        if (!sender.getTradeService().validateOffer(sender.getSession().getResourceStorage(), offered, requested)) {
            broadcaster.sendError(sender.getClientId(), request.getRequestId(),
                    "You do not have enough resources for this trade");
            return;
        }

        TradeOffer offer = new TradeOffer(sender.getClientId(), receiver.getClientId(), offered, requested);
        if (!sender.getTradeService().lockOfferedResources(sender.getSession().getResourceStorage(), offer)) {
            broadcaster.sendError(sender.getClientId(), request.getRequestId(),
                    "Failed to lock resources");
            return;
        }

        receiver.getTradeInbox().addOffer(offer);

        TradeOfferBroadcast broadcast = new TradeOfferBroadcast("server");
        broadcast.setOfferId(offer.getId());
        broadcast.setSenderClientId(sender.getClientId());
        broadcast.setSenderUsername(sender.getUsername());
        broadcast.setReceiverClientId(receiver.getClientId());
        broadcast.setOfferedResources(request.getOfferedResources());
        broadcast.setRequestedResources(request.getRequestedResources());
        broadcast.setCreatedAt(offer.getCreatedAt().toString());

        broadcaster.sendToPlayer(receiver.getClientId(), broadcast);
        broadcaster.sendSuccess(sender.getClientId(), request.getRequestId());
    }

    public void handleTradeAccept(MultiplayerGameState game, PlayerState acceptor,
                                  TradeAcceptRequest request, MessageBroadcaster broadcaster) {
        TradeOffer offer = acceptor.getTradeInbox().getOfferById(request.getOfferId());
        if (offer == null) {
            broadcaster.sendError(acceptor.getClientId(), request.getRequestId(),
                    "Trade offer not found");
            return;
        }
        PlayerState sender = game.getPlayer(offer.getSenderClientId());
        if (sender == null) {
            broadcaster.sendError(acceptor.getClientId(), request.getRequestId(),
                    "Sender is no longer available");
            return;
        }

        boolean success = acceptor.getTradeService().acceptTrade(
                sender.getSession().getResourceStorage(),
                acceptor.getSession().getResourceStorage(),
                offer);
        if (!success) {
            broadcaster.sendError(acceptor.getClientId(), request.getRequestId(),
                    "You do not have enough resources to accept this trade");
            return;
        }

        acceptor.getTradeInbox().removeOffer(offer.getId());

        sendInboxUpdate(acceptor, broadcaster);
        sendInboxUpdate(sender, broadcaster);

        broadcaster.broadcastGameStateUpdate(game);
        broadcaster.sendSuccess(acceptor.getClientId(), request.getRequestId());
    }

    public void handleTradeReject(MultiplayerGameState game, PlayerState rejector,
                                  TradeRejectRequest request, MessageBroadcaster broadcaster) {
        TradeOffer offer = rejector.getTradeInbox().getOfferById(request.getOfferId());
        if (offer == null) {
            broadcaster.sendError(rejector.getClientId(), request.getRequestId(),
                    "Trade offer not found");
            return;
        }
        PlayerState sender = game.getPlayer(offer.getSenderClientId());
        offer.cancel();
        rejector.getTradeInbox().removeOffer(offer.getId());

        if (sender != null && sender.isActive()) {
            sender.getTradeService().cancelOffer(sender.getSession().getResourceStorage(), offer);
            sendInboxUpdate(sender, broadcaster);
        }

        sendInboxUpdate(rejector, broadcaster);
        broadcaster.sendSuccess(rejector.getClientId(), request.getRequestId());
    }

    public void handleTradeCancel(MultiplayerGameState game, PlayerState canceller,
                                  TradeCancelRequest request, MessageBroadcaster broadcaster) {
        TradeOffer offer = null;
        PlayerState receiver = null;
        for (PlayerState other : game.getAllPlayers()) {
            if (other.getClientId().equals(canceller.getClientId())) {
                continue;
            }
            TradeOffer candidate = other.getTradeInbox().getOfferById(request.getOfferId());
            if (candidate != null && candidate.getSenderClientId().equals(canceller.getClientId())) {
                offer = candidate;
                receiver = other;
                break;
            }
        }
        if (offer == null) {
            broadcaster.sendError(canceller.getClientId(), request.getRequestId(),
                    "Trade offer not found");
            return;
        }
        offer.cancel();
        receiver.getTradeInbox().removeOffer(offer.getId());
        canceller.getTradeService().cancelOffer(canceller.getSession().getResourceStorage(), offer);

        sendInboxUpdate(receiver, broadcaster);
        broadcaster.broadcastGameStateUpdate(game);
        broadcaster.sendSuccess(canceller.getClientId(), request.getRequestId());
    }

    public void sendInboxUpdate(PlayerState player, MessageBroadcaster broadcaster) {
        TradeInboxUpdateBroadcast broadcast = new TradeInboxUpdateBroadcast("server");
        broadcast.setReceiverClientId(player.getClientId());
        List<TradeOffer> offers = player.getTradeInbox().getPendingOffers();
        java.util.List<TradeInboxUpdateBroadcast.TradeOfferDTO> dtos = new java.util.ArrayList<>();
        for (int i = 0; i < offers.size(); i++) {
            TradeOffer offer = offers.get(i);
            TradeInboxUpdateBroadcast.TradeOfferDTO dto = new TradeInboxUpdateBroadcast.TradeOfferDTO();
            dto.setOfferId(offer.getId());
            dto.setSenderClientId(offer.getSenderClientId());
            dto.setCreatedAt(offer.getCreatedAt().toString());
            dto.setOfferedResources(toStringMap(offer.getOfferedResources()));
            dto.setRequestedResources(toStringMap(offer.getRequestedResources()));
            dtos.add(dto);
        }
        broadcast.setPendingOffers(dtos);
        broadcaster.sendToPlayer(player.getClientId(), broadcast);
    }

    private Map<ResourceType, Integer> toResourceMap(Map<String, Integer> stringMap) {
        Map<ResourceType, Integer> result = new HashMap<ResourceType, Integer>();
        for (String key : stringMap.keySet()) {
            try {
                ResourceType type = ResourceType.valueOf(key);
                result.put(type, stringMap.get(key));
            } catch (IllegalArgumentException e) {
            }
        }
        return result;
    }

    private Map<String, Integer> toStringMap(Map<ResourceType, Integer> resourceMap) {
        Map<String, Integer> result = new HashMap<String, Integer>();
        for (ResourceType type : resourceMap.keySet()) {
            result.put(type.name(), resourceMap.get(type));
        }
        return result;
    }
}