package civgame.controller.lobby;

import civgame.client.GameClient;
import civgame.client.MultiplayerGameClientState;
import civgame.controller.chat.ChatController;
import civgame.controller.diplomacy.DiplomacyController;
import civgame.controller.game.GameController;
import civgame.controller.game.MultiplayerTurnController;
import civgame.model.building.TownHall;
import civgame.model.building.TownHallLevel;
import civgame.model.game.CombatResult;
import civgame.model.game.EnemyUnitMarker;
import civgame.model.game.FogOfWarCalculator;
import civgame.model.game.GameSession;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.map.TileResourceType;
import civgame.model.map.TileVisibility;
import civgame.model.resource.ResourceType;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitFactory;
import civgame.model.unit.UnitType;
import civgame.network.NetworkConfig;
import civgame.network.NetworkException;
import civgame.network.message.Broadcast;
import civgame.network.message.ErrorResponse;
import civgame.network.message.Message;
import civgame.network.message.MessageType;
import civgame.network.message.chat.ChatMessageBroadcast;
import civgame.network.message.combat.AttackRequest;
import civgame.network.message.combat.AttackResponse;
import civgame.network.message.combat.CombatResultBroadcast;
import civgame.network.message.diplomacy.DiplomacyResponse;
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
import civgame.server.lobby.PlayerSlot;
import civgame.util.Constants;
import civgame.view.game.CombatResultDialog;
import civgame.view.game.GameFrame;
import civgame.view.lobby.LobbyFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LobbyController {
    
    private final LobbyFrame lobbyFrame;
    private final ChatController chatController;
    private GameClient gameClient;
    private final String username;
    private String myClientId;
    private String currentLobbyId;
    private String currentHostClientId;
    private boolean isReady;
    private boolean suppressMapChange;
    private MultiplayerGameClientState multiplayerState;
    private DiplomacyController diplomacyController;
    private GameClient currentGameClient;
    private GameSession currentGameSession;
    private GameFrame currentGameFrame;
    private GameController currentGameController;
    private MultiplayerTurnController turnController;
    
    public LobbyController(LobbyFrame lobbyFrame, String username) {
        this.lobbyFrame = lobbyFrame;
        this.username = username;
        this.myClientId = null;
        this.currentLobbyId = null;
        this.currentHostClientId = null;
        this.isReady = false;
        this.suppressMapChange = true;
        this.gameClient = null;
        this.chatController = new ChatController(lobbyFrame.getChatPanel());
        this.multiplayerState = null;
        this.turnController = null;
        
        registerListeners();
    }
    
    private void registerListeners() {
        lobbyFrame.getLobbyPanel().getReadyButton().addActionListener(new ReadyButtonListener());
        lobbyFrame.getLobbyPanel().getStartButton().addActionListener(new StartButtonListener());
        lobbyFrame.getLobbyPanel().getLeaveButton().addActionListener(new LeaveButtonListener());
        lobbyFrame.getLobbyPanel().getMapComboBox().addActionListener(new MapComboBoxListener());
        lobbyFrame.getLobbyPanel().getCheatsCheckBox().addActionListener(new CheatsCheckBoxListener());
    }
    
    public void connectAndJoin() {
        lobbyFrame.getLobbyPanel().setStatusMessage("Connecting to server...");
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NetworkConfig config = new NetworkConfig();
                    gameClient = new GameClient(config);
                    
                    gameClient.setClientCallback(new GameClient.GameClientCallback() {
                        @Override
                        public void onConnected() {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    lobbyFrame.getLobbyPanel().setStatusMessage("Connected, joining lobby...");
                                }
                            });
                        }
                        
                        @Override
                        public void onDisconnected(String reason) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    JOptionPane.showMessageDialog(
                                            lobbyFrame,
                                            "Disconnected from server: " + reason,
                                            "Connection Lost",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                    lobbyFrame.dispose();
                                }
                            });
                        }
                        
                        @Override
                        public void onServerMessage(String message) {
                        }
                        
                        @Override
                        public void onServerMessageObject(Message message) {
                            handleServerMessage(message);
                        }
                    });
                    
                    gameClient.connect();
                    
                    JoinLobbyRequest request = new JoinLobbyRequest("pending", username, null);
                    gameClient.sendMessageObject(request);
                    
                } catch (NetworkException e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(
                                    lobbyFrame,
                                    "Failed to connect: " + e.getMessage(),
                                    "Connection Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            lobbyFrame.dispose();
                        }
                    });
                }
            }
        }, "LobbyConnectionThread").start();
    }
    
    private void handleServerMessage(Message message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                processMessage(message);
            }
        });
    }
    
    private void processMessage(Message message) {
        try {
            String type = message.getType();
            if (type.equals(MessageType.JOIN_LOBBY_RESPONSE)) {
                handleJoinLobbyResponse((JoinLobbyResponse) message);
            } else if (type.equals(MessageType.PLAYER_JOINED_BROADCAST)) {
                handlePlayerJoinedBroadcast((PlayerJoinedBroadcast) message);
            } else if (type.equals(MessageType.PLAYER_LEFT_BROADCAST)) {
                handlePlayerLeftBroadcast((PlayerLeftBroadcast) message);
            } else if (type.equals(MessageType.PLAYER_READY_BROADCAST)) {
                handlePlayerReadyBroadcast((PlayerReadyBroadcast) message);
            } else if (type.equals(MessageType.MAP_SELECTED_BROADCAST)) {
                handleMapSelectedBroadcast((MapSelectedBroadcast) message);
            } else if (type.equals(MessageType.START_GAME_RESPONSE)) {
                handleStartGameResponse((StartGameResponse) message);
            } else if (type.equals(MessageType.GAME_START_BROADCAST)) {
                handleGameStartBroadcast((GameStartBroadcast) message);
            } else if (type.equals(MessageType.CHAT_MESSAGE_BROADCAST)) {
                chatController.handleChatBroadcast((ChatMessageBroadcast) message);
            } else if (type.equals(MessageType.ERROR_RESPONSE)) {
                handleErrorResponse((ErrorResponse) message);
            } else if (type.equals(MessageType.TOGGLE_CHEATS_BROADCAST)) {
                handleToggleCheatsBroadcast((civgame.network.message.lobby.ToggleCheatsBroadcast) message);
        }} catch (Exception e) {
            System.err.println("Error processing lobby message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleJoinLobbyResponse(JoinLobbyResponse response) {
        if (!response.isSuccess()) {
            JOptionPane.showMessageDialog(
                    lobbyFrame,
                    "Failed to join lobby: " + response.getErrorMessage(),
                    "Join Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            lobbyFrame.dispose();
            return;
        }
        
        myClientId = response.getMyClientId();
        currentLobbyId = response.getLobbyId();
        currentHostClientId = response.getHostClientId();
        
        chatController.setGameClient(gameClient, myClientId);
        
        lobbyFrame.getLobbyPanel().initializeWithClientId(myClientId);
        lobbyFrame.getLobbyPanel().updatePlayerList(response.getCurrentPlayers(), currentHostClientId);
        lobbyFrame.getLobbyPanel().setAvailableMaps(response.getAvailableMaps());
        suppressMapChange = true;
        lobbyFrame.getLobbyPanel().updateMapSelection(response.getSelectedMapName());
        suppressMapChange = false;
        lobbyFrame.getLobbyPanel().setStatusMessage("Joined lobby: " + currentLobbyId);
    }
    
    private void handlePlayerJoinedBroadcast(PlayerJoinedBroadcast broadcast) {
        currentHostClientId = broadcast.getHostClientId();
        lobbyFrame.getLobbyPanel().updatePlayerList(broadcast.getAllPlayers(), currentHostClientId);
        
        PlayerSlot.PlayerSlotInfo joined = broadcast.getJoinedPlayer();
        if (joined != null) {
            lobbyFrame.getLobbyPanel().setStatusMessage(joined.getUsername() + " joined the lobby");
        }
    }
    
    private void handlePlayerLeftBroadcast(PlayerLeftBroadcast broadcast) {
        if (broadcast.isLobbyClosed()) {
            JOptionPane.showMessageDialog(
                    lobbyFrame,
                    "Lobby has been closed",
                    "Lobby Closed",
                    JOptionPane.INFORMATION_MESSAGE
            );
            lobbyFrame.dispose();
            return;
        }
        
        currentHostClientId = broadcast.getNewHostClientId();
        lobbyFrame.getLobbyPanel().updatePlayerList(broadcast.getRemainingPlayers(), currentHostClientId);
        lobbyFrame.getLobbyPanel().setStatusMessage(broadcast.getLeftUsername() + " left the lobby");
    }
    
    private void handlePlayerReadyBroadcast(PlayerReadyBroadcast broadcast) {
        lobbyFrame.getLobbyPanel().updatePlayerList(broadcast.getAllPlayers(), currentHostClientId);
        
        String statusText = broadcast.isReady() ? "is ready" : "is not ready";
        lobbyFrame.getLobbyPanel().setStatusMessage(broadcast.getUsername() + " " + statusText);
    }
    
    private void handleMapSelectedBroadcast(MapSelectedBroadcast broadcast) {
        suppressMapChange = true;
        lobbyFrame.getLobbyPanel().updateMapSelection(broadcast.getMapName());
        suppressMapChange = false;
        lobbyFrame.getLobbyPanel().setStatusMessage(
                broadcast.getSelectedByUsername() + " selected map: " + broadcast.getMapName()
        );
    }
    
    private void handleStartGameResponse(StartGameResponse response) {
        if (!response.isSuccess()) {
            JOptionPane.showMessageDialog(
                    lobbyFrame,
                    "Cannot start game: " + response.getReason(),
                    "Start Failed",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        lobbyFrame.getLobbyPanel().setStatusMessage("Game is starting... waiting for snapshot from server");
    }
    
    private void handleGameStartBroadcast(GameStartBroadcast broadcast) {
        multiplayerState = new MultiplayerGameClientState(broadcast);
        lobbyFrame.getLobbyPanel().setStatusMessage("Loading game...");
        launchMultiplayerGame();
    }
    
    private void launchMultiplayerGame() {
        if (multiplayerState == null) {
            return;
        }
        
        GameStartBroadcast snapshot = multiplayerState.getInitialSnapshot();
        GameSession localSession = rebuildLocalSessionFromSnapshot(snapshot);
        
        FogOfWarCalculator fogCalculator = new FogOfWarCalculator();
        fogCalculator.updateVisibility(localSession);
        
        GameFrame gameFrame = new GameFrame(localSession, true);
        this.currentGameFrame = gameFrame;
        this.currentGameSession = localSession;
        GameController gameController = new GameController(gameFrame, localSession, fogCalculator, null);
        this.currentGameController = gameController;

        gameController.setMultiplayerState(multiplayerState);
        gameController.setNetworkManager(gameClient.getNetworkManager());

        if (gameClient != null) {
            this.turnController = new MultiplayerTurnController(
                    gameFrame,
                    localSession,
                    gameClient,
                    multiplayerState.getMyClientId(),
                    multiplayerState.getInitialSnapshot().getInitialTurnClientId()
            );
            
            gameClient.setClientCallback(new GameClient.GameClientCallback() {
                @Override
                public void onConnected() {
                }
                
                @Override
                public void onDisconnected(String reason) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(
                                    gameFrame,
                                    "Disconnected from server: " + reason,
                                    "Connection Lost",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            gameFrame.dispose();
                        }
                    });
                }
                
                @Override
                public void onServerMessage(String message) {
                }
                
                @Override
                public void onServerMessageObject(Message message) {
                    handleGameMessage(message);
                }
            });
            
            ChatController gameChatController = new ChatController(gameFrame.getChatPanel());
            gameChatController.setGameClient(gameClient, multiplayerState.getMyClientId());
            
            this.diplomacyController = new DiplomacyController(gameFrame, multiplayerState.getMyClientId());
            diplomacyController.setGameClient(gameClient);
            diplomacyController.initializeFromOpponents(snapshot.getOpponents());
            diplomacyController.initializeFromRelations(snapshot.getInitialRelations());
            
            gameController.setDiplomacyState(diplomacyController.getState());
            
            initializeEnemyUnits(localSession, snapshot.getVisibleEnemyUnits());
            setupPvpAttackListener(gameFrame, localSession, gameClient);
        }
        
        gameFrame.setVisible(true);
        lobbyFrame.dispose();
        
        gameFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (gameClient != null && gameClient.isConnected()) {
                    gameClient.disconnect();
                }
                System.out.println("Game frame closed, disconnecting from server");
            }
        });
    }
    
    private void handleGameMessage(Message message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    processGameMessage(message);
                } catch (Exception e) {
                    System.err.println("Error processing game message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void processGameMessage(Message message) {
        String type = message.getType();
        if (type.equals(MessageType.END_TURN_RESPONSE)) {
            if (turnController != null) {
                turnController.handleEndTurnResponse((EndTurnResponse) message);
            }
        } else if (type.equals(MessageType.TURN_UPDATE_BROADCAST)) {
            if (turnController != null) {
                turnController.handleTurnUpdate((TurnUpdateBroadcast) message);
            }
        } else if (type.equals(MessageType.CHAT_MESSAGE_BROADCAST)) {
            if (currentGameFrame != null && currentGameFrame.getChatPanel() != null) {
                ChatController gameChatController = new ChatController(currentGameFrame.getChatPanel());
                gameChatController.setGameClient(gameClient, multiplayerState.getMyClientId());
                gameChatController.handleChatBroadcast((ChatMessageBroadcast) message);
            }
        } else if (type.equals(MessageType.DIPLOMACY_RESPONSE)) {
            if (diplomacyController != null) {
                diplomacyController.handleDiplomacyResponse((DiplomacyResponse) message);
            }
        } else if (isDiplomacyBroadcast(type)) {
            if (diplomacyController != null) {
                diplomacyController.handleBroadcast((Broadcast) message);
            }
        } else if (type.equals(MessageType.ATTACK_RESPONSE)) {
            handleAttackResponse((AttackResponse) message);
        } else if (type.equals(MessageType.COMBAT_RESULT_BROADCAST)) {
            handleCombatResultBroadcast((CombatResultBroadcast) message);
        } else if (type.equals(MessageType.GAME_START_BROADCAST)) {
            handleGameStateUpdate((GameStartBroadcast) message);
        } else if (type.equals(MessageType.FOG_REVEAL_BROADCAST)) {
            handleFogRevealBroadcast((civgame.network.message.game.FogRevealBroadcast) message);
        }

        
    }

    private void handleGameStateUpdate(GameStartBroadcast snapshot) {
        if (currentGameSession == null || currentGameFrame == null) {
            return;
        }
        
        if (diplomacyController != null) {
            diplomacyController.initializeFromRelations(snapshot.getInitialRelations());
        }
        
        civgame.model.game.FogOfWarCalculator fogCalculator = new civgame.model.game.FogOfWarCalculator();
        fogCalculator.updateVisibility(currentGameSession);
        
        updateMapVisibilityFromSnapshot(snapshot);
        updateTerritory(snapshot.getTerritory());
        
        initializeEnemyUnits(currentGameSession, snapshot.getVisibleEnemyUnits());
        
        currentGameFrame.refreshHud(currentGameSession);
        currentGameFrame.getMapPanel().repaint();
    }

    private void updateTerritory(GameStartBroadcast.TerritoryDTO territoryDTO) {
        if (territoryDTO == null) {
            return;
        }
        Set<HexCoordinate> allyTiles = new java.util.HashSet<>();
        for (String tileKey : territoryDTO.getAllyTerritory()) {
            String[] parts = tileKey.split(",");
            int q = Integer.parseInt(parts[0]);
            int r = Integer.parseInt(parts[1]);
            allyTiles.add(new HexCoordinate(q, r));
        }
        currentGameSession.setAllyTerritory(allyTiles);
        
        Set<HexCoordinate> enemyTiles = new java.util.HashSet<>();
        for (String tileKey : territoryDTO.getEnemyTerritory()) {
            String[] parts = tileKey.split(",");
            int q = Integer.parseInt(parts[0]);
            int r = Integer.parseInt(parts[1]);
            enemyTiles.add(new HexCoordinate(q, r));
        }
        currentGameSession.setEnemyTerritory(enemyTiles);
    }

    private void updateMapVisibilityFromSnapshot(GameStartBroadcast snapshot) {
        HexMap localMap = currentGameSession.getHexMap();
        Map<String, GameStartBroadcast.TileDTO> tiles = snapshot.getTiles();
        for (Map.Entry<String, GameStartBroadcast.TileDTO> entry : tiles.entrySet()) {
            GameStartBroadcast.TileDTO dto = entry.getValue();
            HexCoordinate coordinate = new HexCoordinate(dto.getQ(), dto.getR());
            TileVisibility newVisibility = TileVisibility.valueOf(dto.getVisibility());
            HexTile existingTile = localMap.getTile(coordinate);
            if (existingTile == null) {
                continue;
            }
            boolean shouldUpdate = false;
            if (existingTile.getVisibility() == TileVisibility.UNSEEN) {
                shouldUpdate = true;
            } else if (newVisibility.ordinal() > existingTile.getVisibility().ordinal()) {
                shouldUpdate = true;
            }
            if (shouldUpdate) {
                try {
                    TerrainType terrain = TerrainType.valueOf(dto.getTerrainType());
                    TileResourceType resource = TileResourceType.valueOf(dto.getResourceType());
                    HexTile newTile = new HexTile(coordinate, terrain, resource);
                    TileVisibility finalVisibility = newVisibility.ordinal() > existingTile.getVisibility().ordinal() 
                            ? newVisibility : existingTile.getVisibility();
                    newTile.setVisibility(finalVisibility);
                    localMap.setTile(coordinate, newTile);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }
    
    private boolean isDiplomacyBroadcast(String type) {
        return type.equals(MessageType.DECLARE_WAR_BROADCAST)
                || type.equals(MessageType.PEACE_OFFER_BROADCAST)
                || type.equals(MessageType.PEACE_ESTABLISHED_BROADCAST)
                || type.equals(MessageType.ALLIANCE_PROPOSAL_BROADCAST)
                || type.equals(MessageType.ALLIANCE_ESTABLISHED_BROADCAST)
                || type.equals(MessageType.RELATION_UPDATE_BROADCAST);
    }
    
    private GameSession rebuildLocalSessionFromSnapshot(GameStartBroadcast snapshot) {
        int radius = snapshot.getMapRadius();
        HexMap localMap = new HexMap(radius);
        
        Map<String, GameStartBroadcast.TileDTO> tiles = snapshot.getTiles();
        for (Map.Entry<String, GameStartBroadcast.TileDTO> entry : tiles.entrySet()) {
            GameStartBroadcast.TileDTO dto = entry.getValue();
            HexCoordinate coordinate = new HexCoordinate(dto.getQ(), dto.getR());
            TerrainType terrain = TerrainType.valueOf(dto.getTerrainType());
            TileResourceType resource = TileResourceType.valueOf(dto.getResourceType());
            HexTile tile = new HexTile(coordinate, terrain, resource);
            tile.setVisibility(TileVisibility.valueOf(dto.getVisibility()));
            localMap.setTile(coordinate, tile);
        }
        
        GameStartBroadcast.PlayerSnapshotDTO playerSnapshot = snapshot.getMySnapshot();
        HexCoordinate townHallCoord = new HexCoordinate(
                playerSnapshot.getTownHallQ(), playerSnapshot.getTownHallR()
        );
        TownHall townHall = new TownHall(townHallCoord);
        townHall.setLevel(TownHallLevel.valueOf(playerSnapshot.getTownHallLevel()));
        townHall.setMaxHitPoints(playerSnapshot.getTownHallMaxHp());
        int damage = playerSnapshot.getTownHallMaxHp() - playerSnapshot.getTownHallCurrentHp();
        if (damage > 0) {
            townHall.applyCombatDamage(damage);
        }
        townHall.setDefense(playerSnapshot.getTownHallDefense());
        
        GameSession session = new GameSession(localMap, townHall, Constants.INITIAL_UNIT_CAP);
        
        session.getResourceStorage().addResource(ResourceType.FOOD, playerSnapshot.getFood());
        session.getResourceStorage().addResource(ResourceType.WOOD, playerSnapshot.getWood());
        session.getResourceStorage().addResource(ResourceType.STONE, playerSnapshot.getStone());
        session.getResourceStorage().addResource(ResourceType.IRON, playerSnapshot.getIron());
        
        UnitFactory unitFactory = new UnitFactory();
        List<GameStartBroadcast.UnitDTO> units = playerSnapshot.getUnits();
        for (int i = 0; i < units.size(); i++) {
            GameStartBroadcast.UnitDTO unitDTO = units.get(i);
            UnitType unitType = UnitType.valueOf(unitDTO.getUnitType());
            HexCoordinate unitCoord = new HexCoordinate(unitDTO.getQ(), unitDTO.getR());
            Unit unit = unitFactory.createUnit(unitType, unitCoord);
            session.addUnit(unit);
        }
        
        return session;
    }
    
    private void handleErrorResponse(ErrorResponse response) {
        JOptionPane.showMessageDialog(
                lobbyFrame,
                "Error: " + response.getErrorMessage(),
                "Server Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
    
    private class ReadyButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (gameClient == null || !gameClient.isConnected()) {
                return;
            }
            isReady = !isReady;
            SetReadyRequest request = new SetReadyRequest(myClientId, isReady);
            try {
                gameClient.sendMessageObject(request);
                lobbyFrame.getLobbyPanel().getReadyButton().setText(isReady ? "Not Ready" : "Ready");
            } catch (NetworkException e) {
                JOptionPane.showMessageDialog(
                        lobbyFrame,
                        "Failed to update ready status: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (gameClient == null || !gameClient.isConnected()) {
                return;
            }
            
            String selectedMap = lobbyFrame.getLobbyPanel().getSelectedMapName();
            if (selectedMap == null || selectedMap.isEmpty()) {
                JOptionPane.showMessageDialog(
                        lobbyFrame,
                        "Please select a map first",
                        "No Map Selected",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            
            StartGameRequest request = new StartGameRequest(myClientId);
            try {
                gameClient.sendMessageObject(request);
            } catch (NetworkException e) {
                JOptionPane.showMessageDialog(
                        lobbyFrame,
                        "Failed to send start request: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    private class LeaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int choice = JOptionPane.showConfirmDialog(
                    lobbyFrame,
                    "Are you sure you want to leave the lobby?",
                    "Confirm Leave",
                    JOptionPane.YES_NO_OPTION
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                if (gameClient != null && gameClient.isConnected()) {
                    LeaveLobbyRequest request = new LeaveLobbyRequest(myClientId);
                    try {
                        gameClient.sendMessageObject(request);
                    } catch (NetworkException e) {
                    }
                    gameClient.disconnect();
                }
                lobbyFrame.dispose();
            }
        }
    }
    
    private class MapComboBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (suppressMapChange) {
                return;
            }
            if (gameClient == null || !gameClient.isConnected()) {
                return;
            }
            String selectedMap = lobbyFrame.getLobbyPanel().getSelectedMapName();
            if (selectedMap == null) {
                return;
            }
            SelectMapRequest request = new SelectMapRequest(myClientId, selectedMap);
            try {
                gameClient.sendMessageObject(request);
            } catch (NetworkException e) {
                JOptionPane.showMessageDialog(
                        lobbyFrame,
                        "Failed to select map: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    private class CheatsCheckBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (gameClient == null || !gameClient.isConnected()) {
                return;
            }
            if (!myClientId.equals(currentHostClientId)) {
                JOptionPane.showMessageDialog(
                        lobbyFrame,
                        "Only the host can enable/disable cheats.",
                        "Permission Denied",
                        JOptionPane.WARNING_MESSAGE
                );
                lobbyFrame.getLobbyPanel().getCheatsCheckBox().setSelected(
                        !lobbyFrame.getLobbyPanel().getCheatsCheckBox().isSelected()
                );
                return;
            }
            boolean isEnabled = lobbyFrame.getLobbyPanel().getCheatsCheckBox().isSelected();
            civgame.network.message.lobby.ToggleCheatsRequest request = 
                    new civgame.network.message.lobby.ToggleCheatsRequest(myClientId, isEnabled);
            try {
                gameClient.sendMessageObject(request);
            } catch (NetworkException e) {
                JOptionPane.showMessageDialog(
                        lobbyFrame,
                        "Failed to toggle cheats: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    private void initializeEnemyUnits(GameSession session,
                                       List<GameStartBroadcast.EnemyUnitDTO> enemyUnits) {
        if (enemyUnits == null) {
            return;
        }
        
        session.clearVisibleEnemyUnits();
        for (int i = 0; i < enemyUnits.size(); i++) {
            GameStartBroadcast.EnemyUnitDTO dto = enemyUnits.get(i);
            EnemyUnitMarker marker = new EnemyUnitMarker(
                    dto.getUnitId(),
                    dto.getOwnerClientId(),
                    dto.getOwnerUsername(),
                    UnitType.valueOf(dto.getUnitType()),
                    new HexCoordinate(dto.getQ(), dto.getR()),
                    dto.getCurrentHp(),
                    dto.getMaxHp(),
                    dto.isFloating()
            );
            session.addVisibleEnemyUnit(marker);
        }
    }
    
    private void setupPvpAttackListener(GameFrame gameFrame, GameSession session, GameClient client) {
        this.currentGameClient = client;
        this.currentGameSession = session;
        
        GameController.PvpAttackRequestListener listener = new GameController.PvpAttackRequestListener() {
            @Override
            public void onAttackRequested(Unit attackerUnit, EnemyUnitMarker enemyUnit) {
                sendPvPAttackRequest(attackerUnit, enemyUnit);
            }
        };
        
        currentGameController.setPvpAttackRequestListener(listener);
    }
    
    private void sendPvPAttackRequest(Unit attackerUnit, EnemyUnitMarker enemyUnit) {
        if (currentGameClient == null || !currentGameClient.isConnected()) {
            return;
        }
        
        AttackRequest request = new AttackRequest(
                multiplayerState.getMyClientId(),
                attackerUnit.getCoordinate(),
                enemyUnit.getCoordinate(),
                enemyUnit.getUnitId(),
                enemyUnit.getOwnerClientId()
        );
        
        try {
            currentGameClient.sendMessageObject(request);
        } catch (NetworkException e) {
            JOptionPane.showMessageDialog(
                    currentGameFrame,
                    "Failed to send attack request: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void handleAttackResponse(AttackResponse response) {
        if (!response.isSuccess()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(
                            currentGameFrame,
                            "Attack failed: " + response.getReason(),
                            "Attack Failed",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            });
        }
    }
    
    private void handleCombatResultBroadcast(CombatResultBroadcast broadcast) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (currentGameSession != null) {
                    updateEnemyUnitsFromCombat(broadcast);
                    currentGameSession.clearTribeNotifications();
                }
                
                showCombatResultDialog(broadcast);
                
                if (currentGameClient != null && currentGameFrame != null) {
                    currentGameFrame.refreshHud(currentGameSession);
                    currentGameFrame.getMapPanel().repaint();
                }
            }
        });
    }
    
    private void updateEnemyUnitsFromCombat(CombatResultBroadcast broadcast) {
        if (currentGameSession == null) {
            return;
        }
        
        List<CombatResultBroadcast.UnitLossDTO> defenderLosses = broadcast.getDefenderLosses();
        
        for (int i = 0; i < defenderLosses.size(); i++) {
            CombatResultBroadcast.UnitLossDTO loss = defenderLosses.get(i);
            if (!loss.getOwnerClientId().equals(multiplayerState.getMyClientId())) {
                currentGameSession.removeVisibleEnemyUnit(loss.getUnitId());
            }
        }
    }
    
    private void showCombatResultDialog(CombatResultBroadcast broadcast) {
        if (broadcast.getCombatResult() == null) {
            return;
        }
        
        CombatResult localResult = new CombatResult(
                broadcast.getCombatResult().getAttackerDice(),
                broadcast.getCombatResult().getDefenderDice(),
                broadcast.getCombatResult().getAttackerHits(),
                broadcast.getCombatResult().getDefenderHits()
        );
        
        CombatResultDialog dialog = new CombatResultDialog(localResult);
        dialog.setTitle("Combat: " + broadcast.getAttackerUsername() + " vs " + broadcast.getDefenderUsername());
        dialog.setVisible(true);
    }

    private void handleFogRevealBroadcast(civgame.network.message.game.FogRevealBroadcast broadcast) {
        if (currentGameSession == null || currentGameFrame == null) {
            return;
        }
        civgame.model.map.HexMap localMap = currentGameSession.getHexMap();
        List<civgame.network.message.game.FogRevealBroadcast.RevealedTileDTO> tiles = broadcast.getRevealedTiles();
        boolean updated = false;
        for (int i = 0; i < tiles.size(); i++) {
            civgame.network.message.game.FogRevealBroadcast.RevealedTileDTO dto = tiles.get(i);
            HexCoordinate coordinate = new HexCoordinate(dto.getQ(), dto.getR());
            civgame.model.map.TileVisibility newVisibility = civgame.model.map.TileVisibility.valueOf(dto.getVisibility());
            HexTile existingTile = localMap.getTile(coordinate);
            if (existingTile != null && newVisibility.ordinal() >= existingTile.getVisibility().ordinal()) {
                try {
                    TerrainType terrain = TerrainType.valueOf(dto.getTerrainType());
                    civgame.model.map.TileResourceType resource = civgame.model.map.TileResourceType.valueOf(dto.getResourceType());
                    HexTile newTile = new HexTile(coordinate, terrain, resource);
                    newTile.setVisibility(newVisibility);
                    localMap.setTile(coordinate, newTile);
                    updated = true;
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        if (updated) {
            currentGameFrame.getMapPanel().repaint();
        }
    }

    private void handleToggleCheatsBroadcast(civgame.network.message.lobby.ToggleCheatsBroadcast broadcast) {
        lobbyFrame.getLobbyPanel().updateCheatsStatus(broadcast.isEnabled());
    }
}