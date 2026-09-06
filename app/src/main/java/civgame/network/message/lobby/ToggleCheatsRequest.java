package civgame.network.message.lobby;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class ToggleCheatsRequest extends Request {
    private boolean enabled;

    public ToggleCheatsRequest() {
        super();
        setType(MessageType.TOGGLE_CHEATS_REQUEST);
    }

    public ToggleCheatsRequest(String senderId, boolean enabled) {
        super(MessageType.TOGGLE_CHEATS_REQUEST, senderId);
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}