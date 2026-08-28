package civgame.network.message;

public abstract class Message {
    
    private String type;
    private long timestamp;
    
    public Message() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public Message(String type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}