package civgame.network.message.diplomacy;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class DeclareWarRequest extends Request {
    
    private String targetClientId;
    
    public DeclareWarRequest() {
        super();
        setType(MessageType.DECLARE_WAR_REQUEST);
    }
    
    public DeclareWarRequest(String senderId, String targetClientId) {
        super(MessageType.DECLARE_WAR_REQUEST, senderId);
        this.targetClientId = targetClientId;
    }
    
    public String getTargetClientId() {
        return targetClientId;
    }
    
    public void setTargetClientId(String targetClientId) {
        this.targetClientId = targetClientId;
    }
}