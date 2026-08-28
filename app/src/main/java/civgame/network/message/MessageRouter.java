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