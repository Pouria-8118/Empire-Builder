package civgame.network.message;

public interface MessageHandler {
    
    void handleRequest(Request request);
    
    void handleResponse(Response response);
    
    void handleBroadcast(Broadcast broadcast);
}