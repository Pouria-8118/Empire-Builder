package civgame.view.game;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.util.Constants;
import civgame.util.HexGeometry;
import civgame.util.TerrainColorProvider;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.Map;

public class MapPanel extends JPanel {

    private final HexMap hexMap;
    private final CameraState cameraState;

    public MapPanel(HexMap hexMap, CameraState cameraState) {
        this.hexMap = hexMap;
        this.cameraState = cameraState;
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;

        double zoom = cameraState.getCurrentZoom();
        double hexSize = Constants.HEX_SIZE * zoom;
        double centerX = getWidth() / 2.0 + cameraState.getOffsetX();
        double centerY = getHeight() / 2.0 + cameraState.getOffsetY();

        Map<HexCoordinate, HexTile> allTiles = hexMap.getAllTiles();

        for (Map.Entry<HexCoordinate, HexTile> entry : allTiles.entrySet()) {
            HexTile tile = entry.getValue();
            drawTile(graphics2D, tile, hexSize, centerX, centerY);
        }
    }

    private void drawTile(Graphics2D graphics2D, HexTile tile, double hexSize, double centerX, double centerY) {
        Point2D pixelPosition = HexGeometry.axialToPixel(tile.getCoordinate(), hexSize);
        double tileCenterX = centerX + pixelPosition.getX();
        double tileCenterY = centerY + pixelPosition.getY();

        double[] xPoints = HexGeometry.buildHexagonXPoints(tileCenterX, tileCenterY, hexSize);
        double[] yPoints = HexGeometry.buildHexagonYPoints(tileCenterX, tileCenterY, hexSize);

        Polygon hexagonPolygon = new Polygon();
        for (int i = 0; i < xPoints.length; i++) {
            hexagonPolygon.addPoint((int) xPoints[i], (int) yPoints[i]);
        }

        Color fillColor = TerrainColorProvider.getColorForTerrain(tile.getTerrainType());
        graphics2D.setColor(fillColor);
        graphics2D.fillPolygon(hexagonPolygon);

        graphics2D.setColor(Color.BLACK);
        graphics2D.drawPolygon(hexagonPolygon);

        if (tile.hasResource()) {
            drawResourceMarker(graphics2D, tileCenterX, tileCenterY, hexSize);
        }
    }

    private void drawResourceMarker(Graphics2D graphics2D, double centerX, double centerY, double hexSize) {
        double markerRadius = hexSize * 0.2;
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillOval(
                (int) (centerX - markerRadius),
                (int) (centerY - markerRadius),
                (int) (markerRadius * 2),
                (int) (markerRadius * 2)
        );

        // TODO (bonus - visual polish): replace this generic white dot with a
        // specific icon per resource type (wood, stone, iron, wheat, rice, cattle, sheep)
    }

    // TODO : skip/darken tiles based on tile.getVisibility()
    // TODO : draw units on top of tiles here
}