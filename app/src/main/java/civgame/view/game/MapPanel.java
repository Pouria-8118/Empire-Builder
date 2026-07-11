package civgame.view.game;

import civgame.model.building.Building;
import civgame.model.building.TownHall;
import civgame.model.game.GameSession;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TileVisibility;
import civgame.model.unit.Unit;
import civgame.util.BuildingColorProvider;
import civgame.util.BuildingIconProvider;
import civgame.util.Constants;
import civgame.util.HexGeometry;
import civgame.util.IconProvider;
import civgame.util.ResourceIconProvider;
import civgame.util.TerrainColorProvider;
import civgame.util.UnitColorProvider;
import civgame.util.UnitIconProvider;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

public class MapPanel extends JPanel {

    private static final double UNIT_SIZE_FACTOR = 0.75;
    private static final double BUILDING_SIZE_FACTOR = 1.2;
    private static final double TOWN_HALL_SIZE_FACTOR = 1.4;
    private static final double REACHABLE_TILE_BRIGHTEN_AMOUNT = 0.16;

    private static final Color OUTSIDE_BORDER_TINT = new Color(0, 0, 0, 110);
    private static final Color SETTLEMENT_COLOR = new Color(150, 90, 200);
    private static final Color REACHABLE_TILE_OUTLINE = new Color(255, 255, 255, 130);
    private static final Font FALLBACK_LABEL_FONT = new Font("SansSerif", Font.BOLD, 12);
    private static final Font AP_BADGE_FONT = new Font("SansSerif", Font.BOLD, 10);

    private final GameSession gameSession;
    private final CameraState cameraState;
    private final SelectionState selectionState;
    private final UnitMovementAnimator movementAnimator;

    public MapPanel(GameSession gameSession, CameraState cameraState, SelectionState selectionState, UnitMovementAnimator movementAnimator) {
        this.gameSession = gameSession;
        this.cameraState = cameraState;
        this.selectionState = selectionState;
        this.movementAnimator = movementAnimator;
        setBackground(Color.BLACK);
        setToolTipText("");
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;

        double hexSize = getCurrentHexSize();
        double centerX = getPanelCenterX();
        double centerY = getPanelCenterY();

        drawAllTiles(graphics2D, hexSize, centerX, centerY);
        drawTownHall(graphics2D, hexSize, centerX, centerY);
        drawAllBuildings(graphics2D, hexSize, centerX, centerY);
        drawAllSettlements(graphics2D, hexSize, centerX, centerY);
        drawAllUnits(graphics2D, hexSize, centerX, centerY);
    }

    private void drawAllTiles(Graphics2D graphics2D, double hexSize, double centerX, double centerY) {
        HexMap hexMap = gameSession.getHexMap();
        Map<HexCoordinate, HexTile> allTiles = hexMap.getAllTiles();

        for (HexTile tile : allTiles.values()) {
            drawTile(graphics2D, tile, hexSize, centerX, centerY);
        }
    }

    private void drawTile(Graphics2D graphics2D, HexTile tile, double hexSize, double centerX, double centerY) {
        Point2D pixelPosition = HexGeometry.axialToPixel(tile.getCoordinate(), hexSize);
        double tileCenterX = centerX + pixelPosition.getX();
        double tileCenterY = centerY + pixelPosition.getY();

        Polygon hexagonPolygon = buildHexagonPolygon(tileCenterX, tileCenterY, hexSize);
        boolean isReachable = selectionState.getReachableTiles().contains(tile.getCoordinate());

        Color fillColor = resolveTileColor(tile);
        if (isReachable) {
            fillColor = brighten(fillColor, REACHABLE_TILE_BRIGHTEN_AMOUNT);
        }

        graphics2D.setColor(fillColor);
        graphics2D.fillPolygon(hexagonPolygon);

        graphics2D.setColor(new Color(0, 0, 0, 80));
        graphics2D.drawPolygon(hexagonPolygon);

        if (isReachable) {
            graphics2D.setColor(REACHABLE_TILE_OUTLINE);
            graphics2D.drawPolygon(hexagonPolygon);
        }

        if (tile.hasResource() && tile.getVisibility() != TileVisibility.UNSEEN) {
            drawResourceMarker(graphics2D, tile, tileCenterX, tileCenterY, hexSize);
        }

        if (tile.getVisibility() != TileVisibility.UNSEEN && !gameSession.isWithinBorder(tile.getCoordinate())) {
            graphics2D.setColor(OUTSIDE_BORDER_TINT);
            graphics2D.fillPolygon(hexagonPolygon);
        }
    }

