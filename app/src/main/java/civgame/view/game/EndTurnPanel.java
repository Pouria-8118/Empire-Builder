package civgame.view.game;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;

public class EndTurnPanel extends JPanel {

    private final JLabel turnLabel;
    private final JLabel starvationLabel;
    private final JButton endTurnButton;

    public EndTurnPanel() {
        setLayout(new FlowLayout(FlowLayout.RIGHT));

        turnLabel = new JLabel("Turn: 1");
        starvationLabel = new JLabel("");
        endTurnButton = new JButton("End Turn");

        add(turnLabel);
        add(starvationLabel);
        add(endTurnButton);
    }

    public JButton getEndTurnButton() {
        return endTurnButton;
    }

    public void updateTurnNumber(int turnNumber) {
        turnLabel.setText("Turn: " + turnNumber);
    }

    public void setStarvationWarningVisible(boolean visible) {
        if (visible) {
            starvationLabel.setText("STARVATION!");
        } else {
            starvationLabel.setText("");
        }
    }

    // TODO : this temporary panel gets replaced by the full HUD
    // (resources, unit counts, production queue, invalid-action prevention)
}