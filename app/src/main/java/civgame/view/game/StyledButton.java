package civgame.view.game;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StyledButton extends JButton {

    private static final Color NORMAL_BACKGROUND = new Color(60, 48, 30);
    private static final Color HOVER_BACKGROUND = new Color(95, 76, 42);
    private static final Color BORDER_COLOR = new Color(200, 170, 90);
    private static final Color TEXT_COLOR = new Color(240, 230, 200);

    private boolean hovering;

    public StyledButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setForeground(TEXT_COLOR);
        setBorderPainted(false);
        setFont(new Font("SansSerif", Font.BOLD, 14));
        addMouseListener(new HoverListener());
    }

    public StyledButton(String text, Dimension preferredSize) {
        this(text);
        setPreferredSize(preferredSize);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (hovering) {
            graphics2D.setColor(HOVER_BACKGROUND);
        } else {
            graphics2D.setColor(NORMAL_BACKGROUND);
        }
        graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

        graphics2D.setColor(BORDER_COLOR);
        graphics2D.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);

        graphics2D.dispose();
        super.paintComponent(graphics);
    }

    private class HoverListener extends MouseAdapter {

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            hovering = true;
            repaint();
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            hovering = false;
            repaint();
        }
    }
}