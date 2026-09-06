package civgame.network.message.game;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;

public class GameOverBroadcast extends Broadcast {
    private String winnerClientId;
    private String winnerUsername;

    public GameOverBroadcast() {
        super();
        setType(MessageType.GAME_OVER_BROADCAST);
    }

    public GameOverBroadcast(String broadcasterId, String winnerClientId, String winnerUsername) {
        super(MessageType.GAME_OVER_BROADCAST, broadcasterId);
        this.winnerClientId = winnerClientId;
        this.winnerUsername = winnerUsername;
    }

    public String getWinnerClientId() {
        return winnerClientId;
    }

    public void setWinnerClientId(String winnerClientId) {
        this.winnerClientId = winnerClientId;
    }

    public String getWinnerUsername() {
        return winnerUsername;
    }

    public void setWinnerUsername(String winnerUsername) {
        this.winnerUsername = winnerUsername;
    }
}