    private Color brighten(Color baseColor, double amount) {
        int red = (int) (baseColor.getRed() + (255 - baseColor.getRed()) * amount);
        int green = (int) (baseColor.getGreen() + (255 - baseColor.getGreen()) * amount);
        int blue = (int) (baseColor.getBlue() + (255 - baseColor.getBlue()) * amount);
        return new Color(red, green, blue);
    }

    private Color resolveTileColor(HexTile tile) {
        if (tile.getVisibility() == TileVisibility.UNSEEN) {
            return TerrainColorProvider.getUnseenColor();
        }
        if (tile.getVisibility() == TileVisibility.EXPLORED) {
            return TerrainColorProvider.getExploredColor(tile.getTerrainType());
        }
        return TerrainColorProvider.getColorForTerrain(tile.getTerrainType());
    }

    private Polygon buildHexagonPolygon(double centerX, double centerY, double hexSize) {
        double[] xPoints = HexGeometry.buildHexagonXPoints(centerX, centerY, hexSize);
        double[] yPoints = HexGeometry.buildHexagonYPoints(centerX, centerY, hexSize);

        Polygon hexagonPolygon = new Polygon();
        for (int i = 0; i < xPoints.length; i++) {
            hexagonPolygon.addPoint((int) xPoints[i], (int) yPoints[i]);
        }

        return hexagonPolygon;
    }

    private void drawResourceMarker(Graphics2D graphics2D, HexTile tile, double centerX, double centerY, double hexSize) {
        int markerSize = (int) (hexSize * 0.42);
        String iconPath = ResourceIconProvider.getIconPath(tile.getResourceType());
        Image resourceIcon = null;
        if (iconPath != null) {
            resourceIcon = IconProvider.loadImage(iconPath);
        }

        if (resourceIcon != null) {
            graphics2D.drawImage(resourceIcon, (int) (centerX - markerSize / 2.0), (int) (centerY - markerSize / 2.0), markerSize, markerSize, this);
        } else {
            double fallbackRadius = hexSize * 0.2;
            graphics2D.setColor(Color.WHITE);
            graphics2D.fillOval((int) (centerX - fallbackRadius), (int) (centerY - fallbackRadius), (int) (fallbackRadius * 2), (int) (fallbackRadius * 2));
        }

        if (tile.isIronDepositPresent()) {
            drawIronDepositOverlay(graphics2D, centerX, centerY, hexSize);
        }
    }

    private void drawIronDepositOverlay(Graphics2D graphics2D, double centerX, double centerY, double hexSize) {
        int overlaySize = (int) (hexSize * 0.3);
        double offsetX = centerX + hexSize * 0.3;
        double offsetY = centerY + hexSize * 0.3;

        Image ironIcon = IconProvider.loadImage(Constants.IRON_ICON_PATH);
        if (ironIcon != null) {
            graphics2D.drawImage(ironIcon, (int) (offsetX - overlaySize / 2.0), (int) (offsetY - overlaySize / 2.0), overlaySize, overlaySize, this);
        } else {
            graphics2D.setColor(new Color(160, 80, 30));
            graphics2D.fillOval((int) (offsetX - overlaySize / 2.0), (int) (offsetY - overlaySize / 2.0), overlaySize, overlaySize);
        }
    }

