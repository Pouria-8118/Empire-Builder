package civgame.network.message.diplomacy;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;
import civgame.model.diplomacy.PlayerRelation;

public class RelationUpdateBroadcast extends Broadcast {
    
    private String playerAClientId;
    private String playerAUsername;
    private String playerBClientId;
    private String playerBUsername;
    private String relation;
    
    public RelationUpdateBroadcast() {
        super();
        setType(MessageType.RELATION_UPDATE_BROADCAST);
    }
    
    public RelationUpdateBroadcast(String broadcasterId,
                                    String playerAClientId,
                                    String playerAUsername,
                                    String playerBClientId,
                                    String playerBUsername,
                                    PlayerRelation relation) {
        super(MessageType.RELATION_UPDATE_BROADCAST, broadcasterId);
        this.playerAClientId = playerAClientId;
        this.playerAUsername = playerAUsername;
        this.playerBClientId = playerBClientId;
        this.playerBUsername = playerBUsername;
        this.relation = relation.name();
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
    
    public String getRelation() {
        return relation;
    }
    
    public void setRelation(String relation) {
        this.relation = relation;
    }
    
    public PlayerRelation getRelationEnum() {
        try {
            return PlayerRelation.valueOf(relation);
        } catch (Exception e) {
            return PlayerRelation.NEUTRAL;
        }
    }
}