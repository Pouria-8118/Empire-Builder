package civgame.client;

import civgame.network.NetworkConfig;
import civgame.network.NetworkException;
import civgame.network.TCPClient;
import civgame.network.UDPClient;
import civgame.network.message.Message;
import civgame.network.message.MessageRouter;


import javax.swing.SwingUtilities;

public class ClientNetworkManager {
    
    private final NetworkConfig config;
    private TCPClient tcpClient;
    private UDPClient udpClient;
    private ServerListener serverListener;
    private final MessageRouter messageRouter;
    private volatile boolean connected;
    private ClientEventCallback eventCallback;
    
    public ClientNetworkManager(NetworkConfig config) {
        this.config = config;
        this.tcpClient = new TCPClient(config);
        this.udpClient = new UDPClient(config);
        this.messageRouter = new MessageRouter();
        this.connected = false;
        this.eventCallback = null;
    }
    
    public void setEventCallback(ClientEventCallback callback) {
        this.eventCallback = callback;
    }
    
    public void connectToServer() throws NetworkException {
        System.out.println("Connecting to server at " + config.getServerIp() + ":" + config.getTcpPort());
        
        tcpClient.connect();
        udpClient.start();
        
        connected = true;
        
        startServerListener();
        
        System.out.println("Connected to server successfully");
    }
    
    public void disconnectFromServer() {
        connected = false;
        
        if (serverListener != null) {
            serverListener.stopListener();
        }
        
        tcpClient.disconnect();
        udpClient.stop();
        
        System.out.println("Disconnected from server");
    }
    
    public boolean isConnected() {
        return connected && tcpClient.isConnected();
    }
    
    private void startServerListener() {
        ServerListener.ServerMessageHandler handler = new ServerListener.ServerMessageHandler() {
            @Override
            public void onMessageReceived(String message) {
                handleIncomingMessage(message);
            }
            
            @Override
            public void onConnectionError(String errorMessage) {
                handleConnectionError(errorMessage);
            }
        };
        
        serverListener = new ServerListener(tcpClient, handler);
        serverListener.start();
    }
    
    private void handleIncomingMessage(String message) {
        System.out.println("Received message: " + message);
        
        try {
            Message parsedMessage = messageRouter.deserializeMessage(message);
            
            if (eventCallback != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        eventCallback.onMessageObjectReceived(parsedMessage);
                    }
                });
            }
            
        } catch (NetworkException e) {
            System.err.println("Failed to parse message: " + e.getMessage());
            
            if (eventCallback != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        eventCallback.onMessageReceived(message);
                    }
                });
            }
        }
    }
    
    private void handleConnectionError(String errorMessage) {
        connected = false;
        
        if (eventCallback != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    eventCallback.onConnectionLost(errorMessage);
                }
            });
        }
    }
    
    public void sendMessage(String message) throws NetworkException {
        if (!isConnected()) {
            throw new NetworkException("Not connected to server");
        }
        
        tcpClient.sendMessage(message);
    }
    
    public void sendMessageObject(Message message) throws NetworkException {
        String json = messageRouter.serializeMessage(message);
        sendMessage(json);
    }
    
    public void sendMessageAsync(String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendMessage(message);
                } catch (NetworkException e) {
                    System.err.println("Failed to send message: " + e.getMessage());
                }
            }
        }, "SendMessageThread").start();
    }
    
    public void sendMessageObjectAsync(Message message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendMessageObject(message);
                } catch (NetworkException e) {
                    System.err.println("Failed to send message object: " + e.getMessage());
                }
            }
        }, "SendMessageObjectThread").start();
    }
    
    public void sendUDPMessage(String message) throws NetworkException {
        udpClient.send(message);
    }
    
    public TCPClient getTcpClient() {
        return tcpClient;
    }
    
    public UDPClient getUdpClient() {
        return udpClient;
    }
    
    public NetworkConfig getConfig() {
        return config;
    }
    
    public interface ClientEventCallback {
        void onMessageReceived(String message);
        void onMessageObjectReceived(Message message);
        void onConnectionLost(String errorMessage);
    }
}