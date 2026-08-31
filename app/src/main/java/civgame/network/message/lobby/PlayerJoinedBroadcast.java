package civgame.network.message.lobby;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;
import civgame.server.lobby.PlayerSlot;

import java.util.ArrayList;
import java.util.List;

public class PlayerJoinedBroadcast extends Broadcast {
    
    private PlayerSlot.PlayerSlotInfo joinedPlayer;
    private List<PlayerSlot.PlayerSlotInfo> allPlayers;
    private String hostClientId;
    
    public PlayerJoinedBroadcast() {
        super();
        setType(MessageType.PLAYER_JOINED_BROADCAST);
        this.allPlayers = new ArrayList<>();
    }
    
    public PlayerJoinedBroadcast(String broadcasterId,
                                 PlayerSlot.PlayerSlotInfo joinedPlayer,
                                 List<PlayerSlot.PlayerSlotInfo> allPlayers,
                                 String hostClientId) {
        super(MessageType.PLAYER_JOINED_BROADCAST, broadcasterId);
        this.joinedPlayer = joinedPlayer;
        this.allPlayers = allPlayers != null ? allPlayers : new ArrayList<>();
        this.hostClientId = hostClientId;
    }
    
    public PlayerSlot.PlayerSlotInfo getJoinedPlayer() {
        return joinedPlayer;
    }
    
    public void setJoinedPlayer(PlayerSlot.PlayerSlotInfo joinedPlayer) {
        this.joinedPlayer = joinedPlayer;
    }
    
    public List<PlayerSlot.PlayerSlotInfo> getAllPlayers() {
        return allPlayers;
    }
    
    public void setAllPlayers(List<PlayerSlot.PlayerSlotInfo> allPlayers) {
        this.allPlayers = allPlayers;
    }
    
    public String getHostClientId() {
        return hostClientId;
    }
    
    public void setHostClientId(String hostClientId) {
        this.hostClientId = hostClientId;
    }
}