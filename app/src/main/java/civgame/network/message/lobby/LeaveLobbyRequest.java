package civgame.network.message.lobby;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class LeaveLobbyRequest extends Request {
    
    public LeaveLobbyRequest() {
        super();
        setType(MessageType.LEAVE_LOBBY_REQUEST);
    }
    
    public LeaveLobbyRequest(String senderId) {
        super(MessageType.LEAVE_LOBBY_REQUEST, senderId);
    }
}