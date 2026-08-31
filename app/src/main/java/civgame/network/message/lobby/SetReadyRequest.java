package civgame.network.message.lobby;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class SetReadyRequest extends Request {
    
    private boolean ready;
    
    public SetReadyRequest() {
        super();
        setType(MessageType.SET_READY_REQUEST);
    }
    
    public SetReadyRequest(String senderId, boolean ready) {
        super(MessageType.SET_READY_REQUEST, senderId);
        this.ready = ready;
    }
    
    public boolean isReady() {
        return ready;
    }
    
    public void setReady(boolean ready) {
        this.ready = ready;
    }
}