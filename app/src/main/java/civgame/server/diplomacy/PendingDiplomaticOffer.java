package civgame.server.diplomacy;

public class PendingDiplomaticOffer {
    
    public enum OfferType {
        PEACE,
        ALLIANCE
    }
    
    private final OfferType type;
    private final String proposerClientId;
    private final String targetClientId;
    private final long createdAt;
    private final long expiresAt;
    
    public PendingDiplomaticOffer(OfferType type, String proposerClientId, String targetClientId) {
        this.type = type;
        this.proposerClientId = proposerClientId;
        this.targetClientId = targetClientId;
        this.createdAt = System.currentTimeMillis();
        this.expiresAt = this.createdAt + 30_000L;
    }
    
    public OfferType getType() {
        return type;
    }
    
    public String getProposerClientId() {
        return proposerClientId;
    }
    
    public String getTargetClientId() {
        return targetClientId;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}