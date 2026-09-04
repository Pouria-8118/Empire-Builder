package civgame.network.message.trade;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class TradeAcceptRequest extends Request {

    private String offerId;

    public TradeAcceptRequest() {
        super();
        setType(MessageType.TRADE_ACCEPT_REQUEST);
    }

    public TradeAcceptRequest(String senderId, String offerId) {
        super(MessageType.TRADE_ACCEPT_REQUEST, senderId);
        this.offerId = offerId;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }
}