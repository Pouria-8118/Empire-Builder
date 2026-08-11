package civgame.model.trade;

public interface TradeStrategy {

    int getRequiredOfferedAmount();

    int calculateReceivedAmount(int offeredAmount);
}