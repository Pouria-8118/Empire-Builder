package civgame.server;

import civgame.network.NetworkException;
import civgame.network.TCPServer;
import civgame.network.message.Broadcast;
import civgame.network.message.Message;
import civgame.network.message.MessageHandler;
import civgame.network.message.MessageRouter;
import civgame.network.message.Request;
import civgame.network.message.Response;

public class ClientHandler extends Thread {
    
    private final TCPServer.ClientConnection connection;
    private final String clientId;
    private final ConnectionManager connectionManager;
    private final MessageRouter messageRouter;
    private volatile boolean running;
    
    public ClientHandler(TCPServer.ClientConnection connection, 
                        String clientId, 
                        ConnectionManager connectionManager) {
        this.connection = connection;
        this.clientId = clientId;
        this.connectionManager = connectionManager;
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
        // TODO: Implement specific request handlers
        System.out.println("Processing request from " + clientId + ": " + request.getType());
    }
    
    private void processResponse(Response response) {
        System.out.println("Processing response from " + clientId + ": " + response.getType());
    }
    
    private void processBroadcast(Broadcast broadcast) {
        System.out.println("Processing broadcast from " + clientId + ": " + broadcast.getType());
    }
    
    private void handleDisconnection() {
        System.out.println("Client disconnected: " + clientId);
        connectionManager.removeConnection(clientId);
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