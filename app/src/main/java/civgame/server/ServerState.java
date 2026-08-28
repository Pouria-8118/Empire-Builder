package civgame.server;

public enum ServerState {
    
    STARTING,
    RUNNING,
    STOPPED,
    ERROR;
    
    public boolean isRunning() {
        return this == RUNNING;
    }
    
    public boolean isStopped() {
        return this == STOPPED;
    }
    
    public boolean canAcceptConnections() {
        return this == RUNNING;
    }
}