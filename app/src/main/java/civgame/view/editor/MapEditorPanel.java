package civgame.view.editor;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexEdge;
import civgame.model.map.HexEdgeFeatures;
import civgame.model.map.HexTile;
import civgame.model.map.MapBlueprint;
import civgame.model.map.TerrainType;
import civgame.model.map.TileResourceType;
import civgame.model.map.Wall;
import civgame.util.HexGeometry;
import civgame.util.TerrainColorProvider;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Map;

public class MapEditorPanel extends JPanel {

    private static final double HEX_SIZE = 34.0;

    private final MapBlueprint blueprint;
    private HexCoordinate pendingEdgeSource;
    private MapEditorToolMode toolMode;
    private TerrainType selectedTerrainType;
    private TileResourceType selectedResourceType;

    public MapEditorPanel(MapBlueprint blueprint) {
        this.blueprint = blueprint;
        this.toolMode = MapEditorToolMode.PAINT_TERRAIN;
        this.selectedTerrainType = TerrainType.PLAIN;
        this.selectedResourceType = TileResourceType.NONE;
        this.pendingEdgeSource = null;

        setBackground(Color.BLACK);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                handleClick(mouseEvent);
            }
        });
    }

    public void setToolMode(MapEditorToolMode toolMode) {
        this.toolMode = toolMode;
        this.pendingEdgeSource = null;
    }

    public void setSelectedTerrainType(TerrainType terrainType) {
        this.selectedTerrainType = terrainType;
    }

    public void setSelectedResourceType(TileResourceType resourceType) {
        this.selectedResourceType = resourceType;
    }

    private void handleClick(MouseEvent mouseEvent) {
        double centerX = getWidth() / 2.0;
        double centerY = getHeight() / 2.0;
        double relativeX = mouseEvent.getX() - centerX;
        double relativeY = mouseEvent.getY() - centerY;
        HexCoordinate clickedCoordinate = HexGeometry.pixelToAxial(relativeX, relativeY, HEX_SIZE);

        if (blueprint.getTile(clickedCoordinate) == null) {
            return;
        }

        if (toolMode == MapEditorToolMode.PAINT_TERRAIN) {
            blueprint.setTile(clickedCoordinate, new HexTile(clickedCoordinate, selectedTerrainType, selectedResourceType));
        } else if (toolMode == MapEditorToolMode.PLACE_TOWN_HALL) {
            blueprint.setTownHallCoordinate(clickedCoordinate);
        } else if (toolMode == MapEditorToolMode.TOGGLE_RIVER) {
            handleRiverEdgeClick(clickedCoordinate);
        } else if (toolMode == MapEditorToolMode.TOGGLE_WALL) {
            handleWallEdgeClick(clickedCoordinate);
        }

        repaint();
    }

    private void handleRiverEdgeClick(HexCoordinate clickedCoordinate) {
        if (pendingEdgeSource == null) {
            pendingEdgeSource = clickedCoordinate;
            return;
        }

        if (pendingEdgeSource.distanceTo(clickedCoordinate) == 1) {
            HexEdgeFeatures features = blueprint.getOrCreateEdgeFeatures(pendingEdgeSource, clickedCoordinate);
            features.setRiverPresent(!features.isRiverPresent());
        }

        pendingEdgeSource = null;
    }

    private void handleWallEdgeClick(HexCoordinate clickedCoordinate) {
        if (pendingEdgeSource == null) {
            pendingEdgeSource = clickedCoordinate;
            return;
        }

        if (pendingEdgeSource.distanceTo(clickedCoordinate) == 1) {
            HexEdgeFeatures features = blueprint.getOrCreateEdgeFeatures(pendingEdgeSource, clickedCoordinate);
            if (features.hasActiveWall()) {
                features.setWall(null);
            } else {
                features.setWall(new Wall());
            }
        }

        pendingEdgeSource = null;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;

        double centerX = getWidth() / 2.0;
        double centerY = getHeight() / 2.0;

        for (Map.Entry<HexCoordinate, HexTile> entry : blueprint.getTiles().entrySet()) {
            drawTile(graphics2D, entry.getValue(), centerX, centerY);
        }

        drawRivers(graphics2D, centerX, centerY);
        drawWalls(graphics2D, centerX, centerY);

        if (blueprint.getTownHallCoordinate() != null) {
            drawTownHallMarker(graphics2D, blueprint.getTownHallCoordinate(), centerX, centerY);
        }
    }

    private void drawTile(Graphics2D graphics2D, HexTile tile, double centerX, double centerY) {
        Point2D pixelPosition = HexGeometry.axialToPixel(tile.getCoordinate(), HEX_SIZE);
        double tileCenterX = centerX + pixelPosition.getX();
        double tileCenterY = centerY + pixelPosition.getY();

        Polygon hexagon = buildHexagonPolygon(tileCenterX, tileCenterY);
        graphics2D.setColor(TerrainColorProvider.getColorForTerrain(tile.getTerrainType()));
        graphics2D.fillPolygon(hexagon);
        graphics2D.setColor(Color.DARK_GRAY);
        graphics2D.drawPolygon(hexagon);

        if (tile.getResourceType() != TileResourceType.NONE) {
            graphics2D.setColor(Color.WHITE);
            graphics2D.setFont(new Font("SansSerif", Font.PLAIN, 9));
            String label = tile.getResourceType().name().substring(0, Math.min(3, tile.getResourceType().name().length()));
            graphics2D.drawString(label, (int) (tileCenterX - 10), (int) (tileCenterY + 4));
        }
    }

    private void drawTownHallMarker(Graphics2D graphics2D, HexCoordinate coordinate, double centerX, double centerY) {
        Point2D pixelPosition = HexGeometry.axialToPixel(coordinate, HEX_SIZE);
        double x = centerX + pixelPosition.getX();
        double y = centerY + pixelPosition.getY();

        graphics2D.setColor(Color.YELLOW);
        graphics2D.fillRect((int) (x - 8), (int) (y - 8), 16, 16);
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawRect((int) (x - 8), (int) (y - 8), 16, 16);
    }

    private void drawWalls(Graphics2D graphics2D, double centerX, double centerY) {
        graphics2D.setColor(new Color(90, 80, 70));
        graphics2D.setStroke(new BasicStroke(6f));

        for (Map.Entry<HexEdge, HexEdgeFeatures> entry : blueprint.getEdgeFeatures().entrySet()) {
            if (!entry.getValue().hasActiveWall()) {
                continue;
            }

            Point2D firstPixel = HexGeometry.axialToPixel(entry.getKey().getFirst(), HEX_SIZE);
            Point2D secondPixel = HexGeometry.axialToPixel(entry.getKey().getSecond(), HEX_SIZE);

            int x1 = (int) (centerX + firstPixel.getX());
            int y1 = (int) (centerY + firstPixel.getY());
            int x2 = (int) (centerX + secondPixel.getX());
            int y2 = (int) (centerY + secondPixel.getY());

            graphics2D.drawLine(x1, y1, x2, y2);
        }

        graphics2D.setStroke(new BasicStroke(1f));
    }

    private void drawRivers(Graphics2D graphics2D, double centerX, double centerY) {
        graphics2D.setColor(new Color(50, 110, 200));
        graphics2D.setStroke(new BasicStroke(4f));

        for (Map.Entry<HexEdge, HexEdgeFeatures> entry : blueprint.getEdgeFeatures().entrySet()) {
            if (!entry.getValue().isRiverPresent()) {
                continue;
            }

            Point2D firstPixel = HexGeometry.axialToPixel(entry.getKey().getFirst(), HEX_SIZE);
            Point2D secondPixel = HexGeometry.axialToPixel(entry.getKey().getSecond(), HEX_SIZE);

            int x1 = (int) (centerX + firstPixel.getX());
            int y1 = (int) (centerY + firstPixel.getY());
            int x2 = (int) (centerX + secondPixel.getX());
            int y2 = (int) (centerY + secondPixel.getY());

            graphics2D.drawLine(x1, y1, x2, y2);
        }

        graphics2D.setStroke(new BasicStroke(1f));
    }

    private Polygon buildHexagonPolygon(double centerX, double centerY) {
        Polygon polygon = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            int x = (int) (centerX + HEX_SIZE * Math.cos(angle));
            int y = (int) (centerY + HEX_SIZE * Math.sin(angle));
            polygon.addPoint(x, y);
        }
        return polygon;
    }
}