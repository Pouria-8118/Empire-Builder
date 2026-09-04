package civgame.network.message.item;

import civgame.network.message.MessageType;
import civgame.network.message.Response;

public class ItemActionResponse extends Response {

    private String actionType;

    public ItemActionResponse() {
        super();
        setType(MessageType.ITEM_ACTION_RESPONSE);
    }

    public ItemActionResponse(String requestId, boolean success, String actionType) {
        super(MessageType.ITEM_ACTION_RESPONSE, requestId, success);
        this.actionType = actionType;
    }

    public ItemActionResponse(String requestId, String errorMessage, String actionType) {
        super(MessageType.ITEM_ACTION_RESPONSE, requestId, errorMessage);
        this.actionType = actionType;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}