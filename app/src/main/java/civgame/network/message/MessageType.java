package civgame.network.message;

public final class MessageType {
    
    public static final String CONNECT_REQUEST = "CONNECT_REQUEST";
    public static final String CONNECT_RESPONSE = "CONNECT_RESPONSE";
    public static final String DISCONNECT_BROADCAST = "DISCONNECT_BROADCAST";
    public static final String ERROR_RESPONSE = "ERROR_RESPONSE";
    public static final String PING_REQUEST = "PING_REQUEST";
    public static final String PONG_RESPONSE = "PONG_RESPONSE";
    
    public static final String JOIN_LOBBY_REQUEST = "JOIN_LOBBY_REQUEST";
    public static final String JOIN_LOBBY_RESPONSE = "JOIN_LOBBY_RESPONSE";
    public static final String LEAVE_LOBBY_REQUEST = "LEAVE_LOBBY_REQUEST";
    public static final String PLAYER_JOINED_BROADCAST = "PLAYER_JOINED_BROADCAST";
    public static final String PLAYER_LEFT_BROADCAST = "PLAYER_LEFT_BROADCAST";
    public static final String SET_READY_REQUEST = "SET_READY_REQUEST";
    public static final String PLAYER_READY_BROADCAST = "PLAYER_READY_BROADCAST";
    public static final String SELECT_MAP_REQUEST = "SELECT_MAP_REQUEST";
    public static final String MAP_SELECTED_BROADCAST = "MAP_SELECTED_BROADCAST";
    public static final String START_GAME_REQUEST = "START_GAME_REQUEST";
    public static final String START_GAME_RESPONSE = "START_GAME_RESPONSE";
    
    public static final String CHAT_MESSAGE_REQUEST = "CHAT_MESSAGE_REQUEST";
    public static final String CHAT_MESSAGE_BROADCAST = "CHAT_MESSAGE_BROADCAST";
    
    public static final String GAME_START_BROADCAST = "GAME_START_BROADCAST";
    public static final String TURN_UPDATE_BROADCAST = "TURN_UPDATE_BROADCAST";
    public static final String GAME_OVER_BROADCAST = "GAME_OVER_BROADCAST";
    public static final String END_TURN_REQUEST = "END_TURN_REQUEST";
    public static final String END_TURN_RESPONSE = "END_TURN_RESPONSE";
    
    private MessageType() {
    }
}