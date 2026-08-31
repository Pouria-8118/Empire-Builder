package civgame.network.message.lobby;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class StartGameRequest extends Request {
    
    public StartGameRequest() {
        super();
        setType(MessageType.START_GAME_REQUEST);
    }
    
    public StartGameRequest(String senderId) {
        super(MessageType.START_GAME_REQUEST, senderId);
    }
}