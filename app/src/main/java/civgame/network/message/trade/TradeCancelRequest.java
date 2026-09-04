package civgame.network.message.trade;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class TradeCancelRequest extends Request {

    private String offerId;

    public TradeCancelRequest() {
        super();
        setType(MessageType.TRADE_CANCEL_REQUEST);
    }

    public TradeCancelRequest(String senderId, String offerId) {
        super(MessageType.TRADE_CANCEL_REQUEST, senderId);
        this.offerId = offerId;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }
}