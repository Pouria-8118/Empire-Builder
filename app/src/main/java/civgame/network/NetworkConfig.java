package civgame.network;

public class NetworkConfig {

    public static final String DEFAULT_SERVER_IP = "localhost";
    public static final int DEFAULT_TCP_PORT = 9999;
    public static final int DEFAULT_UDP_PORT = 9998;
    public static final int CONNECTION_TIMEOUT_MS = 5000;
    public static final int READ_TIMEOUT_MS = 30000;
    public static final int MAX_MESSAGE_SIZE = 65536;

    private String serverIp;
    private int tcpPort;
    private int udpPort;

    public NetworkConfig() {
        this.serverIp = DEFAULT_SERVER_IP;
        this.tcpPort = DEFAULT_TCP_PORT;
        this.udpPort = DEFAULT_UDP_PORT;
    }

    public NetworkConfig(String serverIp, int tcpPort, int udpPort) {
        this.serverIp = serverIp;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
    }

    public String getServerIp() {
        return serverIp;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }
}