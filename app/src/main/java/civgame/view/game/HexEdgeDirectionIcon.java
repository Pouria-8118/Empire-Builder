package civgame.view.game;

import civgame.model.map.HexCoordinate;
import civgame.util.HexGeometry;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

public class HexEdgeDirectionIcon implements Icon {

    private static final int SIZE = 34;

    private final int deltaQ;
    private final int deltaR;

    public HexEdgeDirectionIcon(int deltaQ, int deltaR) {
        this.deltaQ = deltaQ;
        this.deltaR = deltaR;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double hexRadius = SIZE * 0.24;
        double centerX = x + SIZE / 2.0;
        double centerY = y + SIZE / 2.0;

        Point2D originPixel = HexGeometry.axialToPixel(new HexCoordinate(0, 0), hexRadius);
        Point2D neighborPixel = HexGeometry.axialToPixel(new HexCoordinate(deltaQ, deltaR), hexRadius);

        double offsetX = (neighborPixel.getX() - originPixel.getX()) * 0.95;
        double offsetY = (neighborPixel.getY() - originPixel.getY()) * 0.95;

        double neighborCenterX = centerX + offsetX;
        double neighborCenterY = centerY + offsetY;

        Polygon centerHex = buildHexagon(centerX, centerY, hexRadius);
        Polygon neighborHex = buildHexagon(neighborCenterX, neighborCenterY, hexRadius);

        graphics2D.setColor(new Color(150, 110, 60));
        graphics2D.setStroke(new BasicStroke(5f));
        graphics2D.drawLine((int) centerX, (int) centerY, (int) neighborCenterX, (int) neighborCenterY);
        graphics2D.setStroke(new BasicStroke(1f));

        graphics2D.setColor(new Color(100, 100, 110));
        graphics2D.fillPolygon(centerHex);
        graphics2D.setColor(new Color(160, 130, 90));
        graphics2D.fillPolygon(neighborHex);

        graphics2D.setColor(Color.LIGHT_GRAY);
        graphics2D.drawPolygon(centerHex);
        graphics2D.drawPolygon(neighborHex);
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