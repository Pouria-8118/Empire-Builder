package civgame.network.message.trade;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;

import java.util.ArrayList;
import java.util.List;

public class TradeInboxUpdateBroadcast extends Broadcast {

    private String receiverClientId;
    private List<TradeOfferDTO> pendingOffers;

    public TradeInboxUpdateBroadcast() {
        super();
        setType(MessageType.TRADE_INBOX_UPDATE_BROADCAST);
        this.pendingOffers = new ArrayList<TradeOfferDTO>();
    }

    public TradeInboxUpdateBroadcast(String broadcasterId) {
        super(MessageType.TRADE_INBOX_UPDATE_BROADCAST, broadcasterId);
        this.pendingOffers = new ArrayList<TradeOfferDTO>();
    }

    public String getReceiverClientId() {
        return receiverClientId;
    }

    public List<TradeOfferDTO> getPendingOffers() {
        return pendingOffers;
    }

    public void setReceiverClientId(String receiverClientId) {
        this.receiverClientId = receiverClientId;
    }

    public void setPendingOffers(List<TradeOfferDTO> pendingOffers) {
        this.pendingOffers = pendingOffers;
    }

    public static class TradeOfferDTO implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private String offerId;
        private String senderClientId;
        private String senderUsername;
        private java.util.Map<String, Integer> offeredResources;
        private java.util.Map<String, Integer> requestedResources;
        private String createdAt;

        public TradeOfferDTO() {
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

        public java.util.Map<String, Integer> getOfferedResources() {
            return offeredResources;
        }

        public java.util.Map<String, Integer> getRequestedResources() {
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

        public void setOfferedResources(java.util.Map<String, Integer> offeredResources) {
            this.offeredResources = offeredResources;
        }

        public void setRequestedResources(java.util.Map<String, Integer> requestedResources) {
            this.requestedResources = requestedResources;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}