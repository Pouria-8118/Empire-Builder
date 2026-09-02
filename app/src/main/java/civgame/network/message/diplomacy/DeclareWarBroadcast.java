package civgame.network.message.diplomacy;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;

public class DeclareWarBroadcast extends Broadcast {
    
    private String aggressorClientId;
    private String aggressorUsername;
    private String targetClientId;
    private String targetUsername;
    
    public DeclareWarBroadcast() {
        super();
        setType(MessageType.DECLARE_WAR_BROADCAST);
    }
    
    public DeclareWarBroadcast(String broadcasterId,
                                String aggressorClientId,
                                String aggressorUsername,
                                String targetClientId,
                                String targetUsername) {
        super(MessageType.DECLARE_WAR_BROADCAST, broadcasterId);
        this.aggressorClientId = aggressorClientId;
        this.aggressorUsername = aggressorUsername;
        this.targetClientId = targetClientId;
        this.targetUsername = targetUsername;
    }
    
    public String getAggressorClientId() {
        return aggressorClientId;
    }
    
    public void setAggressorClientId(String aggressorClientId) {
        this.aggressorClientId = aggressorClientId;
    }
    
    public String getAggressorUsername() {
        return aggressorUsername;
    }
    
    public void setAggressorUsername(String aggressorUsername) {
        this.aggressorUsername = aggressorUsername;
    }
    
    public String getTargetClientId() {
        return targetClientId;
    }
    
    public void setTargetClientId(String targetClientId) {
        this.targetClientId = targetClientId;
    }
    
    public String getTargetUsername() {
        return targetUsername;
    }
    
    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }
}