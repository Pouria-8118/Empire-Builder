package civgame.client;

import civgame.network.NetworkException;
import civgame.network.TCPClient;

public class ServerListener extends Thread {
    
    private final TCPClient tcpClient;
    private final ServerMessageHandler messageHandler;
    private volatile boolean running;
    
    public ServerListener(TCPClient tcpClient, ServerMessageHandler messageHandler) {
        this.tcpClient = tcpClient;
        this.messageHandler = messageHandler;
        this.running = false;
        this.setName("ServerListenerThread");
    }
    
    @Override
    public void run() {
        running = true;
        System.out.println("ServerListener started");
        while (running && tcpClient.isConnected()) {
            try {
                String message = tcpClient.receiveMessageBlocking();
                if (message != null) {
                    try {
                        messageHandler.onMessageReceived(message);
                    } catch (Throwable t) {
                        System.err.println("Critical error processing message: " + t.getMessage());
                        t.printStackTrace();
                    }
                }
            } catch (NetworkException e) {
                if (running) {
                    System.err.println("Listener error: " + e.getMessage());
                    e.printStackTrace();
                    messageHandler.onConnectionError(e.getMessage());
                    break;
                }
            } catch (Throwable t) {
                if (running) {
                    System.err.println("Unexpected fatal error in listener: " + t.getMessage());
                    t.printStackTrace();
                    messageHandler.onConnectionError(t.getMessage());
                    break;
                }
            }
        }
        System.out.println("ServerListener stopped");
    }
    
    public void stopListener() {
        running = false;
    }
    
    public boolean isListenerRunning() {
        return running;
    }
    
    public interface ServerMessageHandler {
        void onMessageReceived(String message);
        void onConnectionError(String errorMessage);
    }
}