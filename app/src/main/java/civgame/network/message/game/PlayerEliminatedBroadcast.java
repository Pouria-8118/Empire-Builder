package civgame.network.message.game;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;

public class PlayerEliminatedBroadcast extends Broadcast {
    private String eliminatedClientId;
    private String eliminatedUsername;
    private String attackerClientId;
    private String attackerUsername;

    public PlayerEliminatedBroadcast() {
        super();
        setType(MessageType.PLAYER_ELIMINATED_BROADCAST);
    }

    public PlayerEliminatedBroadcast(String broadcasterId, String eliminatedClientId, String eliminatedUsername, String attackerClientId, String attackerUsername) {
        super(MessageType.PLAYER_ELIMINATED_BROADCAST, broadcasterId);
        this.eliminatedClientId = eliminatedClientId;
        this.eliminatedUsername = eliminatedUsername;
        this.attackerClientId = attackerClientId;
        this.attackerUsername = attackerUsername;
    }

    public String getEliminatedClientId() {
        return eliminatedClientId;
    }

    public void setEliminatedClientId(String eliminatedClientId) {
        this.eliminatedClientId = eliminatedClientId;
    }

    public String getEliminatedUsername() {
        return eliminatedUsername;
    }

    public void setEliminatedUsername(String eliminatedUsername) {
        this.eliminatedUsername = eliminatedUsername;
    }

    public String getAttackerClientId() {
        return attackerClientId;
    }

    public void setAttackerClientId(String attackerClientId) {
        this.attackerClientId = attackerClientId;
    }

    public String getAttackerUsername() {
        return attackerUsername;
    }

    public void setAttackerUsername(String attackerUsername) {
        this.attackerUsername = attackerUsername;
    }
}