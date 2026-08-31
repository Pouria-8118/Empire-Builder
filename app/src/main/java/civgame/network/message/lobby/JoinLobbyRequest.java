package civgame.network.message.lobby;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class JoinLobbyRequest extends Request {
    
    private String username;
    private String lobbyId;
    
    public JoinLobbyRequest() {
        super();
        setType(MessageType.JOIN_LOBBY_REQUEST);
    }
    
    public JoinLobbyRequest(String senderId, String username, String lobbyId) {
        super(MessageType.JOIN_LOBBY_REQUEST, senderId);
        this.username = username;
        this.lobbyId = lobbyId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getLobbyId() {
        return lobbyId;
    }
    
    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }
}