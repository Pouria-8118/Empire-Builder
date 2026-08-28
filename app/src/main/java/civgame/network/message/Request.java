package civgame.network.message;

public abstract class Request extends Message {
    
    private String requestId;
    private String senderId;
    
    public Request() {
        super();
    }
    
    public Request(String type, String senderId) {
        super(type);
        this.requestId = generateRequestId();
        this.senderId = senderId;
    }
    
    private String generateRequestId() {
        return "req_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}