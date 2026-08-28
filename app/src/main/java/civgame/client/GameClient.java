package civgame.client;

import civgame.network.NetworkConfig;
import civgame.network.NetworkException;
import civgame.network.message.Message;

public class GameClient {
    
    private final NetworkConfig config;
    private final ClientNetworkManager networkManager;
    private GameClientCallback clientCallback;
    
    public GameClient(NetworkConfig config) {
        this.config = config;
        this.networkManager = new ClientNetworkManager(config);
        this.clientCallback = null;
        
        setupNetworkCallbacks();
    }
    
    private void setupNetworkCallbacks() {
        networkManager.setEventCallback(new ClientNetworkManager.ClientEventCallback() {
            @Override
            public void onMessageReceived(String message) {
                if (clientCallback != null) {
                    clientCallback.onServerMessage(message);
                }
            }
            
            @Override
            public void onMessageObjectReceived(Message message) {
                if (clientCallback != null) {
                    clientCallback.onServerMessageObject(message);
                }
            }
            
            @Override
            public void onConnectionLost(String errorMessage) {
                if (clientCallback != null) {
                    clientCallback.onDisconnected(errorMessage);
                }
            }
        });
    }
    
    public void connect() throws NetworkException {
        networkManager.connectToServer();
        
        if (clientCallback != null) {
            clientCallback.onConnected();
        }
    }
    
    public void disconnect() {
        networkManager.disconnectFromServer();
        
        if (clientCallback != null) {
            clientCallback.onDisconnected("Client disconnected");
        }
    }
    
    public boolean isConnected() {
        return networkManager.isConnected();
    }
    
    public void sendMessage(String message) throws NetworkException {
        networkManager.sendMessage(message);
    }
    
    public void sendMessageObject(Message message) throws NetworkException {
        networkManager.sendMessageObject(message);
    }
    
    public void sendMessageAsync(String message) {
        networkManager.sendMessageAsync(message);
    }
    
    public void sendMessageObjectAsync(Message message) {
        networkManager.sendMessageObjectAsync(message);
    }
    
    public void sendUDPMessage(String message) throws NetworkException {
        networkManager.sendUDPMessage(message);
    }
    
    public void setClientCallback(GameClientCallback callback) {
        this.clientCallback = callback;
    }
    
    public ClientNetworkManager getNetworkManager() {
        return networkManager;
    }
    
    public NetworkConfig getConfig() {
        return config;
    }
    
    public interface GameClientCallback {
        void onConnected();
        void onDisconnected(String reason);
        void onServerMessage(String message);
        void onServerMessageObject(Message message);
    }
}