package civgame.model.diplomacy;

public enum PlayerRelation {
    
    ALLY,
    FRIENDLY,
    NEUTRAL,
    HOSTILE,
    WAR;
    
    public boolean isAtWar() {
        return this == WAR;
    }
    
    public boolean isAllied() {
        return this == ALLY;
    }
    
    public boolean isFriendly() {
        return this == FRIENDLY;
    }
    
    public boolean isHostile() {
        return this == HOSTILE;
    }
    
    public boolean canAttack() {
        return this == WAR || this == HOSTILE;
    }
    
    public boolean canTrade() {
        return this == ALLY || this == FRIENDLY || this == NEUTRAL;
    }
    
    public boolean canFormAlliance() {
        return this == FRIENDLY;
    }
    
    public String getDisplayName() {
        switch (this) {
            case ALLY:
                return "Allied";
            case FRIENDLY:
                return "Friendly";
            case NEUTRAL:
                return "Neutral";
            case HOSTILE:
                return "Hostile";
            case WAR:
                return "At War";
            default:
                return "Unknown";
        }
    }
}