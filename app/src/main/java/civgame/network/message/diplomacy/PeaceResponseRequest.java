package civgame.network.message.diplomacy;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class PeaceResponseRequest extends Request {
    
    private String proposerClientId;
    private boolean accepted;
    
    public PeaceResponseRequest() {
        super();
        setType(MessageType.PEACE_RESPONSE_REQUEST);
    }
    
    public PeaceResponseRequest(String senderId, String proposerClientId, boolean accepted) {
        super(MessageType.PEACE_RESPONSE_REQUEST, senderId);
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