    private void drawTownHall(Graphics2D graphics2D, double hexSize, double centerX, double centerY) {
        TownHall townHall = gameSession.getTownHall();
        Point2D pixelPosition = HexGeometry.axialToPixel(townHall.getCoordinate(), hexSize);
        double drawX = centerX + pixelPosition.getX();
        double drawY = centerY + pixelPosition.getY();
        int markerSize = (int) (hexSize * TOWN_HALL_SIZE_FACTOR);

        Image townHallIcon = IconProvider.loadImage(Constants.TOWN_HALL_GAME_ICON_PATH);

        if (townHallIcon != null) {
            graphics2D.drawImage(townHallIcon, (int) (drawX - markerSize / 2.0), (int) (drawY - markerSize / 2.0), markerSize, markerSize, this);
        } else {
            graphics2D.setColor(new Color(200, 40, 40));
            graphics2D.fillRect((int) (drawX - markerSize / 2.0), (int) (drawY - markerSize / 2.0), markerSize, markerSize);
            graphics2D.setColor(Color.WHITE);
            graphics2D.setFont(FALLBACK_LABEL_FONT);
            int textWidth = graphics2D.getFontMetrics().stringWidth("TH");
            graphics2D.drawString("TH", (int) (drawX - textWidth / 2.0), (int) (drawY + 5));
        }
    }

    private void drawAllBuildings(Graphics2D graphics2D, double hexSize, double centerX, double centerY) {
        List<Building> allBuildings = gameSession.getAllBuildings();

        for (int i = 0; i < allBuildings.size(); i++) {
            drawBuilding(graphics2D, allBuildings.get(i), hexSize, centerX, centerY);
        }
    }

    private void drawBuilding(Graphics2D graphics2D, Building building, double hexSize, double centerX, double centerY) {
        Point2D pixelPosition = HexGeometry.axialToPixel(building.getCoordinate(), hexSize);
        double drawX = centerX + pixelPosition.getX();
        double drawY = centerY + pixelPosition.getY();

        int squareSize = (int) (hexSize * BUILDING_SIZE_FACTOR);
        Image buildingIcon = IconProvider.loadImage(BuildingIconProvider.getIconPath(building.getBuildingType()));

        if (buildingIcon != null) {
            graphics2D.drawImage(buildingIcon, (int) (drawX - squareSize / 2.0), (int) (drawY - squareSize / 2.0), squareSize, squareSize, this);
        } else {
            drawBuildingFallbackShape(graphics2D, building, drawX, drawY, squareSize);
        }

        drawWorkerCountBadge(graphics2D, building, drawX, drawY, squareSize);
    }

    private void drawBuildingFallbackShape(Graphics2D graphics2D, Building building, double drawX, double drawY, int squareSize) {
        graphics2D.setColor(BuildingColorProvider.getColorForBuildingType(building.getBuildingType()));
        graphics2D.fillRect((int) (drawX - squareSize / 2.0), (int) (drawY - squareSize / 2.0), squareSize, squareSize);

        graphics2D.setColor(Color.BLACK);
        graphics2D.drawRect((int) (drawX - squareSize / 2.0), (int) (drawY - squareSize / 2.0), squareSize, squareSize);

        String label = BuildingIconProvider.getFallbackLabel(building.getBuildingType());
        graphics2D.setFont(FALLBACK_LABEL_FONT);
        int textWidth = graphics2D.getFontMetrics().stringWidth(label);
        graphics2D.drawString(label, (int) (drawX - textWidth / 2.0), (int) (drawY + 5));
    }

