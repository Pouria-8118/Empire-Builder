package civgame.network.message;

public class DisconnectBroadcast extends Broadcast {
    
    private String disconnectedClientId;
    private String reason;
    
    public DisconnectBroadcast() {
        super();
        setType(MessageType.DISCONNECT_BROADCAST);
    }
    
    public DisconnectBroadcast(String broadcasterId, String disconnectedClientId, String reason) {
        super(MessageType.DISCONNECT_BROADCAST, broadcasterId);
        this.disconnectedClientId = disconnectedClientId;
        this.reason = reason;
    }
    
    public String getDisconnectedClientId() {
        return disconnectedClientId;
    }
    
    public void setDisconnectedClientId(String disconnectedClientId) {
        this.disconnectedClientId = disconnectedClientId;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}