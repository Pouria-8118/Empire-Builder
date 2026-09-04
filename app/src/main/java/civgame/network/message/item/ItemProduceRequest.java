package civgame.network.message.item;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class ItemProduceRequest extends Request {

    private String itemType;

    public ItemProduceRequest() {
        super();
        setType(MessageType.ITEM_PRODUCE_REQUEST);
    }

    public ItemProduceRequest(String senderId, String itemType) {
        super(MessageType.ITEM_PRODUCE_REQUEST, senderId);
        this.itemType = itemType;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}