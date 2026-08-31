package civgame.network.message.lobby;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;
import civgame.server.lobby.PlayerSlot;

import java.util.ArrayList;
import java.util.List;

public class PlayerReadyBroadcast extends Broadcast {
    
    private String clientId;
    private String username;
    private boolean ready;
    private List<PlayerSlot.PlayerSlotInfo> allPlayers;
    
    public PlayerReadyBroadcast() {
        super();
        setType(MessageType.PLAYER_READY_BROADCAST);
        this.allPlayers = new ArrayList<>();
    }
    
    public PlayerReadyBroadcast(String broadcasterId,
                                String clientId,
                                String username,
                                boolean ready,
                                List<PlayerSlot.PlayerSlotInfo> allPlayers) {
        super(MessageType.PLAYER_READY_BROADCAST, broadcasterId);
        this.clientId = clientId;
        this.username = username;
        this.ready = ready;
        this.allPlayers = allPlayers != null ? allPlayers : new ArrayList<>();
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public boolean isReady() {
        return ready;
    }
    
    public void setReady(boolean ready) {
        this.ready = ready;
    }
    
    public List<PlayerSlot.PlayerSlotInfo> getAllPlayers() {
        return allPlayers;
    }
    
    public void setAllPlayers(List<PlayerSlot.PlayerSlotInfo> allPlayers) {
        this.allPlayers = allPlayers;
    }
}