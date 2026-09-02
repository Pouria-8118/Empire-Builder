package civgame.network.message.game;

import civgame.network.message.MessageType;
import civgame.network.message.Response;

public class EndTurnResponse extends Response {
    
    private String reason;
    
    public EndTurnResponse() {
        super();
        setType(MessageType.END_TURN_RESPONSE);
    }
    
    public EndTurnResponse(String requestId, boolean success, String reason) {
        super(MessageType.END_TURN_RESPONSE, requestId, success);
        this.reason = reason;
    }
    
    public EndTurnResponse(String requestId, String errorMessage) {
        super(MessageType.END_TURN_RESPONSE, requestId, errorMessage);
        this.reason = errorMessage;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}