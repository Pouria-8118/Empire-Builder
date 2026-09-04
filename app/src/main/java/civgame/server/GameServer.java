package civgame.server;

import civgame.model.diplomacy.PlayerRelation;
import civgame.model.diplomacy.PlayerRelationMap;
import civgame.model.map.HexCoordinate;
import civgame.network.NetworkConfig;
import civgame.network.NetworkException;
import civgame.network.TCPServer;
import civgame.network.UDPServer;
import civgame.network.message.Broadcast;
import civgame.network.message.ErrorResponse;
import civgame.network.message.chat.ChatMessageBroadcast;
import civgame.network.message.chat.ChatMessageRequest;
import civgame.network.message.combat.AttackRequest;
import civgame.network.message.combat.AttackResponse;
import civgame.network.message.diplomacy.AllianceResponseRequest;
import civgame.network.message.diplomacy.DeclareWarRequest;
import civgame.network.message.diplomacy.DiplomacyResponse;
import civgame.network.message.diplomacy.OfferPeaceRequest;
import civgame.network.message.diplomacy.PeaceResponseRequest;
import civgame.network.message.diplomacy.ProposeAllianceRequest;
import civgame.network.message.game.EndTurnRequest;
import civgame.network.message.game.EndTurnResponse;
import civgame.network.message.game.GameStartBroadcast;
import civgame.network.message.game.TurnUpdateBroadcast;
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
import civgame.server.chat.ChatManager;
import civgame.server.combat.PvPCombatService;
import civgame.server.diplomacy.DiplomacyManager;
import civgame.server.game.GameStateSnapshotBuilder;
import civgame.server.game.MultiplayerGameSessionManager;
import civgame.server.game.MultiplayerGameState;
import civgame.server.game.PlayerState;
import civgame.server.game.TurnManager;
import civgame.server.lobby.Lobby;
import civgame.server.lobby.LobbyManager;
import civgame.server.lobby.PlayerSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private final MultiplayerGameSessionManager gameSessionManager;
    private final GameStateSnapshotBuilder snapshotBuilder;
    private final TurnManager turnManager;
    private final DiplomacyManager diplomacyManager;
    private final PvPCombatService pvpCombatService;
    
    public GameServer(NetworkConfig config) {
        this.config = config;
        this.tcpServer = new TCPServer(config);
        this.udpServer = new UDPServer(config);
        this.connectionManager = new ConnectionManager();
        this.lobbyManager = new LobbyManager();
        this.chatManager = new ChatManager();
        this.gameSessionManager = new MultiplayerGameSessionManager();
        this.snapshotBuilder = new GameStateSnapshotBuilder();
        this.turnManager = new TurnManager();
        this.diplomacyManager = new DiplomacyManager();
        this.pvpCombatService = new PvPCombatService();
        this.clientUsernames = new ConcurrentHashMap<>();
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

        gameSessionManager.handleClientDisconnect(clientId);
        diplomacyManager.handleClientDisconnect(clientId);
        
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
                clientId,
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
                ErrorResponse error = new ErrorResponse(
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
        
        MultiplayerGameState game = gameSessionManager.createGameFromLobby(lobby);
        if (game == null) {
            StartGameResponse failResponse = new StartGameResponse(
                    request.getRequestId(),
                    false,
                    "Failed to create game session"
            );
            try {
                handler.sendMessageObject(failResponse);
            } catch (NetworkException e) {
                System.err.println("Failed to send start game response: " + e.getMessage());
            }
            return;
        }

        sendGameStartBroadcasts(game);
        lobbyManager.markLobbyInGame(lobby.getLobbyId());

        System.out.println("Game started: " + game.getGameId() + " with " + game.getAllPlayers().size() + " players");
    }

    private void sendGameStartBroadcasts(MultiplayerGameState game) {
        for (PlayerState player : game.getAllPlayers()) {
            GameStartBroadcast broadcast = snapshotBuilder.buildSnapshotForPlayer(game, player);
            ClientHandler handler = findClientHandler(player.getClientId());
            if (handler == null) {
                continue;
            }
            try {
                handler.sendMessageObject(broadcast);
            } catch (NetworkException e) {
                System.err.println("Failed to send game start to " + player.getClientId() + ": " + e.getMessage());
            }
        }
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
    
    public void handleEndTurnRequest(String clientId, EndTurnRequest request) {
        MultiplayerGameState game = gameSessionManager.getGameForClient(clientId);
        if (game == null) {
            sendEndTurnError(clientId, request.getRequestId(), "You are not in a game");
            return;
        }
        
        TurnManager.EndTurnResult result = turnManager.handleEndTurn(game, clientId);
        
        ClientHandler handler = findClientHandler(clientId);
        if (handler == null) {
            return;
        }
        
        if (!result.isSuccess()) {
            sendEndTurnError(clientId, request.getRequestId(), result.getMessage());
            return;
        }
        
        EndTurnResponse successResponse = new EndTurnResponse(request.getRequestId(), true, result.getMessage());
        try {
            handler.sendMessageObject(successResponse);
        } catch (NetworkException e) {
            System.err.println("Failed to send end turn response: " + e.getMessage());
        }
        
        TurnUpdateBroadcast broadcast = result.getBroadcast();
        broadcastTurnUpdateToAllPlayers(game, broadcast);
    }
    
    private void sendEndTurnError(String clientId, String requestId, String message) {
        ClientHandler handler = findClientHandler(clientId);
        if (handler == null) {
            return;
        }
        EndTurnResponse errorResponse = new EndTurnResponse(requestId, false, message);
        try {
            handler.sendMessageObject(errorResponse);
        } catch (NetworkException e) {
            System.err.println("Failed to send end turn error: " + e.getMessage());
        }
    }
    
    private void broadcastTurnUpdateToAllPlayers(MultiplayerGameState game, TurnUpdateBroadcast broadcast) {
        for (PlayerState player : game.getAllPlayers()) {
            ClientHandler handler = findClientHandler(player.getClientId());
            if (handler == null) {
                continue;
            }
            try {
                handler.sendMessageObject(broadcast);
            } catch (NetworkException e) {
                System.err.println("Failed to send turn update to " + player.getClientId() + ": " + e.getMessage());
            }
        }
    }
    
    public void handleDeclareWarRequest(String clientId, DeclareWarRequest request) {
        MultiplayerGameState game = gameSessionManager.getGameForClient(clientId);
        if (game == null) {
            sendDiplomacyError(clientId, request.getRequestId(), "You are not in a game");
            return;
        }
        
        String targetClientId = request.getTargetClientId();
        if (targetClientId == null || targetClientId.isEmpty()) {
            sendDiplomacyError(clientId, request.getRequestId(), "No target specified");
            return;
        }
        
        DiplomacyManager.DeclareWarResult result = diplomacyManager.handleDeclareWar(game, clientId, targetClientId);
        ClientHandler handler = findClientHandler(clientId);
        if (handler == null) {
            return;
        }
        
        if (!result.isSuccess()) {
            sendDiplomacyError(clientId, request.getRequestId(), result.getMessage());
            return;
        }
        
        DiplomacyResponse successResponse = new DiplomacyResponse(request.getRequestId(), true, result.getMessage());
        try {
            handler.sendMessageObject(successResponse);
        } catch (NetworkException e) {
            System.err.println("Failed to send diplomacy response: " + e.getMessage());
        }
        
        if (result.getWarBroadcast() != null) {
            broadcastToAllInGame(game, result.getWarBroadcast());
        }
        if (result.getUpdateBroadcast() != null) {
            broadcastToAllInGame(game, result.getUpdateBroadcast());
        }
    }
    
    public void handleOfferPeaceRequest(String clientId, OfferPeaceRequest request) {
        MultiplayerGameState game = gameSessionManager.getGameForClient(clientId);
        if (game == null) {
            sendDiplomacyError(clientId, request.getRequestId(), "You are not in a game");
            return;
        }
        
        String targetClientId = request.getTargetClientId();
        if (targetClientId == null || targetClientId.isEmpty()) {
            sendDiplomacyError(clientId, request.getRequestId(), "No target specified");
            return;
        }
        
        DiplomacyManager.OfferResult result = diplomacyManager.handleOfferPeace(game, clientId, targetClientId);
        ClientHandler handler = findClientHandler(clientId);
        if (handler == null) {
            return;
        }
        
        if (!result.isSuccess()) {
            sendDiplomacyError(clientId, request.getRequestId(), result.getMessage());
            return;
        }
        
        DiplomacyResponse successResponse = new DiplomacyResponse(request.getRequestId(), true, result.getMessage());
        try {
            handler.sendMessageObject(successResponse);
        } catch (NetworkException e) {
            System.err.println("Failed to send diplomacy response: " + e.getMessage());
        }
        
        if (result.getBroadcast() != null) {
            ClientHandler targetHandler = findClientHandler(targetClientId);
            if (targetHandler != null) {
                try {
                    targetHandler.sendMessageObject(result.getBroadcast());
                } catch (NetworkException e) {
                    System.err.println("Failed to send peace offer to " + targetClientId + ": " + e.getMessage());
                }
            }
        }
    }
    
    public void handlePeaceResponseRequest(String clientId, PeaceResponseRequest request) {
        MultiplayerGameState game = gameSessionManager.getGameForClient(clientId);
        if (game == null) {
            sendDiplomacyError(clientId, request.getRequestId(), "You are not in a game");
            return;
        }
        
        String proposerClientId = request.getProposerClientId();
        if (proposerClientId == null || proposerClientId.isEmpty()) {
            sendDiplomacyError(clientId, request.getRequestId(), "No proposer specified");
            return;
        }
        
        DiplomacyManager.ResponseResult result = diplomacyManager.handlePeaceResponse(
                game, clientId, proposerClientId, request.isAccepted()
        );
        
        ClientHandler handler = findClientHandler(clientId);
        if (handler == null) {
            return;
        }
        
        if (!result.isSuccess()) {
            sendDiplomacyError(clientId, request.getRequestId(), result.getMessage());
            return;
        }
        
        DiplomacyResponse successResponse = new DiplomacyResponse(request.getRequestId(), true, result.getMessage());
        try {
            handler.sendMessageObject(successResponse);
        } catch (NetworkException e) {
            System.err.println("Failed to send diplomacy response: " + e.getMessage());
        }
        
        if (result.getPrimaryBroadcast() != null) {
            broadcastToAllInGame(game, result.getPrimaryBroadcast());
        }
        if (result.getUpdateBroadcast() != null) {
            broadcastToAllInGame(game, result.getUpdateBroadcast());
        }
    }
    
    public void handleProposeAllianceRequest(String clientId, ProposeAllianceRequest request) {
        MultiplayerGameState game = gameSessionManager.getGameForClient(clientId);
        if (game == null) {
            sendDiplomacyError(clientId, request.getRequestId(), "You are not in a game");
            return;
        }
        
        String targetClientId = request.getTargetClientId();
        if (targetClientId == null || targetClientId.isEmpty()) {
            sendDiplomacyError(clientId, request.getRequestId(), "No target specified");
            return;
        }
        
        DiplomacyManager.OfferResult result = diplomacyManager.handleProposeAlliance(game, clientId, targetClientId);
        ClientHandler handler = findClientHandler(clientId);
        if (handler == null) {
            return;
        }
        
        if (!result.isSuccess()) {
            sendDiplomacyError(clientId, request.getRequestId(), result.getMessage());
            return;
        }
        
        DiplomacyResponse successResponse = new DiplomacyResponse(request.getRequestId(), true, result.getMessage());
        try {
            handler.sendMessageObject(successResponse);
        } catch (NetworkException e) {
            System.err.println("Failed to send diplomacy response: " + e.getMessage());
        }
        
        if (result.getBroadcast() != null) {
            ClientHandler targetHandler = findClientHandler(targetClientId);
            if (targetHandler != null) {
                try {
                    targetHandler.sendMessageObject(result.getBroadcast());
                } catch (NetworkException e) {
                    System.err.println("Failed to send alliance proposal to " + targetClientId + ": " + e.getMessage());
                }
            }
        }
    }
    
    public void handleAllianceResponseRequest(String clientId, AllianceResponseRequest request) {
        MultiplayerGameState game = gameSessionManager.getGameForClient(clientId);
        if (game == null) {
            sendDiplomacyError(clientId, request.getRequestId(), "You are not in a game");
            return;
        }
        
        String proposerClientId = request.getProposerClientId();
        if (proposerClientId == null || proposerClientId.isEmpty()) {
            sendDiplomacyError(clientId, request.getRequestId(), "No proposer specified");
            return;
        }
        
        DiplomacyManager.ResponseResult result = diplomacyManager.handleAllianceResponse(
                game, clientId, proposerClientId, request.isAccepted()
        );
        
        ClientHandler handler = findClientHandler(clientId);
        if (handler == null) {
            return;
        }
        
        if (!result.isSuccess()) {
            sendDiplomacyError(clientId, request.getRequestId(), result.getMessage());
            return;
        }
        
        DiplomacyResponse successResponse = new DiplomacyResponse(request.getRequestId(), true, result.getMessage());
        try {
            handler.sendMessageObject(successResponse);
        } catch (NetworkException e) {
            System.err.println("Failed to send diplomacy response: " + e.getMessage());
        }
        
        if (result.getPrimaryBroadcast() != null) {
            broadcastToAllInGame(game, result.getPrimaryBroadcast());
        }
        if (result.getUpdateBroadcast() != null) {
            broadcastToAllInGame(game, result.getUpdateBroadcast());
        }
    }
    
    public void handleAttackRequest(String clientId, AttackRequest request) {
        MultiplayerGameState game = gameSessionManager.getGameForClient(clientId);
        if (game == null) {
            sendAttackError(clientId, request.getRequestId(), "You are not in a game");
            return;
        }
        
        if (!clientId.equals(game.getCurrentTurnClientId())) {
            sendAttackError(clientId, request.getRequestId(), "It is not your turn");
            return;
        }
        
        PlayerRelationMap relationMap = game.getRelationMap();
        String defenderClientId = request.getDefenderClientId();
        
        if (defenderClientId == null || defenderClientId.isEmpty()) {
            sendAttackError(clientId, request.getRequestId(), "No defender specified");
            return;
        }
        
        PlayerRelation relation = relationMap.getRelation(clientId, defenderClientId);
        if (!relation.canAttack()) {
            sendAttackError(clientId, request.getRequestId(), 
                    "Cannot attack: you are " + relation.getDisplayName() + " with this player");
            return;
        }
        
        PlayerState attackerPlayer = game.getPlayer(clientId);
        PlayerState defenderPlayer = game.getPlayer(defenderClientId);
        
        if (attackerPlayer == null || defenderPlayer == null) {
            sendAttackError(clientId, request.getRequestId(), "Invalid player");
            return;
        }
        
        HexCoordinate attackerCoordinate = new HexCoordinate(request.getAttackerQ(), request.getAttackerR());
        HexCoordinate defenderCoordinate = new HexCoordinate(request.getDefenderQ(), request.getDefenderR());
        
        PvPCombatService.PvPCombatResult result = pvpCombatService.executePvPAttack(
                game, attackerPlayer, defenderPlayer, attackerCoordinate, defenderCoordinate);
        
        ClientHandler handler = findClientHandler(clientId);
        if (handler == null) {
            return;
        }
        
        if (!result.isSuccess()) {
            sendAttackError(clientId, request.getRequestId(), result.getMessage());
            return;
        }
        
        AttackResponse successResponse = new AttackResponse(request.getRequestId(), true, result.getMessage());
        handler.sendMessageObjectIgnoreError(successResponse);
        
        if (result.getBroadcast() != null) {
            broadcastToAllInGame(game, result.getBroadcast());
        }
    }
    
    private void sendAttackError(String clientId, String requestId, String message) {
        ClientHandler handler = findClientHandler(clientId);
        if (handler == null) {
            return;
        }
        AttackResponse errorResponse = new AttackResponse(requestId, false, message);
        handler.sendMessageObjectIgnoreError(errorResponse);
    }
    
    private void broadcastToAllInGame(MultiplayerGameState game, Broadcast broadcast) {
        for (int i = 0; i < game.getAllPlayers().size(); i++) {
            PlayerState player = game.getAllPlayers().get(i);
            ClientHandler handler = findClientHandler(player.getClientId());
            if (handler == null) {
                continue;
            }
            try {
                handler.sendMessageObject(broadcast);
            } catch (NetworkException e) {
                System.err.println("Failed to broadcast to " + player.getClientId() + ": " + e.getMessage());
            }
        }
    }
    
    private void sendDiplomacyError(String clientId, String requestId, String message) {
        ClientHandler handler = findClientHandler(clientId);
        if (handler == null) {
            return;
        }
        DiplomacyResponse errorResponse = new DiplomacyResponse(requestId, false, message);
        try {
            handler.sendMessageObject(errorResponse);
        } catch (NetworkException e) {
            System.err.println("Failed to send diplomacy error: " + e.getMessage());
        }
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