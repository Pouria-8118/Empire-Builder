package civgame.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class UDPClient {
    
    private final NetworkConfig config;
    private DatagramSocket socket;
    private final byte[] receiveBuffer;
    
    public UDPClient(NetworkConfig config) {
        this.config = config;
        this.receiveBuffer = new byte[NetworkConfig.MAX_MESSAGE_SIZE];
    }
    
    public void start() throws NetworkException {
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(NetworkConfig.READ_TIMEOUT_MS);
        } catch (IOException e) {
            throw new NetworkException("Failed to start UDP client: " + e.getMessage(), e);
        }
    }
    
    public void stop() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
    
    public void send(String message) throws NetworkException {
        if (socket == null || socket.isClosed()) {
            throw new NetworkException("UDP client is not started");
        }
        try {
            byte[] data = message.getBytes();
            InetAddress serverAddress = InetAddress.getByName(config.getServerIp());
            DatagramPacket packet = new DatagramPacket(
                    data, 
                    data.length, 
                    serverAddress, 
                    config.getUdpPort()
            );
            socket.send(packet);
        } catch (IOException e) {
            throw new NetworkException("Failed to send UDP message: " + e.getMessage(), e);
        }
    }
    
    public String receive() throws NetworkException {
        if (socket == null || socket.isClosed()) {
            throw new NetworkException("UDP client is not started");
        }
        try {
            DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(packet);
            return new String(packet.getData(), 0, packet.getLength());
        } catch (SocketTimeoutException e) {
            return null;
        } catch (IOException e) {
            throw new NetworkException("Failed to receive UDP message: " + e.getMessage(), e);
        }
    }
    
    public String receiveBlocking() throws NetworkException {
        if (socket == null || socket.isClosed()) {
            throw new NetworkException("UDP client is not started");
        }
        try {
            DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(packet);
            return new String(packet.getData(), 0, packet.getLength());
        } catch (IOException e) {
            throw new NetworkException("Failed to receive UDP message: " + e.getMessage(), e);
        }
    }
}