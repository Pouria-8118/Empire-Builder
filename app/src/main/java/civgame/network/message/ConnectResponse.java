package civgame.network.message;

public class ConnectResponse extends Response {
    
    private String assignedClientId;
    private String serverVersion;
    
    public ConnectResponse() {
        super();
        setType(MessageType.CONNECT_RESPONSE);
    }
    
    public ConnectResponse(String requestId, boolean success, String assignedClientId, String serverVersion) {
        super(MessageType.CONNECT_RESPONSE, requestId, success);
        this.assignedClientId = assignedClientId;
        this.serverVersion = serverVersion;
    }
    
    public ConnectResponse(String requestId, String errorMessage) {
        super(MessageType.CONNECT_RESPONSE, requestId, errorMessage);
        this.assignedClientId = null;
        this.serverVersion = null;
    }
    
    public String getAssignedClientId() {
        return assignedClientId;
    }
    
    public void setAssignedClientId(String assignedClientId) {
        this.assignedClientId = assignedClientId;
    }
    
    public String getServerVersion() {
        return serverVersion;
    }
    
    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }
}