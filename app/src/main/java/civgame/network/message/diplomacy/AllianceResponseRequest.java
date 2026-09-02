package civgame.network.message.diplomacy;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class AllianceResponseRequest extends Request {
    
    private String proposerClientId;
    private boolean accepted;
    
    public AllianceResponseRequest() {
        super();
        setType(MessageType.ALLIANCE_RESPONSE_REQUEST);
    }
    
    public AllianceResponseRequest(String senderId, String proposerClientId, boolean accepted) {
        super(MessageType.ALLIANCE_RESPONSE_REQUEST, senderId);
        this.proposerClientId = proposerClientId;
        this.accepted = accepted;
    }
    
    public String getProposerClientId() {
        return proposerClientId;
    }
    
    public void setProposerClientId(String proposerClientId) {
        this.proposerClientId = proposerClientId;
    }
    
    public boolean isAccepted() {
        return accepted;
    }
    
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}