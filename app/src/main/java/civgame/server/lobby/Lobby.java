package civgame.server.lobby;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Lobby {
    
    private final String lobbyId;
    private final Map<String, PlayerSlot> players;
    private String hostClientId;
    private String selectedMapName;
    private LobbyState state;
    private final long createdAt;
    private boolean cheatsEnabled;
    
    public Lobby(String lobbyId, String hostClientId, String hostUsername) {
        this.lobbyId = lobbyId;
        this.players = new ConcurrentHashMap<>();
        this.state = LobbyState.WAITING;
        this.createdAt = System.currentTimeMillis();
        this.selectedMapName = null;
        this.cheatsEnabled = false;
        
        PlayerSlot hostSlot = new PlayerSlot(hostClientId, hostUsername);
        this.players.put(hostClientId, hostSlot);
        this.hostClientId = hostClientId;
    }
    
    public String getLobbyId() {
        return lobbyId;
    }
    
    public String getHostClientId() {
        return hostClientId;
    }
    
    public boolean isHost(String clientId) {
        return hostClientId != null && hostClientId.equals(clientId);
    }
    
    public LobbyState getState() {
        return state;
    }
    
    public void setState(LobbyState state) {
        this.state = state;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public String getSelectedMapName() {
        return selectedMapName;
    }
    
    public void setSelectedMapName(String mapName) {
        this.selectedMapName = mapName;
    }
    
    public boolean isCheatsEnabled() {
        return cheatsEnabled;
    }
    
    public void setCheatsEnabled(boolean cheatsEnabled) {
        this.cheatsEnabled = cheatsEnabled;
    }
    
    public synchronized void addPlayer(String clientId, String username) {
        if (state != LobbyState.WAITING) {
            return;
        }
        if (!players.containsKey(clientId)) {
            players.put(clientId, new PlayerSlot(clientId, username));
        }
    }
    
    public synchronized void removePlayer(String clientId) {
        players.remove(clientId);
        
        if (hostClientId != null && hostClientId.equals(clientId)) {
            hostClientId = null;
            if (!players.isEmpty()) {
                String newHost = players.keySet().iterator().next();
                hostClientId = newHost;
            }
        }
    }
    
    public synchronized boolean setPlayerReady(String clientId, boolean ready) {
        PlayerSlot slot = players.get(clientId);
        if (slot == null) {
            return false;
        }
        slot.setReady(ready);
        return true;
    }
    
    public synchronized boolean togglePlayerReady(String clientId) {
        PlayerSlot slot = players.get(clientId);
        if (slot == null) {
            return false;
        }
        slot.toggleReady();
        return true;
    }
    
    public boolean isPlayerInLobby(String clientId) {
        return players.containsKey(clientId);
    }
    
    public PlayerSlot getPlayer(String clientId) {
        return players.get(clientId);
    }
    
    public List<PlayerSlot.PlayerSlotInfo> getPlayerInfos() {
        List<PlayerSlot.PlayerSlotInfo> infos = new ArrayList<>();
        for (PlayerSlot slot : players.values()) {
            infos.add(slot.toInfo());
        }
        return infos;
    }
    
    public List<String> getPlayerClientIds() {
        return new ArrayList<>(players.keySet());
    }
    
    public int getPlayerCount() {
        return players.size();
    }
    
    public boolean areAllPlayersReady() {
        if (players.isEmpty()) {
            return false;
        }
        for (PlayerSlot slot : players.values()) {
            if (!slot.isReady()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean canStartGame() {
        return state == LobbyState.WAITING 
                && players.size() >= 2 
                && selectedMapName != null 
                && areAllPlayersReady();
    }
}