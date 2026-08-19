package civgame.view.editor;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexEdge;
import civgame.model.map.HexEdgeFeatures;
import civgame.model.map.HexTile;
import civgame.model.map.MapBlueprint;
import civgame.model.map.TerrainType;
import civgame.model.map.TileResourceType;
import civgame.util.Constants;
import civgame.util.HexGeometry;
import civgame.util.IconProvider;
import civgame.util.ResourceIconProvider;
import civgame.util.TerrainColorProvider;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
                features.setWall(new civgame.model.map.Wall());
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

        drawEdgeFeatures(graphics2D, centerX, centerY);

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
        graphics2D.setColor(new Color(20, 20, 20));
        graphics2D.drawPolygon(hexagon);

        if (tile.getTerrainType() == TerrainType.MOUNTAIN_RANGE) {
            drawMountainRangePeaks(graphics2D, tileCenterX, tileCenterY);
        }

        if (tile.getResourceType() != TileResourceType.NONE) {
            drawResourceIcon(graphics2D, tile, tileCenterX, tileCenterY);
        }
    }

    private void drawResourceIcon(Graphics2D graphics2D, HexTile tile, double tileCenterX, double tileCenterY) {
        int iconSize = (int) (HEX_SIZE * 0.7);
        Image resourceIcon = IconProvider.loadImage(ResourceIconProvider.getIconPath(tile.getResourceType()));

        if (resourceIcon != null) {
            graphics2D.drawImage(resourceIcon, (int) (tileCenterX - iconSize / 2.0), (int) (tileCenterY - iconSize / 2.0), iconSize, iconSize, this);
            return;
        }

        graphics2D.setColor(Color.WHITE);
        graphics2D.fillOval((int) (tileCenterX - iconSize / 4.0), (int) (tileCenterY - iconSize / 4.0), iconSize / 2, iconSize / 2);
        graphics2D.setColor(Color.BLACK);
        graphics2D.setFont(new Font("SansSerif", Font.PLAIN, 8));
        String label = tile.getResourceType().name().substring(0, Math.min(2, tile.getResourceType().name().length()));
        graphics2D.drawString(label, (int) (tileCenterX - 6), (int) (tileCenterY + 3));
    }

    private void drawMountainRangePeaks(Graphics2D graphics2D, double centerX, double centerY) {
        graphics2D.setColor(new Color(30, 25, 25));

        int peakWidth = (int) (HEX_SIZE * 0.35);
        int peakHeight = (int) (HEX_SIZE * 0.5);
        int baseY = (int) (centerY + HEX_SIZE * 0.35);

        drawPeak(graphics2D, (int) (centerX - HEX_SIZE * 0.35), baseY, peakWidth, peakHeight);
        drawPeak(graphics2D, (int) centerX, baseY, peakWidth, peakHeight);
        drawPeak(graphics2D, (int) (centerX + HEX_SIZE * 0.35), baseY, peakWidth, peakHeight);
    }

    private void drawPeak(Graphics2D graphics2D, int baseCenterX, int baseY, int width, int height) {
        Polygon peakPolygon = new Polygon();
        peakPolygon.addPoint(baseCenterX - width / 2, baseY);
        peakPolygon.addPoint(baseCenterX, baseY - height);
        peakPolygon.addPoint(baseCenterX + width / 2, baseY);
        graphics2D.fillPolygon(peakPolygon);
    }

    private void drawTownHallMarker(Graphics2D graphics2D, HexCoordinate coordinate, double centerX, double centerY) {
        Point2D pixelPosition = HexGeometry.axialToPixel(coordinate, HEX_SIZE);
        double x = centerX + pixelPosition.getX();
        double y = centerY + pixelPosition.getY();
        int iconSize = (int) (HEX_SIZE * 1.1);

        Image townHallIcon = IconProvider.loadImage(Constants.TOWN_HALL_GAME_ICON_PATH);
        if (townHallIcon != null) {
            graphics2D.drawImage(townHallIcon, (int) (x - iconSize / 2.0), (int) (y - iconSize / 2.0), iconSize, iconSize, this);
            return;
        }

        graphics2D.setColor(new Color(230, 200, 60));
        graphics2D.fillOval((int) (x - iconSize / 2.0), (int) (y - iconSize / 2.0), iconSize, iconSize);
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawOval((int) (x - iconSize / 2.0), (int) (y - iconSize / 2.0), iconSize, iconSize);
    }

    private void drawEdgeFeatures(Graphics2D graphics2D, double centerX, double centerY) {
        graphics2D.setColor(new Color(50, 110, 200));
        graphics2D.setStroke(new BasicStroke((float) (HEX_SIZE * 0.14)));
        for (Map.Entry<HexEdge, HexEdgeFeatures> entry : blueprint.getEdgeFeatures().entrySet()) {
            if (entry.getValue().isRiverPresent()) {
                drawEdgeLine(graphics2D, entry.getKey(), centerX, centerY);
            }
        }

        graphics2D.setColor(new Color(90, 80, 70));
        graphics2D.setStroke(new BasicStroke((float) (HEX_SIZE * 0.22)));
        for (Map.Entry<HexEdge, HexEdgeFeatures> entry : blueprint.getEdgeFeatures().entrySet()) {
            if (entry.getValue().hasActiveWall()) {
                drawEdgeLine(graphics2D, entry.getKey(), centerX, centerY);
            }
        }

        graphics2D.setStroke(new BasicStroke(1f));
    }

    private void drawEdgeLine(Graphics2D graphics2D, HexEdge edge, double centerX, double centerY) {
        Point2D firstPixel = HexGeometry.axialToPixel(edge.getFirst(), HEX_SIZE);
        Point2D secondPixel = HexGeometry.axialToPixel(edge.getSecond(), HEX_SIZE);

        double firstCenterX = centerX + firstPixel.getX();
        double firstCenterY = centerY + firstPixel.getY();
        double secondCenterX = centerX + secondPixel.getX();
        double secondCenterY = centerY + secondPixel.getY();

        double midX = (firstCenterX + secondCenterX) / 2.0;
        double midY = (firstCenterY + secondCenterY) / 2.0;
        double angle = Math.atan2(secondCenterY - firstCenterY, secondCenterX - firstCenterX);
        double perpendicular = angle + Math.PI / 2.0;
        double halfEdgeLength = HEX_SIZE / 2.0;
        double offsetX = halfEdgeLength * Math.cos(perpendicular);
        double offsetY = halfEdgeLength * Math.sin(perpendicular);

        graphics2D.drawLine((int) (midX + offsetX), (int) (midY + offsetY), (int) (midX - offsetX), (int) (midY - offsetY));
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