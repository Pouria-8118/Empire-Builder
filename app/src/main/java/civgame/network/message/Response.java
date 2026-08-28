package civgame.network.message;

public abstract class Response extends Message {
    
    private String requestId;
    private boolean success;
    private String errorMessage;
    
    public Response() {
        super();
    }
    
    public Response(String type, String requestId, boolean success) {
        super(type);
        this.requestId = requestId;
        this.success = success;
        this.errorMessage = null;
    }
    
    public Response(String type, String requestId, String errorMessage) {
        super(type);
        this.requestId = requestId;
        this.success = false;
        this.errorMessage = errorMessage;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}