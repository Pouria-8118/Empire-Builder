package civgame.model.trade;

import civgame.model.resource.ResourceType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TradeOffer implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final LocalDateTime createdAt;
    private final String senderClientId;
    private final String receiverClientId;
    private final Map<ResourceType, Integer> offeredResources;
    private final Map<ResourceType, Integer> requestedResources;
    private boolean cancelled;

    public TradeOffer(String senderClientId, String receiverClientId,
                      Map<ResourceType, Integer> offeredResources,
                      Map<ResourceType, Integer> requestedResources) {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.senderClientId = senderClientId;
        this.receiverClientId = receiverClientId;
        this.offeredResources = new HashMap<ResourceType, Integer>(offeredResources);
        this.requestedResources = new HashMap<ResourceType, Integer>(requestedResources);
        this.cancelled = false;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getSenderClientId() {
        return senderClientId;
    }

    public String getReceiverClientId() {
        return receiverClientId;
    }

    public Map<ResourceType, Integer> getOfferedResources() {
        return offeredResources;
    }

    public Map<ResourceType, Integer> getRequestedResources() {
        return requestedResources;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }
}