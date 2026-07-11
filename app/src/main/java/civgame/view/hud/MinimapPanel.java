package civgame.view.hud;

import civgame.model.building.TownHall;
import civgame.model.game.GameSession;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TileVisibility;
import civgame.model.unit.Unit;
import civgame.util.Constants;
import civgame.util.HexGeometry;
import civgame.util.TerrainColorProvider;
import civgame.util.UnitColorProvider;
import civgame.view.game.CameraState;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinimapPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(20, 20, 30, 220);
    private static final Color BORDER_COLOR = new Color(200, 170, 90);
    private static final Color TOWN_HALL_COLOR = new Color(200, 40, 40);
    private static final Color VIEWPORT_COLOR = new Color(255, 255, 255, 170);
    private static final double NOMINAL_HEX_SIZE = 1.0;
    private static final int PADDING = 10;

    private final GameSession gameSession;
    private final CameraState cameraState;
    private final Map<HexCoordinate, Point2D> nominalPositions;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private Runnable mapRepaintCallback;

    public MinimapPanel(GameSession gameSession, CameraState cameraState) {
        this.gameSession = gameSession;
        this.cameraState = cameraState;
        this.nominalPositions = new HashMap<HexCoordinate, Point2D>();
        setOpaque(false);
        computeNominalPositions();
        addMouseListener(new MinimapClickListener());
    }

    public void setMapRepaintCallback(Runnable mapRepaintCallback) {
        this.mapRepaintCallback = mapRepaintCallback;
    }

    private void computeNominalPositions() {
        HexMap hexMap = gameSession.getHexMap();
        minX = Double.MAX_VALUE;
        maxX = -Double.MAX_VALUE;
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;

        for (HexCoordinate coordinate : hexMap.getAllTiles().keySet()) {
            Point2D position = HexGeometry.axialToPixel(coordinate, NOMINAL_HEX_SIZE);
            nominalPositions.put(coordinate, position);

            minX = Math.min(minX, position.getX());
            maxX = Math.max(maxX, position.getX());
            minY = Math.min(minY, position.getY());
            maxY = Math.max(maxY, position.getY());
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.setColor(BACKGROUND_COLOR);
        graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        graphics2D.setColor(BORDER_COLOR);
        graphics2D.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);

        double scale = computeScale();
        drawTiles(graphics2D, scale);
        drawTownHall(graphics2D, scale);
        drawUnits(graphics2D, scale);
        drawViewportIndicator(graphics2D, scale);

        graphics2D.dispose();
    }

    private double computeScale() {
        double spanX = Math.max(1.0, maxX - minX);
        double spanY = Math.max(1.0, maxY - minY);
        double availableWidth = getWidth() - PADDING * 2;
        double availableHeight = getHeight() - PADDING * 2;
        return Math.min(availableWidth / spanX, availableHeight / spanY);
    }

    private Point2D toPanelPoint(HexCoordinate coordinate, double scale) {
        Point2D nominal = nominalPositions.get(coordinate);
        double panelX = PADDING + (nominal.getX() - minX) * scale;
        double panelY = PADDING + (nominal.getY() - minY) * scale;
        return new Point2D.Double(panelX, panelY);
    }

    private void drawTiles(Graphics2D graphics2D, double scale) {
        HexMap hexMap = gameSession.getHexMap();
        int dotSize = Math.max(2, (int) (scale * 1.6));

        for (HexTile tile : hexMap.getAllTiles().values()) {
            Point2D panelPoint = toPanelPoint(tile.getCoordinate(), scale);
            graphics2D.setColor(resolveMinimapColor(tile));
            graphics2D.fillRect((int) panelPoint.getX(), (int) panelPoint.getY(), dotSize, dotSize);
        }
    }

    private Color resolveMinimapColor(HexTile tile) {
        if (tile.getVisibility() == TileVisibility.UNSEEN) {
            return TerrainColorProvider.getUnseenColor();
        }
        if (tile.getVisibility() == TileVisibility.EXPLORED) {
            return TerrainColorProvider.getExploredColor(tile.getTerrainType());
        }
        return TerrainColorProvider.getColorForTerrain(tile.getTerrainType());
    }

    private void drawTownHall(Graphics2D graphics2D, double scale) {
        TownHall townHall = gameSession.getTownHall();
        Point2D panelPoint = toPanelPoint(townHall.getCoordinate(), scale);
        int markerSize = Math.max(5, (int) (scale * 2.6));

        graphics2D.setColor(TOWN_HALL_COLOR);
        graphics2D.fillRect((int) (panelPoint.getX() - markerSize / 2.0), (int) (panelPoint.getY() - markerSize / 2.0), markerSize, markerSize);
    }

    private void drawUnits(Graphics2D graphics2D, double scale) {
        int dotSize = Math.max(3, (int) (scale * 1.9));
        List<Unit> units = gameSession.getUnits();

        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (!gameSession.getHexMap().containsCoordinate(unit.getCoordinate())) {
                continue;
            }

            Point2D panelPoint = toPanelPoint(unit.getCoordinate(), scale);
            graphics2D.setColor(UnitColorProvider.getColorForUnitType(unit.getUnitType()));
            graphics2D.fillOval((int) (panelPoint.getX() - dotSize / 2.0), (int) (panelPoint.getY() - dotSize / 2.0), dotSize, dotSize);
        }
    }

    private void drawViewportIndicator(Graphics2D graphics2D, double scale) {
        HexCoordinate focusCoordinate = estimateFocusCoordinate();
        if (focusCoordinate == null || !nominalPositions.containsKey(focusCoordinate)) {
            return;
        }

        Point2D panelPoint = toPanelPoint(focusCoordinate, scale);
        int indicatorSize = 10;

        graphics2D.setColor(VIEWPORT_COLOR);
        graphics2D.drawOval((int) (panelPoint.getX() - indicatorSize / 2.0), (int) (panelPoint.getY() - indicatorSize / 2.0), indicatorSize, indicatorSize);
    }

    private HexCoordinate estimateFocusCoordinate() {
        double hexSize = Constants.HEX_SIZE * cameraState.getCurrentZoom();
        double worldX = -cameraState.getOffsetX();
        double worldY = -cameraState.getOffsetY();
        return HexGeometry.pixelToAxial(worldX, worldY, hexSize);
    }

    private class MinimapClickListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            HexCoordinate nearestCoordinate = findNearestCoordinate(mouseEvent.getX(), mouseEvent.getY());
            if (nearestCoordinate == null) {
                return;
            }

            double hexSize = Constants.HEX_SIZE * cameraState.getCurrentZoom();
            cameraState.centerOnTile(nearestCoordinate, hexSize);

            repaint();
            if (mapRepaintCallback != null) {
                mapRepaintCallback.run();
            }
        }

        private HexCoordinate findNearestCoordinate(int clickX, int clickY) {
            double scale = computeScale();
            HexCoordinate closest = null;
            double closestDistanceSquared = Double.MAX_VALUE;

            for (Map.Entry<HexCoordinate, Point2D> entry : nominalPositions.entrySet()) {
                Point2D panelPoint = toPanelPoint(entry.getKey(), scale);
                double deltaX = panelPoint.getX() - clickX;
                double deltaY = panelPoint.getY() - clickY;
                double distanceSquared = deltaX * deltaX + deltaY * deltaY;

                if (distanceSquared < closestDistanceSquared) {
                    closestDistanceSquared = distanceSquared;
                    closest = entry.getKey();
                }
            }

            return closest;
        }
    }
}