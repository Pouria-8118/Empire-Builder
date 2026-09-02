package civgame.network.message.game;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class EndTurnRequest extends Request {
    
    public EndTurnRequest() {
        super();
        setType(MessageType.END_TURN_REQUEST);
    }
    
    public EndTurnRequest(String senderId) {
        super(MessageType.END_TURN_REQUEST, senderId);
    }
}