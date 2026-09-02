package civgame.model.diplomacy;

import java.util.HashMap;
import java.util.Map;

public class PlayerRelationMap implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final Map<String, Map<String, PlayerRelation>> relations;
    
    public PlayerRelationMap() {
        this.relations = new HashMap<>();
    }
    
    public PlayerRelation getRelation(String playerA, String playerB) {
        if (playerA.equals(playerB)) {
            return PlayerRelation.ALLY;
        }
        
        Map<String, PlayerRelation> playerMap = relations.get(playerA);
        if (playerMap == null) {
            return PlayerRelation.NEUTRAL;
        }
        
        PlayerRelation relation = playerMap.get(playerB);
        if (relation == null) {
            return PlayerRelation.NEUTRAL;
        }
        
        return relation;
    }
    
    public void setRelation(String playerA, String playerB, PlayerRelation relation) {
        if (playerA.equals(playerB)) {
            return;
        }
        
        relations.computeIfAbsent(playerA, k -> new HashMap<>()).put(playerB, relation);
        relations.computeIfAbsent(playerB, k -> new HashMap<>()).put(playerA, relation);
    }
    
    public boolean isAtWar(String playerA, String playerB) {
        return getRelation(playerA, playerB) == PlayerRelation.WAR;
    }
    
    public boolean isAllied(String playerA, String playerB) {
        return getRelation(playerA, playerB) == PlayerRelation.ALLY;
    }
    
    public boolean canAttack(String playerA, String playerB) {
        return getRelation(playerA, playerB).canAttack();
    }
    
    public void addPlayer(String playerId) {
        relations.computeIfAbsent(playerId, k -> new HashMap<>());
    }
    
    public void removePlayer(String playerId) {
        relations.remove(playerId);
        for (Map<String, PlayerRelation> playerMap : relations.values()) {
            playerMap.remove(playerId);
        }
    }
}