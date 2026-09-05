package civgame.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TCPClient {

    private final NetworkConfig config;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected;

    public TCPClient(NetworkConfig config) {
        this.config = config;
        this.connected = false;
    }

    public void connect() throws NetworkException {
        try {
            socket = new Socket(config.getServerIp(), config.getTcpPort());
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;
            System.out.println("Connected to server at " + config.getServerIp() + ":" + config.getTcpPort());
        } catch (IOException e) {
            throw new NetworkException("Failed to connect to server: " + e.getMessage(), e);
        }
    }

    public void disconnect() {
        connected = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }

    public void sendMessage(String message) throws NetworkException {
        if (!isConnected()) {
            throw new NetworkException("Client is not connected");
        }
        try {
            out.println(message);
            out.flush();
        } catch (Exception e) {
            throw new NetworkException("Failed to send message: " + e.getMessage(), e);
        }
    }

    public String receiveMessage() throws NetworkException {
        if (!isConnected()) {
            throw new NetworkException("Client is not connected");
        }
        try {
            return in.readLine();
        } catch (SocketTimeoutException e) {
            return null;
        } catch (IOException e) {
            throw new NetworkException("Failed to receive message: " + e.getMessage(), e);
        }
    }

    public String receiveMessageBlocking() throws NetworkException {
        if (!isConnected()) {
            throw new NetworkException("Client is not connected");
        }
        try {
            while (connected) {
                try {
                    String message = in.readLine();
                    if (message == null) {
                        throw new NetworkException("Connection closed by server");
                    }
                    return message;
                } catch (SocketTimeoutException e) {
                    continue;
                }
            }
            throw new NetworkException("Client disconnected");
        } catch (IOException e) {
            throw new NetworkException("Failed to receive message: " + e.getMessage(), e);
        }
    }

    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed() && socket.isConnected();
    }

    public Socket getSocket() {
        return socket;
    }
}