    private void drawWorkerCountBadge(Graphics2D graphics2D, Building building, double drawX, double drawY, int squareSize) {
        String workerText = building.getStationedWorkers().size() + "/" + building.getWorkerCapacity();
        graphics2D.setFont(AP_BADGE_FONT);
        int textWidth = graphics2D.getFontMetrics().stringWidth(workerText);
        int badgeY = (int) (drawY - squareSize / 2.0 - 6);

        graphics2D.setColor(new Color(0, 0, 0, 160));
        graphics2D.fillRoundRect((int) (drawX - textWidth / 2.0) - 3, badgeY - 11, textWidth + 6, 14, 6, 6);

        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString(workerText, (int) (drawX - textWidth / 2.0), badgeY);
    }

    private void drawAllSettlements(Graphics2D graphics2D, double hexSize, double centerX, double centerY) {
        for (HexCoordinate settlementCoordinate : gameSession.getSettlementTiles()) {
            drawSettlement(graphics2D, settlementCoordinate, hexSize, centerX, centerY);
        }
    }

    private void drawSettlement(Graphics2D graphics2D, HexCoordinate coordinate, double hexSize, double centerX, double centerY) {
        Point2D pixelPosition = HexGeometry.axialToPixel(coordinate, hexSize);
        double drawX = centerX + pixelPosition.getX();
        double drawY = centerY + pixelPosition.getY();
        int markerRadius = (int) (hexSize * BUILDING_SIZE_FACTOR * 0.35);

        Image settlementIcon = IconProvider.loadImage(Constants.SETTLEMENT_ICON_PATH);
        if (settlementIcon != null) {
            int iconSize = markerRadius * 2;
            graphics2D.drawImage(settlementIcon, (int) (drawX - iconSize / 2.0), (int) (drawY - iconSize / 2.0), iconSize, iconSize, this);
            return;
        }

        graphics2D.setColor(SETTLEMENT_COLOR);
        graphics2D.fillOval((int) (drawX - markerRadius), (int) (drawY - markerRadius), markerRadius * 2, markerRadius * 2);

        graphics2D.setColor(Color.BLACK);
        graphics2D.drawOval((int) (drawX - markerRadius), (int) (drawY - markerRadius), markerRadius * 2, markerRadius * 2);
    }

    private void drawAllUnits(Graphics2D graphics2D, double hexSize, double centerX, double centerY) {
        List<Unit> units = gameSession.getUnits();

        for (Unit unit : units) {
            drawUnit(graphics2D, unit, hexSize, centerX, centerY);
        }
    }

    private void drawUnit(Graphics2D graphics2D, Unit unit, double hexSize, double centerX, double centerY) {
        double drawX;
        double drawY;

        if (movementAnimator.isAnimating() && movementAnimator.getAnimatingUnit() == unit) {
            Point2D interpolatedPixel = movementAnimator.getInterpolatedPixelPosition(hexSize);
            drawX = centerX + interpolatedPixel.getX();
            drawY = centerY + interpolatedPixel.getY();
        } else {
            Point2D pixelPosition = HexGeometry.axialToPixel(unit.getCoordinate(), hexSize);
            drawX = centerX + pixelPosition.getX();
            drawY = centerY + pixelPosition.getY();
        }

        int iconSize = (int) (hexSize * UNIT_SIZE_FACTOR);
        Image unitIcon = IconProvider.loadImage(UnitIconProvider.getIconPath(unit.getUnitType()));

        if (unitIcon != null) {
            graphics2D.drawImage(unitIcon, (int) (drawX - iconSize / 2.0), (int) (drawY - iconSize / 2.0), iconSize, iconSize, this);
        } else {
            drawUnitFallbackShape(graphics2D, unit, drawX, drawY, iconSize);
        }

        drawActionPointBadge(graphics2D, unit, drawX, drawY, iconSize);

        if (selectionState.getSelectedUnit() == unit) {
            drawSelectionHighlight(graphics2D, drawX, drawY, iconSize);
        }
    }

