package civgame.server;

import civgame.network.NetworkException;
import civgame.network.TCPServer;
import civgame.network.message.Broadcast;
import civgame.network.message.Message;
import civgame.network.message.MessageHandler;
import civgame.network.message.MessageRouter;
import civgame.network.message.Request;
import civgame.network.message.Response;
import civgame.network.message.lobby.JoinLobbyRequest;
import civgame.network.message.lobby.LeaveLobbyRequest;
import civgame.network.message.lobby.SelectMapRequest;
import civgame.network.message.lobby.SetReadyRequest;
import civgame.network.message.lobby.StartGameRequest;
import civgame.network.message.game.EndTurnRequest;
import civgame.network.message.diplomacy.DeclareWarRequest;
import civgame.network.message.diplomacy.OfferPeaceRequest;
import civgame.network.message.diplomacy.PeaceResponseRequest;
import civgame.network.message.diplomacy.ProposeAllianceRequest;
import civgame.network.message.diplomacy.AllianceResponseRequest;

public class ClientHandler extends Thread {
    
    private final TCPServer.ClientConnection connection;
    private final String clientId;
    private final GameServer gameServer;
    private final MessageRouter messageRouter;
    private volatile boolean running;
    
    public ClientHandler(TCPServer.ClientConnection connection, 
                        String clientId, 
                        GameServer gameServer) {
        this.connection = connection;
        this.clientId = clientId;
        this.gameServer = gameServer;
        this.messageRouter = new MessageRouter();
        this.running = true;
        this.setName("ClientHandler-" + clientId);
    }
    
    @Override
    public void run() {
        System.out.println("ClientHandler started for client: " + clientId);
        
        while (running && connection.isConnected()) {
            try {
                String message = connection.receiveMessage();
                
                if (message == null) {
                    handleDisconnection();
                    break;
                }
                
                handleMessage(message);
                
            } catch (NetworkException e) {
                System.err.println("Error in ClientHandler for " + clientId + ": " + e.getMessage());
                handleDisconnection();
                break;
            }
        }
        
        System.out.println("ClientHandler stopped for client: " + clientId);
    }
    
    private void handleMessage(String message) {
        System.out.println("Received from " + clientId + ": " + message);
        
        try {
            Message parsedMessage = messageRouter.deserializeMessage(message);
            messageRouter.routeMessage(parsedMessage, createMessageHandler());
        } catch (NetworkException e) {
            System.err.println("Failed to process message from " + clientId + ": " + e.getMessage());
        }
    }
    
    private MessageHandler createMessageHandler() {
        return new MessageHandler() {
            @Override
            public void handleRequest(Request request) {
                processRequest(request);
            }
            
            @Override
            public void handleResponse(Response response) {
                processResponse(response);
            }
            
            @Override
            public void handleBroadcast(Broadcast broadcast) {
                processBroadcast(broadcast);
            }
        };
    }
    
    private void processRequest(Request request) {
        String type = request.getType();
        request.setSenderId(clientId);
        
        if (type.equals(civgame.network.message.MessageType.JOIN_LOBBY_REQUEST)) {
            gameServer.handleJoinLobbyRequest(clientId, (JoinLobbyRequest) request);
        } else if (type.equals(civgame.network.message.MessageType.LEAVE_LOBBY_REQUEST)) {
            gameServer.handleLeaveLobbyRequest(clientId, (LeaveLobbyRequest) request);
        } else if (type.equals(civgame.network.message.MessageType.SET_READY_REQUEST)) {
            gameServer.handleSetReadyRequest(clientId, (SetReadyRequest) request);
        } else if (type.equals(civgame.network.message.MessageType.SELECT_MAP_REQUEST)) {
            gameServer.handleSelectMapRequest(clientId, (SelectMapRequest) request);
        } else if (type.equals(civgame.network.message.MessageType.START_GAME_REQUEST)) {
            gameServer.handleStartGameRequest(clientId, (StartGameRequest) request);
        } else if (type.equals(civgame.network.message.MessageType.CHAT_MESSAGE_REQUEST)) {
            gameServer.handleChatMessageRequest(clientId, (civgame.network.message.chat.ChatMessageRequest) request);
        } else if (type.equals(civgame.network.message.MessageType.END_TURN_REQUEST)) {
            gameServer.handleEndTurnRequest(clientId, (EndTurnRequest) request);
        } else if (type.equals(civgame.network.message.MessageType.DECLARE_WAR_REQUEST)) {
            gameServer.handleDeclareWarRequest(clientId, (DeclareWarRequest) request);
        } else if (type.equals(civgame.network.message.MessageType.OFFER_PEACE_REQUEST)) {
            gameServer.handleOfferPeaceRequest(clientId, (OfferPeaceRequest) request);
        } else if (type.equals(civgame.network.message.MessageType.PEACE_RESPONSE_REQUEST)) {
            gameServer.handlePeaceResponseRequest(clientId, (PeaceResponseRequest) request);
        } else if (type.equals(civgame.network.message.MessageType.PROPOSE_ALLIANCE_REQUEST)) {
            gameServer.handleProposeAllianceRequest(clientId, (ProposeAllianceRequest) request);
        } else if (type.equals(civgame.network.message.MessageType.ALLIANCE_RESPONSE_REQUEST)) {
            gameServer.handleAllianceResponseRequest(clientId, (AllianceResponseRequest) request);
        } else {
            System.out.println("Processing unknown request from " + clientId + ": " + type);
        }
    }
    
    private void processResponse(Response response) {
        System.out.println("Processing response from " + clientId + ": " + response.getType());
    }
    
    private void processBroadcast(Broadcast broadcast) {
        System.out.println("Processing broadcast from " + clientId + ": " + broadcast.getType());
    }
    
    private void handleDisconnection() {
        System.out.println("Client disconnected: " + clientId);
        gameServer.handleClientDisconnect(clientId);
        running = false;
    }
    
    public void stopHandler() {
        running = false;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public boolean isHandlerRunning() {
        return running;
    }
    
    public void sendMessage(String message) throws NetworkException {
        if (running && connection.isConnected()) {
            connection.sendMessage(message);
        }
    }
    
    public void sendMessageObject(Message message) throws NetworkException {
        String json = messageRouter.serializeMessage(message);
        sendMessage(json);
    }
}