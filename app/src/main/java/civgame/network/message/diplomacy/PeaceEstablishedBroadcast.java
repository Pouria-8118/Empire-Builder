package civgame.network.message.diplomacy;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;

public class PeaceEstablishedBroadcast extends Broadcast {
    
    private String playerAClientId;
    private String playerAUsername;
    private String playerBClientId;
    private String playerBUsername;
    
    public PeaceEstablishedBroadcast() {
        super();
        setType(MessageType.PEACE_ESTABLISHED_BROADCAST);
    }
    
    public PeaceEstablishedBroadcast(String broadcasterId,
                                      String playerAClientId,
                                      String playerAUsername,
                                      String playerBClientId,
                                      String playerBUsername) {
        super(MessageType.PEACE_ESTABLISHED_BROADCAST, broadcasterId);
        this.playerAClientId = playerAClientId;
        this.playerAUsername = playerAUsername;
        this.playerBClientId = playerBClientId;
        this.playerBUsername = playerBUsername;
    }
    
    public String getPlayerAClientId() {
        return playerAClientId;
    }
    
    public void setPlayerAClientId(String playerAClientId) {
        this.playerAClientId = playerAClientId;
    }
    
    public String getPlayerAUsername() {
        return playerAUsername;
    }
    
    public void setPlayerAUsername(String playerAUsername) {
        this.playerAUsername = playerAUsername;
    }
    
    public String getPlayerBClientId() {
        return playerBClientId;
    }
    
    public void setPlayerBClientId(String playerBClientId) {
        this.playerBClientId = playerBClientId;
    }
    
    public String getPlayerBUsername() {
        return playerBUsername;
    }
    
    public void setPlayerBUsername(String playerBUsername) {
        this.playerBUsername = playerBUsername;
    }
}