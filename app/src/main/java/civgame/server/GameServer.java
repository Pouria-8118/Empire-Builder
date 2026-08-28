package civgame.server;

import civgame.network.NetworkConfig;
import civgame.network.NetworkException;
import civgame.network.TCPServer;
import civgame.network.UDPServer;

import java.util.ArrayList;
import java.util.List;

public class GameServer {
    
    private final NetworkConfig config;
    private final TCPServer tcpServer;
    private final UDPServer udpServer;
    private final ConnectionManager connectionManager;
    private ServerState state;
    private Thread acceptThread;
    private final List<ClientHandler> clientHandlers;
    
    public GameServer(NetworkConfig config) {
        this.config = config;
        this.tcpServer = new TCPServer(config);
        this.udpServer = new UDPServer(config);
        this.connectionManager = new ConnectionManager();
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
        
        for (ClientHandler handler : clientHandlers) {
            handler.stopHandler();
        }
        clientHandlers.clear();
        
        connectionManager.closeAllConnections();
        
        tcpServer.stop();
        udpServer.stop();
        
        System.out.println("Game Server stopped");
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
        
        ClientHandler handler = new ClientHandler(connection, clientId, connectionManager);
        clientHandlers.add(handler);
        handler.start();
        
        System.out.println("New client connected: " + clientId);
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