package civgame.network.message.diplomacy;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class ProposeAllianceRequest extends Request {
    
    private String targetClientId;
    
    public ProposeAllianceRequest() {
        super();
        setType(MessageType.PROPOSE_ALLIANCE_REQUEST);
    }
    
    public ProposeAllianceRequest(String senderId, String targetClientId) {
        super(MessageType.PROPOSE_ALLIANCE_REQUEST, senderId);
        this.targetClientId = targetClientId;
    }
    
    public String getTargetClientId() {
        return targetClientId;
    }
    
    public void setTargetClientId(String targetClientId) {
        this.targetClientId = targetClientId;
    }
}