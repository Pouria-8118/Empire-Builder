package civgame.controller.chat;

import civgame.client.GameClient;
import civgame.network.NetworkException;
import civgame.network.message.chat.ChatMessageBroadcast;
import civgame.network.message.chat.ChatMessageRequest;
import civgame.view.chat.ChatPanel;

public class ChatController {
    
    private final ChatPanel chatPanel;
    private GameClient gameClient;
    private String myClientId;
    
    public ChatController(ChatPanel chatPanel) {
        this.chatPanel = chatPanel;
        this.gameClient = null;
        this.myClientId = null;
        
        setupListener();
    }
    
    private void setupListener() {
        chatPanel.setChatMessageListener(new ChatPanel.ChatMessageListener() {
            @Override
            public void onMessageSent(String content) {
                sendChatMessage(content);
            }
        });
    }
    
    public void setGameClient(GameClient gameClient, String myClientId) {
        this.gameClient = gameClient;
        this.myClientId = myClientId;
    }
    
    public void sendChatMessage(String content) {
        if (gameClient == null || !gameClient.isConnected()) {
            chatPanel.appendSystemMessage("Not connected to server");
            return;
        }
        
        if (content == null || content.trim().isEmpty()) {
            return;
        }
        
        ChatMessageRequest request = new ChatMessageRequest(myClientId, content.trim());
        try {
            gameClient.sendMessageObject(request);
        } catch (NetworkException e) {
            chatPanel.appendSystemMessage("Failed to send message: " + e.getMessage());
        }
    }
    
    public void handleChatBroadcast(ChatMessageBroadcast broadcast) {
        chatPanel.appendChatMessage(broadcast);
    }
    
    public void clearChat() {
        chatPanel.clearMessages();
    }
}