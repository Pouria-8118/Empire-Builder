package civgame.server;

import civgame.network.NetworkConfig;
import civgame.network.NetworkException;
import civgame.network.TCPServer;
import civgame.network.UDPServer;
import civgame.server.lobby.Lobby;
import civgame.server.lobby.LobbyManager;
import civgame.server.lobby.PlayerSlot;
import civgame.network.message.Broadcast;
import civgame.network.message.lobby.JoinLobbyRequest;
import civgame.network.message.lobby.JoinLobbyResponse;
import civgame.network.message.lobby.LeaveLobbyRequest;
import civgame.network.message.lobby.MapSelectedBroadcast;
import civgame.network.message.lobby.PlayerJoinedBroadcast;
import civgame.network.message.lobby.PlayerLeftBroadcast;
import civgame.network.message.lobby.PlayerReadyBroadcast;
import civgame.network.message.lobby.SelectMapRequest;
import civgame.network.message.lobby.SetReadyRequest;
import civgame.network.message.lobby.StartGameRequest;
import civgame.network.message.lobby.StartGameResponse;
import civgame.network.message.chat.ChatMessageBroadcast;
import civgame.network.message.chat.ChatMessageRequest;
import civgame.server.chat.ChatManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameServer {
    
    private final NetworkConfig config;
    private final TCPServer tcpServer;
    private final UDPServer udpServer;
    private final ConnectionManager connectionManager;
    private final LobbyManager lobbyManager;
    private final Map<String, String> clientUsernames;
    private ServerState state;
    private Thread acceptThread;
    private final List<ClientHandler> clientHandlers;
    
    private final ChatManager chatManager;
    
    public GameServer(NetworkConfig config) {
        this.config = config;
        this.tcpServer = new TCPServer(config);
        this.udpServer = new UDPServer(config);
        this.connectionManager = new ConnectionManager();
        this.lobbyManager = new LobbyManager();
        this.chatManager = new ChatManager();
        this.clientUsernames = new java.util.concurrent.ConcurrentHashMap<>();
        this.state = ServerState.STOPPED;
        this.clientHandlers = new ArrayList<>();
    }
    
    public void start() throws NetworkException {
        state = ServerState.STARTING;
        System.out.println("Starting Game Server...");
        
        tcpServer.start();
        udpServer.start();
        
        state = ServerState.RUNNING;
        
        acceptThread = new Thread(new Runnable() {
            @Override
            public void run() {
                acceptLoop();
            }
        }, "ServerAcceptThread");
        acceptThread.start();
        
        System.out.println("Game Server started successfully");
    }
    
    public void stop() {
        System.out.println("Stopping Game Server...");
        state = ServerState.STOPPED;
        
        for (int i = 0; i < clientHandlers.size(); i++) {
            ClientHandler handler = clientHandlers.get(i);
            handler.stopHandler();
        }
        clientHandlers.clear();
        
        connectionManager.closeAllConnections();
        
        tcpServer.stop();
        udpServer.stop();
        
        System.out.println("Game Server stopped");
    }
    
    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }
    
    private void acceptLoop() {
        while (state.canAcceptConnections()) {
            try {
                TCPServer.ClientConnection clientConnection = tcpServer.acceptClient();
                
                if (clientConnection != null) {
                    handleNewClient(clientConnection);
                }
                
            } catch (NetworkException e) {
                if (state.canAcceptConnections()) {
                    System.err.println("Error accepting client: " + e.getMessage());
                    state = ServerState.ERROR;
                }
            }
        }
    }
    
    private void handleNewClient(TCPServer.ClientConnection connection) {
        String clientId = generateClientId(connection);
        
        connectionManager.addConnection(clientId, connection);
        
        ClientHandler handler = new ClientHandler(connection, clientId, this);
        clientHandlers.add(handler);
        handler.start();
        
        System.out.println("New client connected: " + clientId);
    }
    
    public void handleClientDisconnect(String clientId) {
        String username = clientUsernames.remove(clientId);
        Lobby lobby = lobbyManager.leaveLobby(clientId);
        
        if (lobby != null) {
            PlayerLeftBroadcast broadcast = new PlayerLeftBroadcast(
                    "server",
                    clientId,
                    username != null ? username : clientId,
                    lobby.getPlayerInfos(),
                    lobby.getHostClientId(),
                    lobby.getPlayerCount() == 0
            );
            broadcastToLobby(lobby, broadcast, null);
        }
        
        connectionManager.removeConnection(clientId);
        
        ClientHandler handlerToRemove = null;
        for (int i = 0; i < clientHandlers.size(); i++) {
            ClientHandler handler = clientHandlers.get(i);
            if (handler.getClientId().equals(clientId)) {
                handlerToRemove = handler;
                break;
            }
        }
        if (handlerToRemove != null) {
            clientHandlers.remove(handlerToRemove);
            handlerToRemove.stopHandler();
        }
        
        System.out.println("Client fully disconnected: " + clientId);
    }
    
    public void registerClientUsername(String clientId, String username) {
        clientUsernames.put(clientId, username);
    }
    
    public String getClientUsername(String clientId) {
        String username = clientUsernames.get(clientId);
        return username != null ? username : clientId;
    }
    
    public void handleJoinLobbyRequest(String clientId, JoinLobbyRequest request) {
        String username = request.getUsername() != null ? request.getUsername() : clientId;
        registerClientUsername(clientId, username);
        
        Lobby lobby;
        if (request.getLobbyId() != null && !request.getLobbyId().isEmpty()) {
            lobby = lobbyManager.joinLobby(request.getLobbyId(), clientId, username);
        } else {
            lobby = lobbyManager.joinOrCreateLobby(clientId, username, null);
        }
        
        ClientHandler handler = findClientHandler(clientId);
        if (handler == null) {
            return;
        }
        
        if (lobby == null) {
            JoinLobbyResponse failResponse = new JoinLobbyResponse(
                    request.getRequestId(), 
                    "Failed to join lobby: lobby not found or not accepting players"
            );
            try {
                handler.sendMessageObject(failResponse);
            } catch (NetworkException e) {
                System.err.println("Failed to send join lobby response: " + e.getMessage());
            }
            return;
        }
        
        JoinLobbyResponse successResponse = new JoinLobbyResponse(
                request.getRequestId(),
                true,
                lobby.getLobbyId(),
                lobby.getHostClientId(),
                lobby.getPlayerInfos(),
                lobby.getSelectedMapName()
        );
        try {
            handler.sendMessageObject(successResponse);
        } catch (NetworkException e) {
            System.err.println("Failed to send join lobby response: " + e.getMessage());
        }
        
        PlayerJoinedBroadcast joinBroadcast = new PlayerJoinedBroadcast(
                "server",
                lobby.getPlayer(clientId).toInfo(),
                lobby.getPlayerInfos(),
                lobby.getHostClientId()
        );
        broadcastToLobby(lobby, joinBroadcast, clientId);
    }
    
    public void handleLeaveLobbyRequest(String clientId, LeaveLobbyRequest request) {
        Lobby lobby = lobbyManager.leaveLobby(clientId);
        
        if (lobby != null) {
            String username = getClientUsername(clientId);
            PlayerLeftBroadcast broadcast = new PlayerLeftBroadcast(
                    "server",
                    clientId,
                    username,
                    lobby.getPlayerInfos(),
                    lobby.getHostClientId(),
                    lobby.getPlayerCount() == 0
            );
            broadcastToLobby(lobby, broadcast, null);
        }
    }
    
    public void handleSetReadyRequest(String clientId, SetReadyRequest request) {
        Lobby lobby = lobbyManager.getLobbyForClient(clientId);
        if (lobby == null) {
            return;
        }
        
        lobbyManager.setPlayerReady(clientId, request.isReady());
        
        PlayerSlot slot = lobby.getPlayer(clientId);
        if (slot == null) {
            return;
        }
        
        PlayerReadyBroadcast broadcast = new PlayerReadyBroadcast(
                "server",
                clientId,
                slot.getUsername(),
                slot.isReady(),
                lobby.getPlayerInfos()
        );
        broadcastToLobby(lobby, broadcast, null);
    }
    
    public void handleSelectMapRequest(String clientId, SelectMapRequest request) {
        Lobby lobby = lobbyManager.getLobbyForClient(clientId);
        if (lobby == null) {
            return;
        }
        if (!lobby.isHost(clientId)) {
            ClientHandler handler = findClientHandler(clientId);
            if (handler != null) {
                civgame.network.message.ErrorResponse error = new civgame.network.message.ErrorResponse(
                        request.getRequestId(),
                        "NOT_HOST",
                        "Only the host can select the map"
                );
                try {
                    handler.sendMessageObject(error);
                } catch (NetworkException e) {
                    System.err.println("Failed to send error response: " + e.getMessage());
                }
            }
            return;
        }
        
        lobbyManager.selectMap(clientId, request.getMapName());
        
        MapSelectedBroadcast broadcast = new MapSelectedBroadcast(
                "server",
                request.getMapName(),
                clientId,
                getClientUsername(clientId)
        );
        broadcastToLobby(lobby, broadcast, null);
    }
    
    public void handleStartGameRequest(String clientId, StartGameRequest request) {
        Lobby lobby = lobbyManager.getLobbyForClient(clientId);
        
        ClientHandler handler = findClientHandler(clientId);
        if (handler == null) {
            return;
        }
        
        if (lobby == null) {
            StartGameResponse failResponse = new StartGameResponse(
                    request.getRequestId(),
                    false,
                    "You are not in a lobby"
            );
            try {
                handler.sendMessageObject(failResponse);
            } catch (NetworkException e) {
                System.err.println("Failed to send start game response: " + e.getMessage());
            }
            return;
        }
        
        if (!lobby.isHost(clientId)) {
            StartGameResponse failResponse = new StartGameResponse(
                    request.getRequestId(),
                    false,
                    "Only the host can start the game"
            );
            try {
                handler.sendMessageObject(failResponse);
            } catch (NetworkException e) {
                System.err.println("Failed to send start game response: " + e.getMessage());
            }
            return;
        }
        
        if (!lobby.canStartGame()) {
            String reason = "Cannot start game";
            if (lobby.getPlayerCount() < 2) {
                reason = "Need at least 2 players to start";
            } else if (lobby.getSelectedMapName() == null) {
                reason = "No map has been selected";
            } else if (!lobby.areAllPlayersReady()) {
                reason = "Not all players are ready";
            }
            
            StartGameResponse failResponse = new StartGameResponse(
                    request.getRequestId(),
                    false,
                    reason
            );
            try {
                handler.sendMessageObject(failResponse);
            } catch (NetworkException e) {
                System.err.println("Failed to send start game response: " + e.getMessage());
            }
            return;
        }
        
        lobby = lobbyManager.prepareToStartGame(clientId);
        
        StartGameResponse successResponse = new StartGameResponse(
                request.getRequestId(),
                true,
                "Game starting..."
        );
        try {
            handler.sendMessageObject(successResponse);
        } catch (NetworkException e) {
            System.err.println("Failed to send start game response: " + e.getMessage());
        }
        
        // TODO: Implement actual game start
        System.out.println("Game is about to start for lobby: " + lobby.getLobbyId());
        System.out.println("Players: " + lobby.getPlayerInfos().size());
        System.out.println("Map: " + lobby.getSelectedMapName());
    }
    
    public void handleChatMessageRequest(String clientId, ChatMessageRequest request) {
        String content = request.getContent();
        if (content == null || content.trim().isEmpty()) {
            return;
        }
        
        Lobby lobby = lobbyManager.getLobbyForClient(clientId);
        if (lobby == null) {
            return;
        }
        
        String username = getClientUsername(clientId);
        ChatMessageBroadcast broadcast = chatManager.createChatBroadcast(clientId, username, content.trim());
        
        List<ClientHandler> lobbyHandlers = new ArrayList<>();
        List<String> playerIds = lobby.getPlayerClientIds();
        for (int i = 0; i < playerIds.size(); i++) {
            ClientHandler handler = findClientHandler(playerIds.get(i));
            if (handler != null) {
                lobbyHandlers.add(handler);
            }
        }
        
        chatManager.broadcastToClients(broadcast, lobbyHandlers);
        
        System.out.println("Chat [" + username + "]: " + content);
    }
    
    private void broadcastToLobby(Lobby lobby, Broadcast broadcast, String excludeClientId) {
        List<String> playerIds = lobby.getPlayerClientIds();
        for (int i = 0; i < playerIds.size(); i++) {
            String playerId = playerIds.get(i);
            if (excludeClientId != null && excludeClientId.equals(playerId)) {
                continue;
            }
            ClientHandler handler = findClientHandler(playerId);
            if (handler != null) {
                try {
                    handler.sendMessageObject(broadcast);
                } catch (NetworkException e) {
                    System.err.println("Failed to broadcast to " + playerId + ": " + e.getMessage());
                }
            }
        }
    }
    
    private ClientHandler findClientHandler(String clientId) {
        for (int i = 0; i < clientHandlers.size(); i++) {
            ClientHandler handler = clientHandlers.get(i);
            if (handler.getClientId().equals(clientId)) {
                return handler;
            }
        }
        return null;
    }
    
    private String generateClientId(TCPServer.ClientConnection connection) {
        return "client_" + System.currentTimeMillis() + "_" + connection.getSocket().getPort();
    }
    
    public ServerState getState() {
        return state;
    }
    
    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }
    
    public TCPServer getTcpServer() {
        return tcpServer;
    }
    
    public UDPServer getUdpServer() {
        return udpServer;
    }
    
    public NetworkConfig getConfig() {
        return config;
    }
}