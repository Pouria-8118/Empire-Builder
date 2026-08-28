package civgame.network.message;

public abstract class Broadcast extends Message {
    
    private String broadcasterId;
    
    public Broadcast() {
        super();
    }
    
    public Broadcast(String type, String broadcasterId) {
        super(type);
        this.broadcasterId = broadcasterId;
    }
    
    public String getBroadcasterId() {
        return broadcasterId;
    }
    
    public void setBroadcasterId(String broadcasterId) {
        this.broadcasterId = broadcasterId;
    }
}