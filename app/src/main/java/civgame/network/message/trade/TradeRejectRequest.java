package civgame.network.message.trade;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class TradeRejectRequest extends Request {

    private String offerId;

    public TradeRejectRequest() {
        super();
        setType(MessageType.TRADE_REJECT_REQUEST);
    }

    public TradeRejectRequest(String senderId, String offerId) {
        super(MessageType.TRADE_REJECT_REQUEST, senderId);
        this.offerId = offerId;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }
}