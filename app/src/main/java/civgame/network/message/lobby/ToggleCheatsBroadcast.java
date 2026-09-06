package civgame.network.message.lobby;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;

public class ToggleCheatsBroadcast extends Broadcast {
    private boolean enabled;

    public ToggleCheatsBroadcast() {
        super();
        setType(MessageType.TOGGLE_CHEATS_BROADCAST);
    }

    public ToggleCheatsBroadcast(String broadcasterId, boolean enabled) {
        super(MessageType.TOGGLE_CHEATS_BROADCAST, broadcasterId);
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}