    private void drawUnitFallbackShape(Graphics2D graphics2D, Unit unit, double drawX, double drawY, int iconSize) {
        graphics2D.setColor(UnitColorProvider.getColorForUnitType(unit.getUnitType()));
        graphics2D.fillOval((int) (drawX - iconSize / 2.0), (int) (drawY - iconSize / 2.0), iconSize, iconSize);

        graphics2D.setColor(Color.BLACK);
        graphics2D.drawOval((int) (drawX - iconSize / 2.0), (int) (drawY - iconSize / 2.0), iconSize, iconSize);

        String letter = UnitIconProvider.getFallbackLetter(unit.getUnitType());
        graphics2D.setFont(FALLBACK_LABEL_FONT);
        int textWidth = graphics2D.getFontMetrics().stringWidth(letter);
        graphics2D.drawString(letter, (int) (drawX - textWidth / 2.0), (int) (drawY + 5));
    }

    private void drawActionPointBadge(Graphics2D graphics2D, Unit unit, double drawX, double drawY, int iconSize) {
        String apText = unit.getCurrentActionPoints() + "/" + unit.getMaxActionPoints();
        graphics2D.setFont(AP_BADGE_FONT);
        int textWidth = graphics2D.getFontMetrics().stringWidth(apText);
        int badgeY = (int) (drawY + iconSize / 2.0 + 14);

        graphics2D.setColor(new Color(0, 0, 0, 160));
        graphics2D.fillRoundRect((int) (drawX - textWidth / 2.0) - 3, badgeY - 11, textWidth + 6, 14, 6, 6);

        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString(apText, (int) (drawX - textWidth / 2.0), badgeY);
    }

    private void drawSelectionHighlight(Graphics2D graphics2D, double centerX, double centerY, int iconSize) {
        int highlightRadius = iconSize / 2 + 5;
        graphics2D.setColor(Color.YELLOW);
        graphics2D.drawOval(
                (int) (centerX - highlightRadius),
                (int) (centerY - highlightRadius),
                highlightRadius * 2,
                highlightRadius * 2
        );
    }

    public double getCurrentHexSize() {
        return Constants.HEX_SIZE * cameraState.getCurrentZoom();
    }

    public double getPanelCenterX() {
        return getWidth() / 2.0 + cameraState.getOffsetX();
    }

    public double getPanelCenterY() {
        return getHeight() / 2.0 + cameraState.getOffsetY();
    }

    public void updateTooltipForPosition(int screenX, int screenY) {
        double hexSize = getCurrentHexSize();
        double centerX = getPanelCenterX();
        double centerY = getPanelCenterY();

        double relativeX = screenX - centerX;
        double relativeY = screenY - centerY;
        HexCoordinate hoveredCoordinate = HexGeometry.pixelToAxial(relativeX, relativeY, hexSize);

        setToolTipText(buildTooltipText(hoveredCoordinate));
    }

    private String buildTooltipText(HexCoordinate coordinate) {
        Unit hoveredUnit = findUnitAtCoordinateForTooltip(coordinate);
        if (hoveredUnit != null) {
            return buildUnitTooltip(hoveredUnit);
        }

        Building hoveredBuilding = gameSession.getBuildingAt(coordinate);
        if (hoveredBuilding != null) {
            return buildBuildingTooltip(hoveredBuilding);
        }

        return null;
    }

    private Unit findUnitAtCoordinateForTooltip(HexCoordinate coordinate) {
        List<Unit> units = gameSession.getUnits();

        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getCoordinate().equals(coordinate)) {
                return units.get(i);
            }
        }

        return null;
    }

    private String buildUnitTooltip(Unit unit) {
        String apText = unit.getCurrentActionPoints() + "/" + unit.getMaxActionPoints();
        return unit.getUnitType().name() + " | AP: " + apText;
    }

    private String buildBuildingTooltip(Building building) {
        String workerText = building.getStationedWorkers().size() + "/" + building.getWorkerCapacity();
        return building.getBuildingType().name() + " | Workers: " + workerText;
    }
}