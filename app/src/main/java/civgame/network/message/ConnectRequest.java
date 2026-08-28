package civgame.network.message;

public class ConnectRequest extends Request {
    
    private String username;
    private String clientVersion;
    
    public ConnectRequest() {
        super();
        setType(MessageType.CONNECT_REQUEST);
    }
    
    public ConnectRequest(String senderId, String username, String clientVersion) {
        super(MessageType.CONNECT_REQUEST, senderId);
        this.username = username;
        this.clientVersion = clientVersion;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getClientVersion() {
        return clientVersion;
    }
    
    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }
}