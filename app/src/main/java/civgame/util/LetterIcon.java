package civgame.util;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

// A small circular fallback icon used inside menus when a real image
// has not been placed in resources yet.
public class LetterIcon implements Icon {

    private final String letter;
    private final Color backgroundColor;
    private final int diameter;

    public LetterIcon(String letter, Color backgroundColor, int diameter) {
        this.letter = letter;
        this.backgroundColor = backgroundColor;
        this.diameter = diameter;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.setColor(backgroundColor);
        graphics2D.fillOval(x, y, diameter, diameter);

        graphics2D.setColor(Color.WHITE);
        graphics2D.setFont(new Font("SansSerif", Font.BOLD, (int) (diameter * 0.55)));
        int textWidth = graphics2D.getFontMetrics().stringWidth(letter);
        int textAscent = graphics2D.getFontMetrics().getAscent();
        graphics2D.drawString(letter, x + (diameter - textWidth) / 2, y + (diameter + textAscent) / 2 - 2);

        graphics2D.dispose();
    }

    @Override
    public int getIconWidth() {
        return diameter;
    }

    @Override
    public int getIconHeight() {
        return diameter;
    }
}