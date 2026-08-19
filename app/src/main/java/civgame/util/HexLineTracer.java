package civgame.util;

import civgame.model.map.HexCoordinate;

import java.util.ArrayList;
import java.util.List;

public class HexLineTracer {

    private HexLineTracer() {
    }

    public static List<HexCoordinate> traceLine(HexCoordinate start, HexCoordinate end) {
        int distance = start.distanceTo(end);
        List<HexCoordinate> line = new ArrayList<HexCoordinate>();

        if (distance == 0) {
            line.add(start);
            return line;
        }

        double startX = start.getQ();
        double startZ = start.getR();
        double startY = -startX - startZ;

        double endX = end.getQ();
        double endZ = end.getR();
        double endY = -endX - endZ;

        for (int i = 0; i <= distance; i++) {
            double t = (double) i / distance;
            double x = lerp(startX, endX, t);
            double y = lerp(startY, endY, t);
            double z = lerp(startZ, endZ, t);
            line.add(roundToHex(x, y, z));
        }

        return line;
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static HexCoordinate roundToHex(double x, double y, double z) {
        long roundedX = Math.round(x);
        long roundedY = Math.round(y);
        long roundedZ = Math.round(z);

        double xDiff = Math.abs(roundedX - x);
        double yDiff = Math.abs(roundedY - y);
        double zDiff = Math.abs(roundedZ - z);

        if (xDiff > yDiff && xDiff > zDiff) {
            roundedX = -roundedY - roundedZ;
        } else if (yDiff > zDiff) {
            roundedY = -roundedX - roundedZ;
        } else {
            roundedZ = -roundedX - roundedY;
        }

        return new HexCoordinate((int) roundedX, (int) roundedZ);
    }
}