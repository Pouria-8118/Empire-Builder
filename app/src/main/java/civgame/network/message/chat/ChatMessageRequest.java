package civgame.network.message.chat;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class ChatMessageRequest extends Request {
    
    private String content;
    
    public ChatMessageRequest() {
        super();
        setType(MessageType.CHAT_MESSAGE_REQUEST);
    }
    
    public ChatMessageRequest(String senderId, String content) {
        super(MessageType.CHAT_MESSAGE_REQUEST, senderId);
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}