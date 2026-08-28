package civgame.network.message;

public final class MessageType {
    
    public static final String CONNECT_REQUEST = "CONNECT_REQUEST";
    public static final String CONNECT_RESPONSE = "CONNECT_RESPONSE";
    public static final String DISCONNECT_BROADCAST = "DISCONNECT_BROADCAST";
    public static final String ERROR_RESPONSE = "ERROR_RESPONSE";
    public static final String PING_REQUEST = "PING_REQUEST";
    public static final String PONG_RESPONSE = "PONG_RESPONSE";
    
    private MessageType() {
    }
}