package civgame.server;

import civgame.network.NetworkException;
import civgame.network.TCPServer;

public class ClientHandler extends Thread {
    
    private final TCPServer.ClientConnection connection;
    private final String clientId;
    private final ConnectionManager connectionManager;
    private volatile boolean running;
    
    public ClientHandler(TCPServer.ClientConnection connection, 
                        String clientId, 
                        ConnectionManager connectionManager) {
        this.connection = connection;
        this.clientId = clientId;
        this.connectionManager = connectionManager;
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
        // TODO: Implement message routing
        System.out.println("Received from " + clientId + ": " + message);
        
        try {
            connection.sendMessage("ACK: " + message);
        } catch (NetworkException e) {
            System.err.println("Failed to send ACK to " + clientId + ": " + e.getMessage());
        }
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
}