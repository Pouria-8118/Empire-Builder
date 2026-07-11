package civgame.view.game;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class EndTurnPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(20, 20, 30, 210);
    private static final Color BORDER_COLOR = new Color(200, 170, 90);
    private static final Color WARNING_COLOR = new Color(230, 90, 90);

    private final JLabel starvationLabel;
    private final StyledButton endTurnButton;

    public EndTurnPanel() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 10));

        starvationLabel = new JLabel("");
        starvationLabel.setForeground(WARNING_COLOR);
        starvationLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        endTurnButton = new StyledButton("End Turn  \u25B6", new Dimension(140, 36));

        add(starvationLabel);
        add(endTurnButton);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.setColor(BACKGROUND_COLOR);
        graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        graphics2D.setColor(BORDER_COLOR);
        graphics2D.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

        graphics2D.dispose();
        super.paintComponent(graphics);
    }

    public StyledButton getEndTurnButton() {
        return endTurnButton;
    }

    public void setStarvationWarningVisible(boolean visible) {
        if (visible) {
            starvationLabel.setText("STARVATION!");
        } else {
            starvationLabel.setText("");
        }
    }
}