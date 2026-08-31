package civgame.server.lobby;

public enum LobbyState {
    
    WAITING,
    STARTING,
    IN_GAME,
    CLOSED;
    
    public boolean isAcceptingPlayers() {
        return this == WAITING;
    }
    
    public boolean isGameInProgress() {
        return this == IN_GAME;
    }
    
    public boolean isClosed() {
        return this == CLOSED;
    }
}