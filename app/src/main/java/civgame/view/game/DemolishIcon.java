package civgame.view.game;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class DemolishIcon implements Icon {

    private static final int SIZE = 22;

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.setColor(new Color(200, 70, 60));
        graphics2D.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int margin = 4;
        graphics2D.drawLine(x + margin, y + margin, x + SIZE - margin, y + SIZE - margin);
        graphics2D.drawLine(x + SIZE - margin, y + margin, x + margin, y + SIZE - margin);
    }

    @Override
    public int getIconWidth() {
        return SIZE;
    }

    @Override
    public int getIconHeight() {
        return SIZE;
    }
}