package civgame.client.diplomacy;

import civgame.model.diplomacy.PlayerRelation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiplomacyClientState {
    
    private final String myClientId;
    private final Map<String, OpponentInfo> opponents;
    private final Map<String, Map<String, PlayerRelation>> relations;
    
    public DiplomacyClientState(String myClientId) {
        this.myClientId = myClientId;
        this.opponents = new HashMap<>();
        this.relations = new HashMap<>();
    }
    
    public void addOpponent(String clientId, String username, int playerIndex) {
        opponents.put(clientId, new OpponentInfo(clientId, username, playerIndex));
    }
    
    public void setRelation(String playerA, String playerB, PlayerRelation relation) {
        relations.computeIfAbsent(playerA, k -> new HashMap<>()).put(playerB, relation);
        relations.computeIfAbsent(playerB, k -> new HashMap<>()).put(playerA, relation);
    }
    
    public PlayerRelation getRelation(String playerA, String playerB) {
        if (playerA.equals(playerB)) {
            return PlayerRelation.ALLY;
        }
        Map<String, PlayerRelation> map = relations.get(playerA);
        if (map == null) {
            return PlayerRelation.NEUTRAL;
        }
        PlayerRelation relation = map.get(playerB);
        return relation != null ? relation : PlayerRelation.NEUTRAL;
    }
    
    public PlayerRelation getRelationWithMe(String otherClientId) {
        return getRelation(myClientId, otherClientId);
    }
    
    public List<OpponentInfo> getAllOpponents() {
        return new ArrayList<>(opponents.values());
    }
    
    public OpponentInfo getOpponent(String clientId) {
        return opponents.get(clientId);
    }
    
    public String getMyClientId() {
        return myClientId;
    }
    
    public static class OpponentInfo {
        private final String clientId;
        private final String username;
        private final int playerIndex;
        
        public OpponentInfo(String clientId, String username, int playerIndex) {
            this.clientId = clientId;
            this.username = username;
            this.playerIndex = playerIndex;
        }
        
        public String getClientId() {
            return clientId;
        }
        
        public String getUsername() {
            return username;
        }
        
        public int getPlayerIndex() {
            return playerIndex;
        }
    }
}