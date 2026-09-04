package civgame.network.message.item;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class ItemUseRequest extends Request {

    private String itemId;
    private String targetUnitId;
    private int targetQ;
    private int targetR;

    public ItemUseRequest() {
        super();
        setType(MessageType.ITEM_USE_REQUEST);
    }

    public ItemUseRequest(String senderId, String itemId, String targetUnitId, int targetQ, int targetR) {
        super(MessageType.ITEM_USE_REQUEST, senderId);
        this.itemId = itemId;
        this.targetUnitId = targetUnitId;
        this.targetQ = targetQ;
        this.targetR = targetR;
    }

    public String getItemId() {
        return itemId;
    }

    public String getTargetUnitId() {
        return targetUnitId;
    }

    public int getTargetQ() {
        return targetQ;
    }

    public int getTargetR() {
        return targetR;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setTargetUnitId(String targetUnitId) {
        this.targetUnitId = targetUnitId;
    }

    public void setTargetQ(int targetQ) {
        this.targetQ = targetQ;
    }

    public void setTargetR(int targetR) {
        this.targetR = targetR;
    }
}