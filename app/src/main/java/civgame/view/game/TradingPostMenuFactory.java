package civgame.view.game;

import civgame.model.game.GameSession;
import civgame.model.game.TradingPostTradeService;
import civgame.model.map.TradingPost;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TradingPostMenuFactory {

    private final TradingPostTradeService tradingPostTradeService;

    public TradingPostMenuFactory(TradingPostTradeService tradingPostTradeService) {
        this.tradingPostTradeService = tradingPostTradeService;
    }

    public JPopupMenu buildMenu(GameSession gameSession, TradingPost tradingPost, Runnable onActionSuccess) {
        JPopupMenu popupMenu = new JPopupMenu();
        PopupMenuStyler.style(popupMenu);

        if (!gameSession.isWithinBorder(tradingPost.getCoordinate())) {
            popupMenu.add(StyledMenuItemFactory.createItem(null, "Trading Post", "Claim this hex first", "Outside your border", false, null));
            return popupMenu;
        }
        if (gameSession.hasTradingPostTradedThisTurn()) {
            popupMenu.add(StyledMenuItemFactory.createItem(null, "Trading Post", "Rate: " + (int) (Constants.TRADING_POST_RATE * 100) + "%", "Already traded this turn", false, null));
            return popupMenu;
        }

        ResourceType[] resourceTypes = ResourceType.values();
        for (int i = 0; i < resourceTypes.length; i++) {
            popupMenu.add(createOfferedResourceMenu(gameSession, tradingPost, resourceTypes[i], onActionSuccess));
        }

        return popupMenu;
    }

    private JMenu createOfferedResourceMenu(GameSession gameSession, TradingPost tradingPost, ResourceType offeredType, Runnable onActionSuccess) {
        JMenu offeredMenu = new JMenu("Sell " + offeredType.name());

        ResourceType[] resourceTypes = ResourceType.values();
        for (int i = 0; i < resourceTypes.length; i++) {
            ResourceType requestedType = resourceTypes[i];
            if (requestedType == offeredType) {
                continue;
            }
            offeredMenu.add(createTradeItem(gameSession, tradingPost, offeredType, requestedType, onActionSuccess));
        }

        return offeredMenu;
    }

    private JMenuItem createTradeItem(GameSession gameSession, TradingPost tradingPost, ResourceType offeredType, ResourceType requestedType, Runnable onActionSuccess) {
        String title = "For " + requestedType.name() + " (" + (int) (Constants.TRADING_POST_RATE * 100) + "%)";

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                promptAmountAndTrade(gameSession, tradingPost, offeredType, requestedType, onActionSuccess);
            }
        };

        return StyledMenuItemFactory.createItem(null, title, "Choose an amount to sell", "", true, actionListener);
    }

    private void promptAmountAndTrade(GameSession gameSession, TradingPost tradingPost, ResourceType offeredType, ResourceType requestedType, Runnable onActionSuccess) {
        String input = JOptionPane.showInputDialog(null, "How much " + offeredType.name() + " do you want to sell?", "Trade at Trading Post", JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        int offeredAmount;
        try {
            offeredAmount = Integer.parseInt(input.trim());
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(null, "Please enter a whole number.");
            return;
        }

        boolean success = tradingPostTradeService.trade(gameSession, tradingPost, offeredType, requestedType, offeredAmount);
        if (success) {
            if (onActionSuccess != null) {
                onActionSuccess.run();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Trade failed. Check the amount and that you haven't traded here this turn.");
        }
    }
}