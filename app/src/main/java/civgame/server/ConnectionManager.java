package civgame.server;

import civgame.network.TCPServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    
    private final Map<String, TCPServer.ClientConnection> activeConnections;
    
    public ConnectionManager() {
        this.activeConnections = new ConcurrentHashMap<>();
    }
    
    public void addConnection(String clientId, TCPServer.ClientConnection connection) {
        activeConnections.put(clientId, connection);
        System.out.println("Client added: " + clientId + " (Total: " + activeConnections.size() + ")");
    }
    
    public void removeConnection(String clientId) {
        TCPServer.ClientConnection removed = activeConnections.remove(clientId);
        if (removed != null) {
            removed.close();
            System.out.println("Client removed: " + clientId + " (Total: " + activeConnections.size() + ")");
        }
    }
    
    public TCPServer.ClientConnection getConnection(String clientId) {
        return activeConnections.get(clientId);
    }
    
    public List<TCPServer.ClientConnection> getAllConnections() {
        return new ArrayList<>(activeConnections.values());
    }
    
    public List<String> getAllClientIds() {
        return new ArrayList<>(activeConnections.keySet());
    }
    
    public boolean hasConnection(String clientId) {
        return activeConnections.containsKey(clientId);
    }
    
    public int getConnectionCount() {
        return activeConnections.size();
    }
    
    public void closeAllConnections() {
        for (TCPServer.ClientConnection connection : activeConnections.values()) {
            connection.close();
        }
        activeConnections.clear();
        System.out.println("All connections closed");
    }
}