package civgame.view.game;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

public class HexEdgeDirectionIcon implements Icon {

    private static final int SIZE = 30;
    private final int neighborDirectionIndex;

    public HexEdgeDirectionIcon(int neighborDirectionIndex) {
        this.neighborDirectionIndex = neighborDirectionIndex;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double hexRadius = SIZE * 0.28;
        double centerX = x + SIZE / 2.0;
        double centerY = y + SIZE / 2.0;

        double angle = Math.toRadians(60 * neighborDirectionIndex);
        double neighborCenterX = centerX + hexRadius * 1.7 * Math.cos(angle);
        double neighborCenterY = centerY + hexRadius * 1.7 * Math.sin(angle);

        Polygon centerHex = buildHexagon(centerX, centerY, hexRadius);
        Polygon neighborHex = buildHexagon(neighborCenterX, neighborCenterY, hexRadius);

        graphics2D.setColor(new Color(90, 90, 100));
        graphics2D.fillPolygon(centerHex);
        graphics2D.fillPolygon(neighborHex);
        graphics2D.setColor(Color.LIGHT_GRAY);
        graphics2D.drawPolygon(centerHex);
        graphics2D.drawPolygon(neighborHex);

        graphics2D.setColor(new Color(150, 110, 60));
        graphics2D.setStroke(new BasicStroke(4f));
        graphics2D.drawLine((int) centerX, (int) centerY, (int) neighborCenterX, (int) neighborCenterY);
    }

    private Polygon buildHexagon(double centerX, double centerY, double radius) {
        Polygon polygon = new Polygon();
        for (int i = 0; i < 6; i++) {
            double pointAngle = Math.toRadians(60 * i - 30);
            polygon.addPoint((int) (centerX + radius * Math.cos(pointAngle)), (int) (centerY + radius * Math.sin(pointAngle)));
        }
        return polygon;
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