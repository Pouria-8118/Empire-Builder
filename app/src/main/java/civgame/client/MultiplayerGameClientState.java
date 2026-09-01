package civgame.client;

import civgame.network.message.game.GameStartBroadcast;

public class MultiplayerGameClientState {

    private final String gameId;
    private final String myClientId;
    private final String myUsername;
    private final int myPlayerIndex;
    private final GameStartBroadcast initialSnapshot;
    private String currentTurnClientId;
    private int currentTurnNumber;

    public MultiplayerGameClientState(GameStartBroadcast initialSnapshot) {
        this.gameId = initialSnapshot.getGameId();
        this.myClientId = initialSnapshot.getMyClientId();
        this.myUsername = initialSnapshot.getMyUsername();
        this.myPlayerIndex = initialSnapshot.getMyPlayerIndex();
        this.initialSnapshot = initialSnapshot;
        this.currentTurnClientId = initialSnapshot.getInitialTurnClientId();
        this.currentTurnNumber = initialSnapshot.getInitialTurnNumber();
    }

    public String getGameId() {
        return gameId;
    }

    public String getMyClientId() {
        return myClientId;
    }

    public String getMyUsername() {
        return myUsername;
    }

    public int getMyPlayerIndex() {
        return myPlayerIndex;
    }

    public GameStartBroadcast getInitialSnapshot() {
        return initialSnapshot;
    }

    public String getCurrentTurnClientId() {
        return currentTurnClientId;
    }

    public void setCurrentTurnClientId(String currentTurnClientId) {
        this.currentTurnClientId = currentTurnClientId;
    }

    public int getCurrentTurnNumber() {
        return currentTurnNumber;
    }

    public void setCurrentTurnNumber(int currentTurnNumber) {
        this.currentTurnNumber = currentTurnNumber;
    }

    public boolean isMyTurn() {
        return myClientId != null && myClientId.equals(currentTurnClientId);
    }
}