package civgame.network.message;

public class ErrorResponse extends Response {
    
    private String errorCode;
    
    public ErrorResponse() {
        super();
        setType(MessageType.ERROR_RESPONSE);
    }
    
    public ErrorResponse(String requestId, String errorCode, String errorMessage) {
        super(MessageType.ERROR_RESPONSE, requestId, errorMessage);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}