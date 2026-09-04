package civgame.network.message.trade;

import civgame.model.resource.ResourceType;
import civgame.network.message.MessageType;
import civgame.network.message.Request;

import java.util.HashMap;
import java.util.Map;

public class TradeOfferRequest extends Request {

    private String receiverClientId;
    private Map<String, Integer> offeredResources;
    private Map<String, Integer> requestedResources;

    public TradeOfferRequest() {
        super();
        setType(MessageType.TRADE_OFFER_REQUEST);
        this.offeredResources = new HashMap<String, Integer>();
        this.requestedResources = new HashMap<String, Integer>();
    }

    public TradeOfferRequest(String senderId, String receiverClientId,
                             Map<ResourceType, Integer> offered,
                             Map<ResourceType, Integer> requested) {
        super(MessageType.TRADE_OFFER_REQUEST, senderId);
        this.receiverClientId = receiverClientId;
        this.offeredResources = new HashMap<String, Integer>();
        this.requestedResources = new HashMap<String, Integer>();
        for (ResourceType type : offered.keySet()) {
            offeredResources.put(type.name(), offered.get(type));
        }
        for (ResourceType type : requested.keySet()) {
            requestedResources.put(type.name(), requested.get(type));
        }
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

    public void setReceiverClientId(String receiverClientId) {
        this.receiverClientId = receiverClientId;
    }

    public void setOfferedResources(Map<String, Integer> offeredResources) {
        this.offeredResources = offeredResources;
    }

    public void setRequestedResources(Map<String, Integer> requestedResources) {
        this.requestedResources = requestedResources;
    }
}