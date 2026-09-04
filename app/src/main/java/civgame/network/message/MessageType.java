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
    
    public static final String DECLARE_WAR_REQUEST = "DECLARE_WAR_REQUEST";
    public static final String DECLARE_WAR_BROADCAST = "DECLARE_WAR_BROADCAST";
    public static final String OFFER_PEACE_REQUEST = "OFFER_PEACE_REQUEST";
    public static final String PEACE_OFFER_BROADCAST = "PEACE_OFFER_BROADCAST";
    public static final String PEACE_RESPONSE_REQUEST = "PEACE_RESPONSE_REQUEST";
    public static final String PEACE_ESTABLISHED_BROADCAST = "PEACE_ESTABLISHED_BROADCAST";
    public static final String PROPOSE_ALLIANCE_REQUEST = "PROPOSE_ALLIANCE_REQUEST";
    public static final String ALLIANCE_PROPOSAL_BROADCAST = "ALLIANCE_PROPOSAL_BROADCAST";
    public static final String ALLIANCE_RESPONSE_REQUEST = "ALLIANCE_RESPONSE_REQUEST";
    public static final String ALLIANCE_ESTABLISHED_BROADCAST = "ALLIANCE_ESTABLISHED_BROADCAST";
    public static final String RELATION_UPDATE_BROADCAST = "RELATION_UPDATE_BROADCAST";
    public static final String DIPLOMACY_RESPONSE = "DIPLOMACY_RESPONSE";

    public static final String ATTACK_REQUEST = "ATTACK_REQUEST";
    public static final String ATTACK_RESPONSE = "ATTACK_RESPONSE";
    public static final String COMBAT_RESULT_BROADCAST = "COMBAT_RESULT_BROADCAST";
    public static final String TRADE_OFFER_REQUEST = "TRADE_OFFER_REQUEST";
    public static final String TRADE_ACCEPT_REQUEST = "TRADE_ACCEPT_REQUEST";
    public static final String TRADE_REJECT_REQUEST = "TRADE_REJECT_REQUEST";
    public static final String TRADE_CANCEL_REQUEST = "TRADE_CANCEL_REQUEST";
    public static final String TRADE_OFFER_BROADCAST = "TRADE_OFFER_BROADCAST";
    public static final String TRADE_INBOX_UPDATE_BROADCAST = "TRADE_INBOX_UPDATE_BROADCAST";
    public static final String ITEM_PRODUCE_REQUEST = "ITEM_PRODUCE_REQUEST";
    public static final String ITEM_USE_REQUEST = "ITEM_USE_REQUEST";
    public static final String ITEM_ACTION_RESPONSE = "ITEM_ACTION_RESPONSE";
    public static final String ITEM_ACTION_BROADCAST = "ITEM_ACTION_BROADCAST";
    
    private MessageType() {
    }
}