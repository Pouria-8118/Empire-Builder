package civgame.network.message.diplomacy;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class OfferPeaceRequest extends Request {
    
    private String targetClientId;
    
    public OfferPeaceRequest() {
        super();
        setType(MessageType.OFFER_PEACE_REQUEST);
    }
    
    public OfferPeaceRequest(String senderId, String targetClientId) {
        super(MessageType.OFFER_PEACE_REQUEST, senderId);
        this.targetClientId = targetClientId;
    }
    
    public String getTargetClientId() {
        return targetClientId;
    }
    
    public void setTargetClientId(String targetClientId) {
        this.targetClientId = targetClientId;
    }
}