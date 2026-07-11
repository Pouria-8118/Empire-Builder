package civgame.view.hud;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class ControlsHintPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(20, 20, 30, 190);
    private static final Color BORDER_COLOR = new Color(200, 170, 90);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);

    public ControlsHintPanel() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));

        JLabel hintLabel = new JLabel(
                "Left-click: select / move   |   Right-click: build, station, upgrade   |   Drag: pan   |   Scroll: zoom   |   Esc: deselect"
        );
        hintLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hintLabel.setForeground(TEXT_COLOR);
        add(hintLabel);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.setColor(BACKGROUND_COLOR);
        graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

        graphics2D.setColor(BORDER_COLOR);
        graphics2D.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);

        graphics2D.dispose();
        super.paintComponent(graphics);
    }
}