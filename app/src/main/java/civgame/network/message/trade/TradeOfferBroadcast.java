package civgame.network.message.trade;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;

import java.util.HashMap;
import java.util.Map;

public class TradeOfferBroadcast extends Broadcast {

    private String offerId;
    private String senderClientId;
    private String senderUsername;
    private String receiverClientId;
    private Map<String, Integer> offeredResources;
    private Map<String, Integer> requestedResources;
    private String createdAt;

    public TradeOfferBroadcast() {
        super();
        setType(MessageType.TRADE_OFFER_BROADCAST);
        this.offeredResources = new HashMap<String, Integer>();
        this.requestedResources = new HashMap<String, Integer>();
    }

    public TradeOfferBroadcast(String broadcasterId) {
        super(MessageType.TRADE_OFFER_BROADCAST, broadcasterId);
        this.offeredResources = new HashMap<String, Integer>();
        this.requestedResources = new HashMap<String, Integer>();
    }

    public String getOfferId() {
        return offerId;
    }

    public String getSenderClientId() {
        return senderClientId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getReceiverClientId() {
        return receiverClientId;
    }

    public Map<String, Integer> getOfferedResources() {
        return offeredResources;
    }

    public Map<String, Integer> getRequestedResources() {
        return requestedResources;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public void setSenderClientId(String senderClientId) {
        this.senderClientId = senderClientId;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public void setReceiverClientId(String receiverClientId) {
        this.receiverClientId = receiverClientId;
    }

    public void setOfferedResources(Map<String, Integer> offeredResources) {
        this.offeredResources = offeredResources;
    }

    public void setRequestedResources(Map<String, Integer> requestedResources) {
        this.requestedResources = requestedResources;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}