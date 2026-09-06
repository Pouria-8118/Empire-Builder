package civgame.view.hud;

import civgame.view.game.StyledButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ActionButtonPanel extends JPanel {

    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 40;
    private static final int BUTTON_GAP = 8;

    private final StyledButton inventoryButton;
    private final StyledButton tradeButton;
    private final StyledButton inboxButton;

    public ActionButtonPanel(boolean multiplayer) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Dimension buttonSize = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);

        inventoryButton = new StyledButton("Inventory", buttonSize);
        inventoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(inventoryButton);

        if (multiplayer) {
            add(Box.createVerticalStrut(BUTTON_GAP));

            tradeButton = new StyledButton("Trade", buttonSize);
            tradeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(tradeButton);

            add(Box.createVerticalStrut(BUTTON_GAP));

            inboxButton = new StyledButton("Inbox", buttonSize);
            inboxButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(inboxButton);
        } else {
            tradeButton = null;
            inboxButton = null;
        }
    }

    public void setInventoryButtonListener(ActionListener listener) {
        inventoryButton.addActionListener(listener);
    }

    public void setTradeButtonListener(ActionListener listener) {
        if (tradeButton != null) {
            tradeButton.addActionListener(listener);
        }
    }

    public void setInboxButtonListener(ActionListener listener) {
        if (inboxButton != null) {
            inboxButton.addActionListener(listener);
        }
    }
}