package civgame.network.message.diplomacy;

import civgame.network.message.MessageType;
import civgame.network.message.Response;

public class DiplomacyResponse extends Response {
    
    private String reason;
    
    public DiplomacyResponse() {
        super();
        setType(MessageType.DIPLOMACY_RESPONSE);
    }
    
    public DiplomacyResponse(String requestId, boolean success, String reason) {
        super(MessageType.DIPLOMACY_RESPONSE, requestId, success);
        this.reason = reason;
    }
    
    public DiplomacyResponse(String requestId, String errorMessage) {
        super(MessageType.DIPLOMACY_RESPONSE, requestId, errorMessage);
        this.reason = errorMessage;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}