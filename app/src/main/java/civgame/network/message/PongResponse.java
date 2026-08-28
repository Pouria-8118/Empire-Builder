package civgame.network.message;

public class PongResponse extends Response {
    
    public PongResponse() {
        super();
        setType(MessageType.PONG_RESPONSE);
    }
    
    public PongResponse(String requestId, boolean success) {
        super(MessageType.PONG_RESPONSE, requestId, success);
    }
}