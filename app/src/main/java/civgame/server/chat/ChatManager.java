package civgame.server.chat;

import civgame.network.NetworkException;
import civgame.network.message.chat.ChatMessageBroadcast;
import civgame.server.ClientHandler;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ChatManager {
    
    private final SimpleDateFormat timeFormat;
    
    public ChatManager() {
        this.timeFormat = new SimpleDateFormat("HH:mm");
    }
    
    public ChatMessageBroadcast createChatBroadcast(String senderClientId, 
                                                     String senderUsername, 
                                                     String content) {
        long sentAt = System.currentTimeMillis();
        String messageId = UUID.randomUUID().toString();
        LocalDateTime createdAt = LocalDateTime.now();
        return new ChatMessageBroadcast("server", messageId, senderUsername, content, sentAt, createdAt);
    }
    
    public void broadcastToClients(ChatMessageBroadcast broadcast, List<ClientHandler> clients) {
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler handler = clients.get(i);
            try {
                handler.sendMessageObject(broadcast);
            } catch (NetworkException e) {
                System.err.println("Failed to send chat to " + handler.getClientId() + ": " + e.getMessage());
            }
        }
    }
    
    public String formatTimestamp(long timestamp) {
        return timeFormat.format(new Date(timestamp));
    }
}