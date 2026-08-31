package civgame.network.message.lobby;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;
import civgame.server.lobby.PlayerSlot;

import java.util.ArrayList;
import java.util.List;

public class PlayerLeftBroadcast extends Broadcast {
    
    private String leftClientId;
    private String leftUsername;
    private List<PlayerSlot.PlayerSlotInfo> remainingPlayers;
    private String newHostClientId;
    private boolean lobbyClosed;
    
    public PlayerLeftBroadcast() {
        super();
        setType(MessageType.PLAYER_LEFT_BROADCAST);
        this.remainingPlayers = new ArrayList<>();
    }
    
    public PlayerLeftBroadcast(String broadcasterId,
                               String leftClientId,
                               String leftUsername,
                               List<PlayerSlot.PlayerSlotInfo> remainingPlayers,
                               String newHostClientId,
                               boolean lobbyClosed) {
        super(MessageType.PLAYER_LEFT_BROADCAST, broadcasterId);
        this.leftClientId = leftClientId;
        this.leftUsername = leftUsername;
        this.remainingPlayers = remainingPlayers != null ? remainingPlayers : new ArrayList<>();
        this.newHostClientId = newHostClientId;
        this.lobbyClosed = lobbyClosed;
    }
    
    public String getLeftClientId() {
        return leftClientId;
    }
    
    public void setLeftClientId(String leftClientId) {
        this.leftClientId = leftClientId;
    }
    
    public String getLeftUsername() {
        return leftUsername;
    }
    
    public void setLeftUsername(String leftUsername) {
        this.leftUsername = leftUsername;
    }
    
    public List<PlayerSlot.PlayerSlotInfo> getRemainingPlayers() {
        return remainingPlayers;
    }
    
    public void setRemainingPlayers(List<PlayerSlot.PlayerSlotInfo> remainingPlayers) {
        this.remainingPlayers = remainingPlayers;
    }
    
    public String getNewHostClientId() {
        return newHostClientId;
    }
    
    public void setNewHostClientId(String newHostClientId) {
        this.newHostClientId = newHostClientId;
    }
    
    public boolean isLobbyClosed() {
        return lobbyClosed;
    }
    
    public void setLobbyClosed(boolean lobbyClosed) {
        this.lobbyClosed = lobbyClosed;
    }
}