package civgame.controller.lobby;

import civgame.client.GameClient;
import civgame.controller.chat.ChatController;
import civgame.network.message.chat.ChatMessageBroadcast;
import civgame.network.NetworkConfig;
import civgame.network.NetworkException;
import civgame.network.message.Message;
import civgame.network.message.MessageType;
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
import civgame.view.lobby.LobbyFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LobbyController {
    
    private final LobbyFrame lobbyFrame;
    private GameClient gameClient;
    private final String username;
    private String myClientId;
    private String currentLobbyId;
    private String currentHostClientId;
    private boolean isReady;
    
    private final ChatController chatController;
    
    public LobbyController(LobbyFrame lobbyFrame, String username) {
        this.lobbyFrame = lobbyFrame;
        this.username = username;
        this.myClientId = null;
        this.currentLobbyId = null;
        this.currentHostClientId = null;
        this.isReady = false;
        this.gameClient = null;
        this.chatController = new ChatController(lobbyFrame.getChatPanel());
        
        registerListeners();
    }
    
    private void registerListeners() {
        lobbyFrame.getLobbyPanel().getReadyButton().addActionListener(new ReadyButtonListener());
        lobbyFrame.getLobbyPanel().getStartButton().addActionListener(new StartButtonListener());
        lobbyFrame.getLobbyPanel().getLeaveButton().addActionListener(new LeaveButtonListener());
        lobbyFrame.getLobbyPanel().getMapComboBox().addActionListener(new MapComboBoxListener());
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
                    chatController.setGameClient(gameClient, myClientId);
                    
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
        } else if (type.equals(MessageType.CHAT_MESSAGE_BROADCAST)) {
            chatController.handleChatBroadcast((ChatMessageBroadcast) message);
        } else if (type.equals(MessageType.ERROR_RESPONSE)) {
            handleErrorResponse((civgame.network.message.ErrorResponse) message);
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
        
        currentLobbyId = response.getLobbyId();
        currentHostClientId = response.getHostClientId();
        
        lobbyFrame.getLobbyPanel().updatePlayerList(response.getCurrentPlayers(), currentHostClientId);
        lobbyFrame.getLobbyPanel().updateMapSelection(response.getSelectedMapName());
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
        lobbyFrame.getLobbyPanel().updateMapSelection(broadcast.getMapName());
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
        
        lobbyFrame.getLobbyPanel().setStatusMessage("Game is starting...");
    }
    
    private void handleErrorResponse(civgame.network.message.ErrorResponse response) {
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
}