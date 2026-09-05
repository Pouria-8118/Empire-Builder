package civgame.network.message.chat;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;
import java.time.LocalDateTime;
import java.util.UUID;

public class ChatMessageBroadcast extends Broadcast {

    private String messageId;
    private LocalDateTime createdAt;
    private String username;
    private String content;
    private long sentAt;
    
    public ChatMessageBroadcast() {
        super();
        setType(MessageType.CHAT_MESSAGE_BROADCAST);
        this.messageId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }
    
    public ChatMessageBroadcast(String broadcasterId, String messageId, String username, String content, long sentAt, LocalDateTime createdAt) {
        super(MessageType.CHAT_MESSAGE_BROADCAST, broadcasterId);
        this.messageId = messageId;
        this.createdAt = createdAt;
        this.username = username;
        this.content = content;
        this.sentAt = sentAt;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageId() {
        return messageId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public long getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }
    
    public String getFormattedTime() {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("HH:mm");
        return dateFormat.format(new java.util.Date(sentAt));
    }
}