package civgame.network.message.lobby;

import civgame.network.message.MessageType;
import civgame.network.message.Response;

public class StartGameResponse extends Response {
    
    private String reason;
    
    public StartGameResponse() {
        super();
        setType(MessageType.START_GAME_RESPONSE);
    }
    
    public StartGameResponse(String requestId, boolean success, String reason) {
        super(MessageType.START_GAME_RESPONSE, requestId, success);
        this.reason = reason;
    }
    
    public StartGameResponse(String requestId, String errorMessage) {
        super(MessageType.START_GAME_RESPONSE, requestId, errorMessage);
        this.reason = errorMessage;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}