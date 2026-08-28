package civgame.network.message;

public class PingRequest extends Request {
    
    public PingRequest() {
        super();
        setType(MessageType.PING_REQUEST);
    }
    
    public PingRequest(String senderId) {
        super(MessageType.PING_REQUEST, senderId);
    }
}