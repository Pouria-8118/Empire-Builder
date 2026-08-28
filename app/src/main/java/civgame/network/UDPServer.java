package civgame.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class UDPServer {
    
    private final NetworkConfig config;
    private DatagramSocket socket;
    private boolean running;
    private final byte[] receiveBuffer;
    
    public UDPServer(NetworkConfig config) {
        this.config = config;
        this.running = false;
        this.receiveBuffer = new byte[NetworkConfig.MAX_MESSAGE_SIZE];
    }
    
    public void start() throws NetworkException {
        try {
            socket = new DatagramSocket(config.getUdpPort());
            socket.setSoTimeout(NetworkConfig.READ_TIMEOUT_MS);
            running = true;
            System.out.println("UDP Server started on port " + config.getUdpPort());
        } catch (IOException e) {
            throw new NetworkException("Failed to start UDP server: " + e.getMessage(), e);
        }
    }
    
    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        System.out.println("UDP Server stopped");
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public DatagramPacket receive() throws NetworkException {
        if (!running) {
            throw new NetworkException("UDP server is not running");
        }
        try {
            DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(packet);
            return packet;
        } catch (SocketTimeoutException e) {
            return null;
        } catch (IOException e) {
            throw new NetworkException("Failed to receive UDP packet: " + e.getMessage(), e);
        }
    }
    
    public void send(DatagramPacket packet) throws NetworkException {
        if (!running) {
            throw new NetworkException("UDP server is not running");
        }
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new NetworkException("Failed to send UDP packet: " + e.getMessage(), e);
        }
    }
    
    public DatagramSocket getSocket() {
        return socket;
    }
}