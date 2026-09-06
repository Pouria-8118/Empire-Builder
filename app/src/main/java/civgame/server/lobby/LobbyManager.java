package civgame.server.lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LobbyManager {
    
    private final Map<String, Lobby> lobbies;
    private final Map<String, String> clientToLobby;
    
    public LobbyManager() {
        this.lobbies = new ConcurrentHashMap<>();
        this.clientToLobby = new ConcurrentHashMap<>();
    }
    
    public synchronized Lobby createLobby(String hostClientId, String hostUsername) {
        String lobbyId = generateLobbyId();
        Lobby lobby = new Lobby(lobbyId, hostClientId, hostUsername);
        lobbies.put(lobbyId, lobby);
        clientToLobby.put(hostClientId, lobbyId);
        System.out.println("Lobby created: " + lobbyId + " by " + hostUsername);
        return lobby;
    }
    
    public synchronized Lobby joinLobby(String lobbyId, String clientId, String username) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            return null;
        }
        if (!lobby.getState().isAcceptingPlayers()) {
            return null;
        }
        if (clientToLobby.containsKey(clientId)) {
            return null;
        }
        lobby.addPlayer(clientId, username);
        clientToLobby.put(clientId, lobbyId);
        System.out.println("Player " + username + " joined lobby " + lobbyId);
        return lobby;
    }
    
    public synchronized Lobby joinOrCreateLobby(String clientId, String username, String mapName) {
        if (clientToLobby.containsKey(clientId)) {
            String existingLobbyId = clientToLobby.get(clientId);
            return lobbies.get(existingLobbyId);
        }
        
        for (Lobby lobby : lobbies.values()) {
            if (lobby.getState().isAcceptingPlayers() && lobby.getPlayerCount() < 8) {
                lobby.addPlayer(clientId, username);
                clientToLobby.put(clientId, lobby.getLobbyId());
                return lobby;
            }
        }
        
        Lobby newLobby = createLobby(clientId, username);
        return newLobby;
    }
    
    public synchronized Lobby leaveLobby(String clientId) {
        String lobbyId = clientToLobby.remove(clientId);
        if (lobbyId == null) {
            return null;
        }
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            return null;
        }
        lobby.removePlayer(clientId);
        System.out.println("Player " + clientId + " left lobby " + lobbyId);
        
        if (lobby.getPlayerCount() == 0) {
            lobbies.remove(lobbyId);
            System.out.println("Lobby " + lobbyId + " closed (empty)");
            return null;
        }
        
        return lobby;
    }
    
    public Lobby getLobbyForClient(String clientId) {
        String lobbyId = clientToLobby.get(clientId);
        if (lobbyId == null) {
            return null;
        }
        return lobbies.get(lobbyId);
    }
    
    public Lobby getLobby(String lobbyId) {
        return lobbies.get(lobbyId);
    }
    
    public List<Lobby> getAllLobbies() {
        return new ArrayList<>(lobbies.values());
    }
    
    public synchronized void selectMap(String clientId, String mapName) {
        Lobby lobby = getLobbyForClient(clientId);
        if (lobby == null) {
            return;
        }
        if (!lobby.isHost(clientId)) {
            return;
        }
        lobby.setSelectedMapName(mapName);
        System.out.println("Host " + clientId + " selected map: " + mapName);
    }
    
    public synchronized boolean setPlayerReady(String clientId, boolean ready) {
        Lobby lobby = getLobbyForClient(clientId);
        if (lobby == null) {
            return false;
        }
        return lobby.setPlayerReady(clientId, ready);
    }

    public synchronized boolean setCheatsEnabled(String clientId, boolean enabled) {
        Lobby lobby = getLobbyForClient(clientId);
        if (lobby == null) {
            return false;
        }
        if (!lobby.isHost(clientId)) {
            return false;
        }
        lobby.setCheatsEnabled(enabled);
        return true;
    }
    
    public synchronized Lobby prepareToStartGame(String clientId) {
        Lobby lobby = getLobbyForClient(clientId);
        if (lobby == null) {
            return null;
        }
        if (!lobby.isHost(clientId)) {
            return null;
        }
        if (!lobby.canStartGame()) {
            return null;
        }
        lobby.setState(LobbyState.STARTING);
        return lobby;
    }
    
    public synchronized void markLobbyInGame(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            lobby.setState(LobbyState.IN_GAME);
        }
    }
    
    public synchronized void closeLobby(String lobbyId) {
        Lobby lobby = lobbies.remove(lobbyId);
        if (lobby == null) {
            return;
        }
        for (String clientId : lobby.getPlayerClientIds()) {
            clientToLobby.remove(clientId);
        }
        System.out.println("Lobby " + lobbyId + " closed");
    }
    
    public synchronized void handleClientDisconnect(String clientId) {
        leaveLobby(clientId);
    }
    
    private String generateLobbyId() {
        return "lobby_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
}