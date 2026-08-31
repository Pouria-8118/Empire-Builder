package civgame.server.lobby;

public class PlayerSlot {
    
    private final String clientId;
    private final String username;
    private final long joinedAt;
    private boolean ready;
    
    public PlayerSlot(String clientId, String username) {
        this.clientId = clientId;
        this.username = username;
        this.joinedAt = System.currentTimeMillis();
        this.ready = false;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public long getJoinedAt() {
        return joinedAt;
    }
    
    public boolean isReady() {
        return ready;
    }
    
    public void setReady(boolean ready) {
        this.ready = ready;
    }
    
    public void toggleReady() {
        this.ready = !this.ready;
    }
    
    public PlayerSlotInfo toInfo() {
        return new PlayerSlotInfo(clientId, username, ready, joinedAt);
    }
    
    public static class PlayerSlotInfo {
        
        private final String clientId;
        private final String username;
        private final boolean ready;
        private final long joinedAt;
        
        public PlayerSlotInfo(String clientId, String username, boolean ready, long joinedAt) {
            this.clientId = clientId;
            this.username = username;
            this.ready = ready;
            this.joinedAt = joinedAt;
        }
        
        public String getClientId() {
            return clientId;
        }
        
        public String getUsername() {
            return username;
        }
        
        public boolean isReady() {
            return ready;
        }
        
        public long getJoinedAt() {
            return joinedAt;
        }
    }
}