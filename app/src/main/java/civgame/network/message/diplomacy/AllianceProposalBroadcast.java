package civgame.network.message.diplomacy;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;

public class AllianceProposalBroadcast extends Broadcast {
    
    private String proposerClientId;
    private String proposerUsername;
    private String targetClientId;
    private String targetUsername;
    
    public AllianceProposalBroadcast() {
        super();
        setType(MessageType.ALLIANCE_PROPOSAL_BROADCAST);
    }
    
    public AllianceProposalBroadcast(String broadcasterId,
                                      String proposerClientId,
                                      String proposerUsername,
                                      String targetClientId,
                                      String targetUsername) {
        super(MessageType.ALLIANCE_PROPOSAL_BROADCAST, broadcasterId);
        this.proposerClientId = proposerClientId;
        this.proposerUsername = proposerUsername;
        this.targetClientId = targetClientId;
        this.targetUsername = targetUsername;
    }
    
    public String getProposerClientId() {
        return proposerClientId;
    }
    
    public void setProposerClientId(String proposerClientId) {
        this.proposerClientId = proposerClientId;
    }
    
    public String getProposerUsername() {
        return proposerUsername;
    }
    
    public void setProposerUsername(String proposerUsername) {
        this.proposerUsername = proposerUsername;
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