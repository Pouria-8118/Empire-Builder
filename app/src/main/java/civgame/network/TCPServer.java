package civgame.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class TCPServer {

    private final NetworkConfig config;
    private ServerSocket serverSocket;
    private boolean running;
    private final List<ClientConnection> connectedClients;

    public TCPServer(NetworkConfig config) {
        this.config = config;
        this.running = false;
        this.connectedClients = new ArrayList<>();
    }

    public void start() throws NetworkException {
        try {
            serverSocket = new ServerSocket(config.getTcpPort());
            serverSocket.setSoTimeout(NetworkConfig.READ_TIMEOUT_MS);
            running = true;
            System.out.println("TCP Server started on port " + config.getTcpPort());
        } catch (IOException e) {
            throw new NetworkException("Failed to start TCP server: " + e.getMessage(), e);
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
        System.out.println("TCP Server stopped");
    }

    public boolean isRunning() {
        return running;
    }

    public ClientConnection acceptClient() throws NetworkException {
        try {
            Socket clientSocket = serverSocket.accept();
            clientSocket.setKeepAlive(true);
            clientSocket.setTcpNoDelay(true);
            ClientConnection connection = new ClientConnection(clientSocket);
            connectedClients.add(connection);
            System.out.println("Client connected: " + clientSocket.getInetAddress());
            return connection;
        } catch (SocketTimeoutException e) {
            return null;
        } catch (IOException e) {
            throw new NetworkException("Failed to accept client: " + e.getMessage(), e);
        }
    }

    public void removeClient(ClientConnection connection) {
        connectedClients.remove(connection);
        connection.close();
    }

    public List<ClientConnection> getConnectedClients() {
        return new ArrayList<>(connectedClients);
    }

    public static class ClientConnection {

        private final Socket socket;
        private final PrintWriter out;
        private final BufferedReader in;
        private final Object writeLock = new Object();

        public ClientConnection(Socket socket) throws NetworkException {
            this.socket = socket;
            try {
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                throw new NetworkException("Failed to initialize client connection: " + e.getMessage(), e);
            }
        }

        public void sendMessage(String message) throws NetworkException {
            synchronized (writeLock) {
                try {
                    out.println(message);
                    out.flush();
                    if (out.checkError()) {
                        throw new NetworkException("PrintWriter reported an error");
                    }
                } catch (Exception e) {
                    throw new NetworkException("Failed to send message: " + e.getMessage(), e);
                }
            }
        }

        public String receiveMessage() throws NetworkException {
            try {
                while (true) {
                    try {
                        String line = in.readLine();
                        if (line == null) {
                            throw new NetworkException("Connection closed by client");
                        }
                        return line;
                    } catch (SocketTimeoutException e) {
                        continue;
                    }
                }
            } catch (IOException e) {
                throw new NetworkException("Failed to receive message: " + e.getMessage(), e);
            }
        }

        public void close() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }

        public boolean isConnected() {
            return socket != null && !socket.isClosed() && socket.isConnected();
        }

        public Socket getSocket() {
            return socket;
        }
    }
}