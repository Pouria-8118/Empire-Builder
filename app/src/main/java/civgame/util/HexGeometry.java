package civgame.util;

import civgame.model.map.HexCoordinate;

import java.awt.geom.Point2D;

public class HexGeometry {

    private HexGeometry() {
    }

    public static Point2D axialToPixel(HexCoordinate coordinate, double hexSize) {
        double x = hexSize * (Math.sqrt(3) * coordinate.getQ() + Math.sqrt(3) / 2 * coordinate.getR());
        double y = hexSize * (1.5 * coordinate.getR());
        return new Point2D.Double(x, y);
    }

    public static double[] buildHexagonXPoints(double centerX, double centerY, double hexSize) {
        double[] xPoints = new double[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            xPoints[i] = centerX + hexSize * Math.cos(angle);
        }
        return xPoints;
    }

    public static double[] buildHexagonYPoints(double centerX, double centerY, double hexSize) {
        double[] yPoints = new double[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            yPoints[i] = centerY + hexSize * Math.sin(angle);
        }
        return yPoints;
    }

    public static HexCoordinate pixelToAxial(double pixelX, double pixelY, double hexSize) {
        double fractionalQ = (Math.sqrt(3) / 3 * pixelX - 1.0 / 3 * pixelY) / hexSize;
        double fractionalR = (2.0 / 3 * pixelY) / hexSize;
        return roundToNearestHex(fractionalQ, fractionalR);
    }

    private static HexCoordinate roundToNearestHex(double fractionalQ, double fractionalR) {
        double fractionalX = fractionalQ;
        double fractionalZ = fractionalR;
        double fractionalY = -fractionalX - fractionalZ;

        long roundedX = Math.round(fractionalX);
        long roundedY = Math.round(fractionalY);
        long roundedZ = Math.round(fractionalZ);

        double xDifference = Math.abs(roundedX - fractionalX);
        double yDifference = Math.abs(roundedY - fractionalY);
        double zDifference = Math.abs(roundedZ - fractionalZ);

        if (xDifference > yDifference && xDifference > zDifference) {
            roundedX = -roundedY - roundedZ;
        } else if (yDifference > zDifference) {
            roundedY = -roundedX - roundedZ;
        } else {
            roundedZ = -roundedX - roundedY;
        }

        return new HexCoordinate((int) roundedX, (int) roundedZ);
    }
}