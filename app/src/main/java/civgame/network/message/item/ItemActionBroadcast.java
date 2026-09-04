package civgame.network.message.item;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;

public class ItemActionBroadcast extends Broadcast {

    private String actionType;
    private String actorClientId;
    private String actorUsername;
    private String targetUnitId;
    private String targetClientId;
    private int targetQ;
    private int targetR;

    public ItemActionBroadcast() {
        super();
        setType(MessageType.ITEM_ACTION_BROADCAST);
    }

    public ItemActionBroadcast(String broadcasterId) {
        super(MessageType.ITEM_ACTION_BROADCAST, broadcasterId);
    }

    public String getActionType() {
        return actionType;
    }

    public String getActorClientId() {
        return actorClientId;
    }

    public String getActorUsername() {
        return actorUsername;
    }

    public String getTargetUnitId() {
        return targetUnitId;
    }

    public String getTargetClientId() {
        return targetClientId;
    }

    public int getTargetQ() {
        return targetQ;
    }

    public int getTargetR() {
        return targetR;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public void setActorClientId(String actorClientId) {
        this.actorClientId = actorClientId;
    }

    public void setActorUsername(String actorUsername) {
        this.actorUsername = actorUsername;
    }

    public void setTargetUnitId(String targetUnitId) {
        this.targetUnitId = targetUnitId;
    }

    public void setTargetClientId(String targetClientId) {
        this.targetClientId = targetClientId;
    }

    public void setTargetQ(int targetQ) {
        this.targetQ = targetQ;
    }

    public void setTargetR(int targetR) {
        this.targetR = targetR;
    }
}