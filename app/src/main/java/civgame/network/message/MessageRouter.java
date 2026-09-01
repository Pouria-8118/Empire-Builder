package civgame.network.message;

import civgame.network.JsonSerializer;
import civgame.network.NetworkException;

import java.util.HashMap;
import java.util.Map;

public class MessageRouter {
    
    private final JsonSerializer jsonSerializer;
    private final Map<String, Class<? extends Message>> messageTypes;
    
    public MessageRouter() {
        this.jsonSerializer = new JsonSerializer();
        this.messageTypes = new HashMap<>();
        registerDefaultMessageTypes();
    }
    
    private void registerDefaultMessageTypes() {
        registerMessageType(MessageType.CONNECT_REQUEST, ConnectRequest.class);
        registerMessageType(MessageType.CONNECT_RESPONSE, ConnectResponse.class);
        registerMessageType(MessageType.DISCONNECT_BROADCAST, DisconnectBroadcast.class);
        registerMessageType(MessageType.ERROR_RESPONSE, ErrorResponse.class);
        registerMessageType(MessageType.PING_REQUEST, PingRequest.class);
        registerMessageType(MessageType.PONG_RESPONSE, PongResponse.class);
        
        registerMessageType(MessageType.JOIN_LOBBY_REQUEST, civgame.network.message.lobby.JoinLobbyRequest.class);
        registerMessageType(MessageType.JOIN_LOBBY_RESPONSE, civgame.network.message.lobby.JoinLobbyResponse.class);
        registerMessageType(MessageType.LEAVE_LOBBY_REQUEST, civgame.network.message.lobby.LeaveLobbyRequest.class);
        registerMessageType(MessageType.PLAYER_JOINED_BROADCAST, civgame.network.message.lobby.PlayerJoinedBroadcast.class);
        registerMessageType(MessageType.PLAYER_LEFT_BROADCAST, civgame.network.message.lobby.PlayerLeftBroadcast.class);
        registerMessageType(MessageType.SET_READY_REQUEST, civgame.network.message.lobby.SetReadyRequest.class);
        registerMessageType(MessageType.PLAYER_READY_BROADCAST, civgame.network.message.lobby.PlayerReadyBroadcast.class);
        registerMessageType(MessageType.SELECT_MAP_REQUEST, civgame.network.message.lobby.SelectMapRequest.class);
        registerMessageType(MessageType.MAP_SELECTED_BROADCAST, civgame.network.message.lobby.MapSelectedBroadcast.class);
        registerMessageType(MessageType.START_GAME_REQUEST, civgame.network.message.lobby.StartGameRequest.class);
        registerMessageType(MessageType.START_GAME_RESPONSE, civgame.network.message.lobby.StartGameResponse.class);
        
        registerMessageType(MessageType.CHAT_MESSAGE_REQUEST, civgame.network.message.chat.ChatMessageRequest.class);
        registerMessageType(MessageType.CHAT_MESSAGE_BROADCAST, civgame.network.message.chat.ChatMessageBroadcast.class);
    }
    
    public void registerMessageType(String type, Class<? extends Message> messageClass) {
        messageTypes.put(type, messageClass);
    }
    
    public String serializeMessage(Message message) throws NetworkException {
        return jsonSerializer.serialize(message);
    }
    
    public Message deserializeMessage(String json) throws NetworkException {
        Message baseMessage = jsonSerializer.deserialize(json, Message.class);
        
        if (baseMessage == null || baseMessage.getType() == null) {
            throw new NetworkException("Invalid message format: missing type");
        }
        
        String messageType = baseMessage.getType();
        Class<? extends Message> targetClass = messageTypes.get(messageType);
        
        if (targetClass == null) {
            throw new NetworkException("Unknown message type: " + messageType);
        }
        
        return jsonSerializer.deserialize(json, targetClass);
    }
    
    public void routeMessage(Message message, MessageHandler handler) throws NetworkException {
        if (message == null) {
            throw new NetworkException("Cannot route null message");
        }
        
        if (handler == null) {
            throw new NetworkException("Cannot route message to null handler");
        }
        
        if (message instanceof Request) {
            handler.handleRequest((Request) message);
        } else if (message instanceof Response) {
            handler.handleResponse((Response) message);
        } else if (message instanceof Broadcast) {
            handler.handleBroadcast((Broadcast) message);
        } else {
            throw new NetworkException("Unknown message category: " + message.getClass().getSimpleName());
        }
    }
    
    public void routeJsonMessage(String json, MessageHandler handler) throws NetworkException {
        Message message = deserializeMessage(json);
        routeMessage(message, handler);
    }
    
    public boolean isKnownMessageType(String type) {
        return messageTypes.containsKey(type);
    }
}