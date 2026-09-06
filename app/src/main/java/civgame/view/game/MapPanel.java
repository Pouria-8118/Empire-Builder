package civgame.view.game;

import civgame.model.building.Building;
import civgame.model.building.TownHall;
import civgame.model.game.ActiveDisasterEffect;
import civgame.model.game.GameSession;
import civgame.model.game.NaturalDisasterType;
import civgame.model.game.SeasonType;
import civgame.model.animal.Bear;
import civgame.model.animal.WildAnimal;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexEdge;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.map.TileVisibility;
import civgame.model.map.Wall;
import civgame.model.pirate.Pirate;
import civgame.model.tribe.Tribe;
import civgame.model.map.TradingPost;
import civgame.model.unit.Unit;
import civgame.model.game.EnemyUnitMarker;
import civgame.util.BuildingColorProvider;
import civgame.util.BuildingIconProvider;
import civgame.util.Constants;
import civgame.util.HexGeometry;
import civgame.util.HexTileInfoTextBuilder;
import civgame.util.IconProvider;
import civgame.util.ResourceIconProvider;
import civgame.util.TerrainColorProvider;
import civgame.util.TribeColorProvider;
import civgame.util.UnitColorProvider;
import civgame.util.UnitIconProvider;

import javax.swing.Timer;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapPanel extends JPanel {

    private static final double UNIT_SIZE_FACTOR = 0.5;
    private static final double BUILDING_SIZE_FACTOR = 1;
    private static final double TOWN_HALL_SIZE_FACTOR = 1.4;
    private static final double REACHABLE_TILE_BRIGHTEN_AMOUNT = 0.16;
    private static final double STACKED_UNIT_SIZE_SCALE = 0.41;
    private static final double STACKED_UNIT_CLUSTER_RADIUS_FACTOR = 0.2;

    private static final Color OUTSIDE_BORDER_TINT = new Color(0, 0, 0, 110);
    private static final Color SETTLEMENT_COLOR = new Color(150, 90, 200);
    private static final Color REACHABLE_TILE_OUTLINE = new Color(255, 255, 255, 130);
    private static final Color DEPLETED_RESOURCE_TINT = new Color(180, 30, 30, 70);
    private static final Font FALLBACK_LABEL_FONT = new Font("SansSerif", Font.BOLD, 12);
    private static final Font AP_BADGE_FONT = new Font("SansSerif", Font.BOLD, 10);

    private final WeatherEffectRenderer weatherEffectRenderer = new WeatherEffectRenderer();
    private final GameSession gameSession;
    private final CameraState cameraState;
    private final SelectionState selectionState;
    private final UnitMovementAnimator movementAnimator;
    private List<String> hoveredTileInfoLines;
    private int hudTopInset = 60;
    private int hudBottomInset = 150;

    
    public MapPanel(GameSession gameSession, CameraState cameraState, SelectionState selectionState, UnitMovementAnimator movementAnimator) {
        this.gameSession = gameSession;
        this.cameraState = cameraState;
        this.selectionState = selectionState;
        this.movementAnimator = movementAnimator;
        setBackground(Color.BLACK);

        Timer weatherTimer = new Timer(50, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            weatherEffectRenderer.advance(gameSession.getCurrentSeason());
            gameSession.tickActiveDisasterEffects();
            repaint();
        }
        });
        weatherTimer.start();


        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent mouseEvent) {
                updateHoveredTileInfo(mouseEvent);
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                double factor = mouseWheelEvent.getWheelRotation() < 0
                        ? Constants.ZOOM_WHEEL_FACTOR
                        : 1.0 / Constants.ZOOM_WHEEL_FACTOR;
                cameraState.zoom(factor, mouseWheelEvent.getX(), mouseWheelEvent.getY(), getWidth(), getHeight());
                repaint();
            }
        });
    }

    private void updateHoveredTileInfo(MouseEvent mouseEvent) {
        double hexSize = getCurrentHexSize();
        double centerX = getPanelCenterX();
        double centerY = getPanelCenterY();

        double relativeX = mouseEvent.getX() - centerX;
        double relativeY = mouseEvent.getY() - centerY;
        HexCoordinate coordinate = HexGeometry.pixelToAxial(relativeX, relativeY, hexSize);

        if (!gameSession.getHexMap().containsCoordinate(coordinate)) {
            hoveredTileInfoLines = null;
            repaint();
            return;
        }

        HexTile tile = gameSession.getHexMap().getTile(coordinate);
        if (tile.getVisibility() == TileVisibility.UNSEEN) {
            hoveredTileInfoLines = null;
        } else {
            hoveredTileInfoLines = HexTileInfoTextBuilder.build(tile, gameSession.getHexMap());
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;

        double hexSize = getCurrentHexSize();
        double centerX = getPanelCenterX();
        double centerY = getPanelCenterY();

        drawAllTiles(graphics2D, hexSize, centerX, centerY);
        drawAllRivers(graphics2D, hexSize, centerX, centerY);
        drawAllWalls(graphics2D, hexSize, centerX, centerY);
        drawTownHall(graphics2D, hexSize, centerX, centerY);
        drawAllWildAnimals(graphics2D, hexSize, centerX, centerY);
        drawAllPirates(graphics2D, hexSize, centerX, centerY);
        drawHoveredTileInfoBox(graphics2D);
        drawAllTradingPosts(graphics2D, hexSize, centerX, centerY);
        drawAllTribeCamps(graphics2D, hexSize, centerX, centerY);
        weatherEffectRenderer.draw(graphics2D, gameSession.getCurrentSeason(), getWidth(), getHeight());
        drawSeasonBanner(graphics2D);
        drawActiveDisasterEffects(graphics2D, hexSize, centerX, centerY);
        drawAllBuildings(graphics2D, hexSize, centerX, centerY);
        drawAllSettlements(graphics2D, hexSize, centerX, centerY);
        drawAllEnemyUnits(graphics2D, hexSize, centerX, centerY);
        drawAllUnits(graphics2D, hexSize, centerX, centerY);
    }

    private void drawAllTiles(Graphics2D graphics2D, double hexSize, double centerX, double centerY) {
        HexMap hexMap = gameSession.getHexMap();
        Map<HexCoordinate, HexTile> allTiles = hexMap.getAllTiles();

        for (HexTile tile : allTiles.values()) {
            drawTile(graphics2D, tile, hexSize, centerX, centerY);
        }
    }

    private void drawAllRivers(Graphics2D graphics2D, double hexSize, double centerX, double centerY) {
        HexMap hexMap = gameSession.getHexMap();
        Set<HexEdge> riverEdges = hexMap.getRiverEdges();

        graphics2D.setColor(new Color(50, 110, 200));
        graphics2D.setStroke(new BasicStroke((float) (hexSize * 0.14)));

        for (HexEdge edge : riverEdges) {
            drawEdgeFeatureLine(graphics2D, hexMap, edge, hexSize, centerX, centerY);
        }

        graphics2D.setStroke(new BasicStroke(1f));
    }

    private void drawEdgeFeatureLine(Graphics2D graphics2D, HexMap hexMap, HexEdge edge, double hexSize, double centerX, double centerY) {
        HexTile firstTile = hexMap.getTile(edge.getFirst());
        HexTile secondTile = hexMap.getTile(edge.getSecond());

        if (firstTile == null || secondTile == null) {
            return;
        }
        if (firstTile.getVisibility() == TileVisibility.UNSEEN && secondTile.getVisibility() == TileVisibility.UNSEEN) {
            return;
        }

        Point2D firstPixel = HexGeometry.axialToPixel(edge.getFirst(), hexSize);
        Point2D secondPixel = HexGeometry.axialToPixel(edge.getSecond(), hexSize);

        double firstCenterX = centerX + firstPixel.getX();
        double firstCenterY = centerY + firstPixel.getY();
        double secondCenterX = centerX + secondPixel.getX();
        double secondCenterY = centerY + secondPixel.getY();

        Point2D[] edgeVertices = computeSharedEdgeVertices(firstCenterX, firstCenterY, secondCenterX, secondCenterY, hexSize);

        graphics2D.drawLine((int) edgeVertices[0].getX(), (int) edgeVertices[0].getY(), (int) edgeVertices[1].getX(), (int) edgeVertices[1].getY());
    }

    private Point2D[] computeSharedEdgeVertices(double centerAX, double centerAY, double centerBX, double centerBY, double hexSize) {
        double midX = (centerAX + centerBX) / 2.0;
        double midY = (centerAY + centerBY) / 2.0;

        double angle = Math.atan2(centerBY - centerAY, centerBX - centerAX);
        double perpendicular = angle + Math.PI / 2.0;

        double halfEdgeLength = hexSize / 2.0;
        double offsetX = halfEdgeLength * Math.cos(perpendicular);
        double offsetY = halfEdgeLength * Math.sin(perpendicular);

        Point2D vertexA = new Point2D.Double(midX + offsetX, midY + offsetY);
        Point2D vertexB = new Point2D.Double(midX - offsetX, midY - offsetY);

        return new Point2D[]{vertexA, vertexB};
    }

    private void drawAllWalls(Graphics2D graphics2D, double hexSize, double centerX, double centerY) {
        HexMap hexMap = gameSession.getHexMap();
        Map<HexEdge, Wall> walls = hexMap.getAllActiveWalls();

        graphics2D.setColor(new Color(90, 80, 70));
        graphics2D.setStroke(new BasicStroke((float) (hexSize * 0.22)));

        for (HexEdge edge : walls.keySet()) {
            drawEdgeFeatureLine(graphics2D, hexMap, edge, hexSize, centerX, centerY);
        }

        graphics2D.setStroke(new BasicStroke(1f));
    }

    private void drawAllWildAnimals(Graphics2D graphics2D, double hexSize, double centerX, double centerY) {
        List<WildAnimal> wildAnimals = gameSession.getWildAnimals();

        for (int i = 0; i < wildAnimals.size(); i++) {
            WildAnimal wildAnimal = wildAnimals.get(i);
            Point2D pixelPosition = HexGeometry.axialToPixel(wildAnimal.getCoordinate(), hexSize);
            double drawX = centerX + pixelPosition.getX();
            double drawY = centerY + pixelPosition.getY();
            int iconSize = (int) (hexSize * UNIT_SIZE_FACTOR);

            boolean isBear = wildAnimal instanceof Bear;
            Image animalIcon = IconProvider.loadImage(isBear ? Constants.BEAR_ICON_PATH : Constants.WILD_ANIMAL_ICON_PATH);

            if (animalIcon != null) {
                graphics2D.drawImage(animalIcon, (int) (drawX - iconSize / 2.0), (int) (drawY - iconSize / 2.0), iconSize, iconSize, this);
            } else {
                graphics2D.setColor(isBear ? new Color(60, 40, 20) : new Color(101, 67, 33));
                graphics2D.fillOval((int) (drawX - iconSize / 2.0), (int) (drawY - iconSize / 2.0), iconSize, iconSize);
                graphics2D.setColor(Color.BLACK);
                graphics2D.drawOval((int) (drawX - iconSize / 2.0), (int) (drawY - iconSize / 2.0), iconSize, iconSize);
            }

            graphics2D.setColor(Color.WHITE);
            graphics2D.setFont(new Font("SansSerif", Font.BOLD, (int) (iconSize * 0.35)));
            String label = isBear ? "BEAR " + wildAnimal.getCurrentHitPoints() : String.valueOf(wildAnimal.getCurrentHitPoints());
            graphics2D.drawString(label, (int) (drawX - iconSize * 0.4), (int) (drawY + iconSize * 0.15));
        }
    }

    private void drawAllPirates(Graphics2D graphics2D, double hexSize, double centerX, double centerY) {
        List<Pirate> pirates = gameSession.getPirates();

        for (int i = 0; i < pirates.size(); i++) {
            Pirate pirate = pirates.get(i);
            Point2D pixelPosition = HexGeometry.axialToPixel(pirate.getCoordinate(), hexSize);
            double drawX = centerX + pixelPosition.getX();
            double drawY = centerY + pixelPosition.getY();
            int iconSize = (int) (hexSize * UNIT_SIZE_FACTOR);

            Image pirateIcon = IconProvider.loadImage(Constants.PIRATE_ICON_PATH);
            if (pirateIcon != null) {
                graphics2D.drawImage(pirateIcon, (int) (drawX - iconSize / 2.0), (int) (drawY - iconSize / 2.0), iconSize, iconSize, this);
            } else {
                graphics2D.setColor(new Color(30, 30, 30));
                graphics2D.fillOval((int) (drawX - iconSize / 2.0), (int) (drawY - iconSize / 2.0), iconSize, iconSize);
                graphics2D.setColor(Color.RED);
                graphics2D.drawOval((int) (drawX - iconSize / 2.0), (int) (drawY - iconSize / 2.0), iconSize, iconSize);
            }

            graphics2D.setColor(Color.WHITE);
            graphics2D.setFont(new Font("SansSerif", Font.BOLD, (int) (iconSize * 0.35)));
            graphics2D.drawString("P" + pirate.getCurrentHitPoints(), (int) (drawX - iconSize * 0.3), (int) (drawY + iconSize * 0.15));
        }
    }
    
    private void drawAllTradingPosts(Graphics2D graphics2D, double hexSize, double centerX, double centerY) {
        List<TradingPost> tradingPosts = gameSession.getTradingPosts();

        for (int i = 0; i < tradingPosts.size(); i++) {
            TradingPost tradingPost = tradingPosts.get(i);
            HexTile tile = gameSession.getHexMap().getTile(tradingPost.getCoordinate());
            if (tile == null || tile.getVisibility() == TileVisibility.UNSEEN) {
                continue;
            }

            Point2D pixelPosition = HexGeometry.axialToPixel(tradingPost.getCoordinate(), hexSize);
            double drawX = centerX + pixelPosition.getX();
            double drawY = centerY + pixelPosition.getY();
            int markerSize = (int) (hexSize * 0.7);

            Polygon diamond = new Polygon();
            diamond.addPoint((int) drawX, (int) (drawY - markerSize / 2.0));
            diamond.addPoint((int) (drawX + markerSize / 2.0), (int) drawY);
            diamond.addPoint((int) drawX, (int) (drawY + markerSize / 2.0));
            diamond.addPoint((int) (drawX - markerSize / 2.0), (int) drawY);

            Image tradingPostIcon = IconProvider.loadImage(Constants.TRADING_POST_ICON_PATH);
            if (tradingPostIcon != null) {
                graphics2D.drawImage(tradingPostIcon, (int) (drawX - markerSize / 2.0), (int) (drawY - markerSize / 2.0), markerSize, markerSize, this);
            } else {
                graphics2D.setColor(new Color(180, 140, 40));
                graphics2D.fillPolygon(diamond);
                graphics2D.setColor(Color.BLACK);
                graphics2D.drawPolygon(diamond);
            }
        }
    }

    private void drawAllTribeCamps(Graphics2D graphics2D, double hexSize, double centerX, double centerY) {
        List<Tribe> tribes = gameSession.getTribes();

        for (int i = 0; i < tribes.size(); i++) {
            Tribe tribe = tribes.get(i);
            if (!tribe.isDiscovered() || tribe.isDefeated()) {
                continue;
            }

            HexTile tile = gameSession.getHexMap().getTile(tribe.getCampCoordinate());
            if (tile == null || tile.getVisibility() == TileVisibility.UNSEEN) {
                continue;
            }

            Point2D pixelPosition = HexGeometry.axialToPixel(tribe.getCampCoordinate(), hexSize);
            double drawX = centerX + pixelPosition.getX();
            double drawY = centerY + pixelPosition.getY();
            int markerSize = (int) (hexSize * 0.75);

            Image tribeCampIcon = IconProvider.loadImage(Constants.TRIBE_CAMP_ICON_PATH);
            if (tribeCampIcon != null) {
                graphics2D.drawImage(tribeCampIcon, (int) (drawX - markerSize / 2.0), (int) (drawY - markerSize / 2.0), markerSize, markerSize, this);
            } else {
                graphics2D.setColor(TribeColorProvider.getColorForTribeType(tribe.getTribeType()));
                graphics2D.fillRect((int) (drawX - markerSize / 2.0), (int) (drawY - markerSize / 2.0), markerSize, markerSize);
                graphics2D.setColor(Color.BLACK);
                graphics2D.drawRect((int) (drawX - markerSize / 2.0), (int) (drawY - markerSize / 2.0), markerSize, markerSize);
            }

            graphics2D.setColor(Color.BLACK);
            graphics2D.setFont(new Font("SansSerif", Font.BOLD, (int) (markerSize * 0.3)));
            graphics2D.drawString(TribeColorProvider.getLetter(tribe.getTribeType()), (int) (drawX - markerSize * 0.18), (int) (drawY + markerSize * 0.18));
        }
    }

    private void drawSeasonBanner(Graphics2D graphics2D) {
        SeasonType season = gameSession.getCurrentSeason();
        int bannerWidth = Math.max(130, (int) (getWidth() * 0.09));
        int bannerY = hudTopInset;

        graphics2D.setColor(getSeasonBannerColor(season));
        graphics2D.fillRoundRect(10, bannerY +20, bannerWidth, 30, 10, 10);
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawRoundRect(10, bannerY + 20, bannerWidth, 30, 10, 10);
        graphics2D.setFont(new Font("SansSerif", Font.BOLD, 14));
        graphics2D.drawString(season.name(), 22, bannerY + 40);
    }

    private void drawHoveredTileInfoBox(Graphics2D graphics2D) {
        if (hoveredTileInfoLines == null || hoveredTileInfoLines.isEmpty()) {
            return;
        }

        int lineHeight = 16;
        int boxWidth = Math.max(220, (int) (getWidth() * 0.16));
        int boxHeight = hoveredTileInfoLines.size() * lineHeight + 12;
        int boxX = 10;
        int boxY = getHeight() - hudBottomInset - boxHeight;

        graphics2D.setColor(new Color(0, 0, 0, 170));
        graphics2D.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 10, 10);
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 10, 10);

        graphics2D.setFont(new Font("SansSerif", Font.PLAIN, 12));
        for (int i = 0; i < hoveredTileInfoLines.size(); i++) {
            graphics2D.drawString(hoveredTileInfoLines.get(i), boxX + 8, boxY + 16 + i * lineHeight);
        }
    }

    private void drawActiveDisasterEffects(Graphics2D graphics2D, double hexSize, double centerX, double centerY) {
        List<ActiveDisasterEffect> effects = gameSession.getActiveDisasterEffects();

        for (int i = 0; i < effects.size(); i++) {
            ActiveDisasterEffect effect = effects.get(i);
            Point2D pixelPosition = HexGeometry.axialToPixel(effect.getCenterCoordinate(), hexSize);
            double effectX = centerX + pixelPosition.getX();
            double effectY = centerY + pixelPosition.getY();

            if (effect.getType() == NaturalDisasterType.EARTHQUAKE) {
                drawEarthquakeEffect(graphics2D, effectX, effectY, hexSize, effect.getRemainingTicks());
            } else if (effect.getType() == NaturalDisasterType.FLOOD) {
                drawFloodEffect(graphics2D, effectX, effectY, hexSize, effect.getRemainingTicks());
            } else if (effect.getType() == NaturalDisasterType.TSUNAMI) {
                drawTsunamiEffect(graphics2D, effectX, effectY, hexSize, effect.getRemainingTicks());
            } else if (effect.getType() == NaturalDisasterType.VOLCANIC_ERUPTION) {
                drawVolcanicEruptionEffect(graphics2D, effectX, effectY, hexSize, effect.getRemainingTicks());
            } else if (effect.getType() == NaturalDisasterType.TORNADO) {
                drawTornadoEffect(graphics2D, effectX, effectY, hexSize, effect.getRemainingTicks());
            } else if (effect.getType() == NaturalDisasterType.AVALANCHE) {
                drawAvalancheEffect(graphics2D, effectX, effectY, hexSize, effect.getRemainingTicks());
            } else if (effect.getType() == NaturalDisasterType.SEA_STORM) {
                drawSeaStormEffect(graphics2D, effectX, effectY, hexSize, effect.getRemainingTicks());
            } else if (effect.getType() == NaturalDisasterType.BEAR_ATTACK) {
                drawBearAttackEffect(graphics2D, effectX, effectY, hexSize, effect.getRemainingTicks());
            }
        }
    }

    private void drawEarthquakeEffect(Graphics2D graphics2D, double centerX, double centerY, double hexSize, int remainingTicks) {
        double progress = 1.0 - (remainingTicks / (double) Constants.DISASTER_EFFECT_DURATION_TICKS);
        
        int crackCount = 8;
        int alpha = (int) (220 * (1.0 - progress));
        
        graphics2D.setColor(new Color(90, 60, 30, alpha));
        graphics2D.setStroke(new BasicStroke(3f));

        for (int i = 0; i < crackCount; i++) {
            double angle = (i * Math.PI * 2) / crackCount;
            double length = hexSize * 1.5 * progress;
            int endX = (int) (centerX + length * Math.cos(angle));
            int endY = (int) (centerY + length * Math.sin(angle));
            graphics2D.drawLine((int) centerX, (int) centerY, endX, endY);
        }

        graphics2D.setStroke(new BasicStroke(1f));
    }

    private void drawFloodEffect(Graphics2D graphics2D, double centerX, double centerY, double hexSize, int remainingTicks) {
        double progress = 1.0 - (remainingTicks / (double) Constants.DISASTER_EFFECT_DURATION_TICKS);
        int radius = (int) (hexSize * 2 * progress);

        graphics2D.setColor(new Color(40, 100, 180, 120));
        graphics2D.fillOval((int) (centerX - radius), (int) (centerY - radius), radius * 2, radius * 2);
    }

    private void drawTsunamiEffect(Graphics2D graphics2D, double centerX, double centerY, double hexSize, int remainingTicks) {
        double progress = 1.0 - (remainingTicks / (double) Constants.DISASTER_EFFECT_DURATION_TICKS);
        int waveCount = 3;
        
        graphics2D.setStroke(new BasicStroke(3f));
        
        for (int i = 0; i < waveCount; i++) {
            double waveProgress = progress - (i * 0.25);
            if (waveProgress < 0) {
                continue;
            }
            int radius = (int) (hexSize * 3 * waveProgress);
            int alpha = (int) (200 * (1.0 - waveProgress));
            
            graphics2D.setColor(new Color(40, 100, 200, alpha));
            graphics2D.drawOval((int) (centerX - radius), (int) (centerY - radius), radius * 2, radius * 2);
        }
        
        graphics2D.setStroke(new BasicStroke(1f));
    }

    private void drawVolcanicEruptionEffect(Graphics2D graphics2D, double centerX, double centerY, double hexSize, int remainingTicks) {
        double progress = 1.0 - (remainingTicks / (double) Constants.DISASTER_EFFECT_DURATION_TICKS);
        
        int smokeRadius = (int) (hexSize * 1.5 * progress);
        int smokeAlpha = (int) (150 * (1.0 - progress));
        graphics2D.setColor(new Color(80, 80, 80, smokeAlpha));
        graphics2D.fillOval((int) (centerX - smokeRadius), (int) (centerY - smokeRadius - hexSize), smokeRadius * 2, smokeRadius * 2);
        
        int lavaRadius = (int) (hexSize * 0.8);
        int lavaAlpha = (int) (200 * (1.0 - progress));
        graphics2D.setColor(new Color(255, 100, 0, lavaAlpha));
        graphics2D.fillOval((int) (centerX - lavaRadius), (int) (centerY - lavaRadius), lavaRadius * 2, lavaRadius * 2);
    }

    private void drawTornadoEffect(Graphics2D graphics2D, double centerX, double centerY, double hexSize, int remainingTicks) {
        double progress = 1.0 - (remainingTicks / (double) Constants.DISASTER_EFFECT_DURATION_TICKS);
        double rotation = progress * Math.PI * 4;
        
        graphics2D.setStroke(new BasicStroke(2f));
        
        for (int i = 0; i < 8; i++) {
            double angle = rotation + (i * Math.PI / 4);
            int radius = (int) (hexSize * 0.8);
            int endX = (int) (centerX + radius * Math.cos(angle));
            int endY = (int) (centerY + radius * Math.sin(angle));
            
            int alpha = (int) (180 * (1.0 - progress));
            graphics2D.setColor(new Color(100, 100, 100, alpha));
            graphics2D.drawLine((int) centerX, (int) centerY, endX, endY);
        }
        
        graphics2D.setStroke(new BasicStroke(1f));
    }

    private void drawAvalancheEffect(Graphics2D graphics2D, double centerX, double centerY, double hexSize, int remainingTicks) {
        double progress = 1.0 - (remainingTicks / (double) Constants.DISASTER_EFFECT_DURATION_TICKS);
        
        int particleCount = 12;
        int alpha = (int) (200 * (1.0 - progress));
        
        for (int i = 0; i < particleCount; i++) {
            double angle = (i * Math.PI * 2) / particleCount;
            int radius = (int) (hexSize * 1.5 * progress);
            int particleX = (int) (centerX + radius * Math.cos(angle));
            int particleY = (int) (centerY + radius * Math.sin(angle));
            
            int particleSize = (int) (hexSize * 0.2);
            graphics2D.setColor(new Color(220, 220, 220, alpha));
            graphics2D.fillOval(particleX - particleSize / 2, particleY - particleSize / 2, particleSize, particleSize);
        }
    }

    private void drawSeaStormEffect(Graphics2D graphics2D, double centerX, double centerY, double hexSize, int remainingTicks) {
        double progress = 1.0 - (remainingTicks / (double) Constants.DISASTER_EFFECT_DURATION_TICKS);
        
        int rainCount = 20;
        int rainAlpha = (int) (150 * (1.0 - progress));
        graphics2D.setColor(new Color(100, 150, 200, rainAlpha));
        graphics2D.setStroke(new BasicStroke(1f));
        
        for (int i = 0; i < rainCount; i++) {
            double angle = (i * Math.PI * 2) / rainCount;
            int radius = (int) (hexSize * 2 * progress);
            int rainX = (int) (centerX + radius * Math.cos(angle));
            int rainY = (int) (centerY + radius * Math.sin(angle));
            
            graphics2D.drawLine(rainX, rainY, rainX - 3, rainY + 8);
        }
        
        int waveRadius = (int) (hexSize * 1.5);
        int waveAlpha = (int) (180 * (1.0 - progress));
        graphics2D.setColor(new Color(40, 80, 150, waveAlpha));
        graphics2D.setStroke(new BasicStroke(2f));
        graphics2D.drawOval((int) (centerX - waveRadius), (int) (centerY - waveRadius), waveRadius * 2, waveRadius * 2);
        
        graphics2D.setStroke(new BasicStroke(1f));
    }

    private void drawBearAttackEffect(Graphics2D graphics2D, double centerX, double centerY, double hexSize, int remainingTicks) {
        double progress = 1.0 - (remainingTicks / (double) Constants.DISASTER_EFFECT_DURATION_TICKS);
        
        int pawSize = (int) (hexSize * 0.6 * (1.0 - progress * 0.5));
        int alpha = (int) (200 * (1.0 - progress));
        
        graphics2D.setColor(new Color(180, 50, 50, alpha));
        
        int padSize = pawSize / 2;
        graphics2D.fillOval((int) (centerX - padSize / 2), (int) (centerY - padSize / 2), padSize, padSize);
        
        int toeSize = pawSize / 4;
        int toeOffset = pawSize / 2;
        graphics2D.fillOval((int) (centerX - toeOffset - toeSize / 2), (int) (centerY - toeOffset), toeSize, toeSize);
        graphics2D.fillOval((int) (centerX + toeOffset - toeSize / 2), (int) (centerY - toeOffset), toeSize, toeSize);
        graphics2D.fillOval((int) (centerX - toeOffset), (int) (centerY + toeOffset / 2 - toeSize / 2), toeSize, toeSize);
        graphics2D.fillOval((int) (centerX + toeOffset - toeSize), (int) (centerY + toeOffset / 2 - toeSize / 2), toeSize, toeSize);
    }

    private Color getSeasonBannerColor(SeasonType season) {
        if (season == SeasonType.SPRING) {
            return new Color(150, 220, 130);
        }
        if (season == SeasonType.SUMMER) {
            return new Color(230, 210, 90);
        }
        if (season == SeasonType.AUTUMN) {
            return new Color(210, 140, 60);
        }
        return new Color(190, 220, 240);
    }

    private void drawRoadMarker(Graphics2D graphics2D, double centerX, double centerY, double hexSize) {
        graphics2D.setColor(new Color(150, 110, 60));
        int dotSize = (int) (hexSize * 0.18);
        int dotX = (int) (centerX - dotSize / 2.0);
        int dotY = (int) (centerY + hexSize * 0.55 - dotSize / 2.0);
        graphics2D.fillOval(dotX, dotY, dotSize, dotSize);
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

        if (selectionState.getAttackableTiles().contains(tile.getCoordinate())) {
            graphics2D.setColor(new Color(220, 40, 40));
            graphics2D.drawPolygon(hexagonPolygon);
        }

        if (tile.getTerrainType() == TerrainType.MOUNTAIN_RANGE && tile.getVisibility() != TileVisibility.UNSEEN) {
            drawMountainRangeOverlay(graphics2D, tileCenterX, tileCenterY, hexSize);
        }

        if (tile.isRoadPresent() && tile.getVisibility() != TileVisibility.UNSEEN) {
            drawRoadMarker(graphics2D, tileCenterX, tileCenterY, hexSize);
        }

        boolean hasBuildingHere = gameSession.getBuildingAt(tile.getCoordinate()) != null;

        if (tile.hasResource() && tile.getVisibility() != TileVisibility.UNSEEN && !hasBuildingHere) {
            drawResourceMarker(graphics2D, tile, tileCenterX, tileCenterY, hexSize);
        }

        if (tile.getVisibility() != TileVisibility.UNSEEN && !gameSession.isWithinBorder(tile.getCoordinate())) {
            graphics2D.setColor(OUTSIDE_BORDER_TINT);
            graphics2D.fillPolygon(hexagonPolygon);
        }

        if (tile.isResourceDepleted() && tile.getVisibility() != TileVisibility.UNSEEN) {
            graphics2D.setColor(DEPLETED_RESOURCE_TINT);
            graphics2D.fillPolygon(hexagonPolygon);
        }

        drawTerritoryOverlay(graphics2D, tile, hexagonPolygon);

    }

    private void drawTerritoryOverlay(Graphics2D graphics2D, HexTile tile, Polygon hexagonPolygon) {
        if (tile.getVisibility() == TileVisibility.UNSEEN) {
            return;
        }
        HexCoordinate coord = tile.getCoordinate();
        if (gameSession.getAllyTerritory().contains(coord)) {
            graphics2D.setColor(new Color(80, 220, 80, 130));
            graphics2D.setStroke(new BasicStroke(3f));
            graphics2D.drawPolygon(hexagonPolygon);
            graphics2D.setStroke(new BasicStroke(1f));
        } else if (gameSession.getEnemyTerritory().contains(coord)) {
            graphics2D.setColor(new Color(230, 60, 60, 130));
            graphics2D.setStroke(new BasicStroke(3f));
            graphics2D.drawPolygon(hexagonPolygon);
            graphics2D.setStroke(new BasicStroke(1f));
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

    private void drawMountainRangeOverlay(Graphics2D graphics2D, double centerX, double centerY, double hexSize) {
        graphics2D.setColor(new Color(30, 25, 25));

        int peakWidth = (int) (hexSize * 0.35);
        int peakHeight = (int) (hexSize * 0.5);
        int baseY = (int) (centerY + hexSize * 0.35);

        drawPeak(graphics2D, (int) (centerX - hexSize * 0.35), baseY, peakWidth, peakHeight);
        drawPeak(graphics2D, (int) centerX, baseY, peakWidth, peakHeight);
        drawPeak(graphics2D, (int) (centerX + hexSize * 0.35), baseY, peakWidth, peakHeight);
    }

    private void drawPeak(Graphics2D graphics2D, int baseCenterX, int baseY, int width, int height) {
        Polygon peakPolygon = new Polygon();
        peakPolygon.addPoint(baseCenterX - width / 2, baseY);
        peakPolygon.addPoint(baseCenterX, baseY - height);
        peakPolygon.addPoint(baseCenterX + width / 2, baseY);
        graphics2D.fillPolygon(peakPolygon);
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
        boolean unitStandingHere = isUnitStandingOn(tile.getCoordinate());

        double markerCenterX = centerX;
        double markerCenterY = centerY;
        int markerSize;

        if (unitStandingHere) {
            markerSize = (int) (hexSize * 0.3);
            markerCenterX = centerX - hexSize * 0.4;
            markerCenterY = centerY - hexSize * 0.4;
        } else {
            markerSize = (int) (hexSize * 0.42);
        }

        String iconPath = ResourceIconProvider.getIconPath(tile.getResourceType());
        Image resourceIcon = null;
        if (iconPath != null) {
            resourceIcon = IconProvider.loadImage(iconPath);
        }

        if (resourceIcon != null) {
            graphics2D.drawImage(resourceIcon, (int) (markerCenterX - markerSize / 2.0), (int) (markerCenterY - markerSize / 2.0), markerSize, markerSize, this);
        } else {
            double fallbackRadius = markerSize / 2.0;
            graphics2D.setColor(Color.WHITE);
            graphics2D.fillOval((int) (markerCenterX - fallbackRadius), (int) (markerCenterY - fallbackRadius), (int) (fallbackRadius * 2), (int) (fallbackRadius * 2));
        }

        if (tile.isIronDepositPresent()) {
            drawIronDepositOverlay(graphics2D, centerX, centerY, hexSize);
        }
    }

    private boolean isUnitStandingOn(HexCoordinate coordinate) {
        List<Unit> units = gameSession.getUnits();

        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getCoordinate().equals(coordinate)) {
                return true;
            }
        }

        return false;
    }

    private void drawIronDepositOverlay(Graphics2D graphics2D, double centerX, double centerY, double hexSize) {
        int overlaySize = (int) (hexSize * 0.3);
        double offsetX = centerX + hexSize * 0.32;
        double offsetY = centerY + hexSize * 0.32;

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

    private void drawAllEnemyUnits(Graphics2D graphics2D, double hexSize, double centerX, double centerY) {
        List<EnemyUnitMarker> enemyUnits = gameSession.getVisibleEnemyUnits();
        
        for (int i = 0; i < enemyUnits.size(); i++) {
            EnemyUnitMarker marker = enemyUnits.get(i);
            HexTile tile = gameSession.getHexMap().getTile(marker.getCoordinate());
            if (tile == null || tile.getVisibility() == TileVisibility.UNSEEN) {
                continue;
            }
            
            Point2D pixelPosition = HexGeometry.axialToPixel(marker.getCoordinate(), hexSize);
            double drawX = centerX + pixelPosition.getX();
            double drawY = centerY + pixelPosition.getY();
            int iconSize = (int) (hexSize * UNIT_SIZE_FACTOR);
            
            drawEnemyUnitMarker(graphics2D, marker, drawX, drawY, iconSize);
        }
    }
    
    private void drawEnemyUnitMarker(Graphics2D graphics2D,
                                      EnemyUnitMarker marker,
                                      double drawX,
                                      double drawY,
                                      int iconSize) {
        if (marker.isFloating()) {
            drawFloatingEnemyUnit(graphics2D, drawX, drawY, iconSize);
        } else {
            Image unitIcon = IconProvider.loadImage(UnitIconProvider.getIconPath(marker.getUnitType()));
            if (unitIcon != null) {
                graphics2D.drawImage(unitIcon, (int) (drawX - iconSize / 2.0), (int) (drawY - iconSize / 2.0), iconSize, iconSize, this);
            } else {
                drawEnemyUnitFallbackShape(graphics2D, marker, drawX, drawY, iconSize);
            }
        }
        
        drawEnemyOwnerBadge(graphics2D, marker, drawX, drawY, iconSize);
    }
    
    private void drawFloatingEnemyUnit(Graphics2D graphics2D, double drawX, double drawY, int iconSize) {
        Image boatIcon = IconProvider.loadImage(Constants.FLOATING_UNIT_ICON_PATH);
        if (boatIcon != null) {
            graphics2D.drawImage(boatIcon, (int) (drawX - iconSize / 2.0), (int) (drawY - iconSize / 2.0), iconSize, iconSize, this);
            return;
        }
        
        int hullTop = (int) (drawY - iconSize * 0.15);
        int hullBottom = (int) (drawY + iconSize * 0.45);
        int hullLeft = (int) (drawX - iconSize * 0.5);
        int hullRight = (int) (drawX + iconSize * 0.5);
        
        Polygon hullPolygon = new Polygon();
        hullPolygon.addPoint((int) drawX, hullBottom);
        hullPolygon.addPoint(hullLeft, hullTop);
        hullPolygon.addPoint(hullRight, hullTop);
        
        graphics2D.setColor(new Color(120, 80, 40));
        graphics2D.fillPolygon(hullPolygon);
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawPolygon(hullPolygon);
    }
    
    private void drawEnemyUnitFallbackShape(Graphics2D graphics2D,
                                             EnemyUnitMarker marker,
                                             double drawX,
                                             double drawY,
                                             int iconSize) {
        graphics2D.setColor(UnitColorProvider.getColorForUnitType(marker.getUnitType()));
        graphics2D.fillOval((int) (drawX - iconSize / 2.0), (int) (drawY - iconSize / 2.0), iconSize, iconSize);
        
        graphics2D.setColor(new Color(220, 40, 40));
        graphics2D.setStroke(new BasicStroke(2f));
        graphics2D.drawOval((int) (drawX - iconSize / 2.0), (int) (drawY - iconSize / 2.0), iconSize, iconSize);
        graphics2D.setStroke(new BasicStroke(1f));
        
        String letter = UnitIconProvider.getFallbackLetter(marker.getUnitType());
        graphics2D.setFont(FALLBACK_LABEL_FONT);
        int textWidth = graphics2D.getFontMetrics().stringWidth(letter);
        graphics2D.drawString(letter, (int) (drawX - textWidth / 2.0), (int) (drawY + 5));
    }
    
    private void drawEnemyOwnerBadge(Graphics2D graphics2D,
                                      EnemyUnitMarker marker,
                                      double drawX,
                                      double drawY,
                                      int iconSize) {
        String ownerText = marker.getOwnerUsername();
        if (ownerText == null || ownerText.isEmpty()) {
            return;
        }
        
        graphics2D.setFont(AP_BADGE_FONT);
        int textWidth = graphics2D.getFontMetrics().stringWidth(ownerText);
        int badgeY = (int) (drawY - iconSize / 2.0 - 8);
        
        graphics2D.setColor(new Color(180, 40, 40, 180));
        graphics2D.fillRoundRect((int) (drawX - textWidth / 2.0) - 3, badgeY - 11, textWidth + 6, 14, 6, 6);
        
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString(ownerText, (int) (drawX - textWidth / 2.0), badgeY);
    }

    private void drawAllUnits(Graphics2D graphics2D, double hexSize, double centerX, double centerY) {
        Map<HexCoordinate, List<Unit>> stationaryGroups = new HashMap<HexCoordinate, List<Unit>>();
        List<Unit> allUnits = gameSession.getUnits();

        for (int i = 0; i < allUnits.size(); i++) {
            Unit unit = allUnits.get(i);

            if (movementAnimator.isAnimating() && movementAnimator.getAnimatingUnit() == unit) {
                drawAnimatingUnit(graphics2D, unit, hexSize, centerX, centerY);
                continue;
            }

            HexCoordinate coordinate = unit.getCoordinate();
            List<Unit> group = stationaryGroups.get(coordinate);
            if (group == null) {
                group = new ArrayList<Unit>();
                stationaryGroups.put(coordinate, group);
            }
            group.add(unit);
        }

        for (Map.Entry<HexCoordinate, List<Unit>> entry : stationaryGroups.entrySet()) {
            drawUnitGroup(graphics2D, entry.getKey(), entry.getValue(), hexSize, centerX, centerY);
        }
    }

    private void drawAnimatingUnit(Graphics2D graphics2D, Unit unit, double hexSize, double centerX, double centerY) {
        Point2D interpolatedPixel = movementAnimator.getInterpolatedPixelPosition(hexSize);
        double drawX = centerX + interpolatedPixel.getX();
        double drawY = centerY + interpolatedPixel.getY();
        drawSingleUnit(graphics2D, unit, drawX, drawY, hexSize, 1.0);
    }

    private void drawUnitGroup(Graphics2D graphics2D, HexCoordinate coordinate, List<Unit> unitsHere, double hexSize, double centerX, double centerY) {
        Point2D pixelPosition = HexGeometry.axialToPixel(coordinate, hexSize);
        double tileCenterX = centerX + pixelPosition.getX();
        double tileCenterY = centerY + pixelPosition.getY();

        double sizeScale = 1.0;
        double clusterRadius = 0.0;
        if (unitsHere.size() > 1) {
            sizeScale = STACKED_UNIT_SIZE_SCALE;
            clusterRadius = hexSize * STACKED_UNIT_CLUSTER_RADIUS_FACTOR;
        }

        for (int i = 0; i < unitsHere.size(); i++) {
            Unit unit = unitsHere.get(i);
            double angle = (2 * Math.PI * i) / unitsHere.size();
            double drawX = tileCenterX + clusterRadius * Math.cos(angle);
            double drawY = tileCenterY + clusterRadius * Math.sin(angle);
            drawSingleUnit(graphics2D, unit, drawX, drawY, hexSize, sizeScale);
        }
    }

    private void drawSingleUnit(Graphics2D graphics2D, Unit unit, double drawX, double drawY, double hexSize, double sizeScale) {
        int iconSize = (int) (hexSize * UNIT_SIZE_FACTOR * sizeScale);

        if (unit.isFloating()) {
            drawFloatingUnit(graphics2D, drawX, drawY, iconSize);
        } else {
            Image unitIcon = IconProvider.loadImage(UnitIconProvider.getIconPath(unit.getUnitType()));
            if (unitIcon != null) {
                graphics2D.drawImage(unitIcon, (int) (drawX - iconSize / 2.0), (int) (drawY - iconSize / 2.0), iconSize, iconSize, this);
            } else {
                drawUnitFallbackShape(graphics2D, unit, drawX, drawY, iconSize);
            }
        }

        drawActionPointBadge(graphics2D, unit, drawX, drawY, iconSize);

        if (selectionState.getSelectedUnit() == unit) {
            drawSelectionHighlight(graphics2D, drawX, drawY, iconSize);
        }
    }

    private void drawFloatingUnit(Graphics2D graphics2D, double drawX, double drawY, int iconSize) {
        Image boatIcon = IconProvider.loadImage(Constants.FLOATING_UNIT_ICON_PATH);
        if (boatIcon != null) {
            graphics2D.drawImage(boatIcon, (int) (drawX - iconSize / 2.0), (int) (drawY - iconSize / 2.0), iconSize, iconSize, this);
            return;
        }

        int hullTop = (int) (drawY - iconSize * 0.15);
        int hullBottom = (int) (drawY + iconSize * 0.45);
        int hullLeft = (int) (drawX - iconSize * 0.5);
        int hullRight = (int) (drawX + iconSize * 0.5);

        Polygon hullPolygon = new Polygon();
        hullPolygon.addPoint((int) drawX, hullBottom);
        hullPolygon.addPoint(hullLeft, hullTop);
        hullPolygon.addPoint(hullRight, hullTop);

        graphics2D.setColor(new Color(120, 80, 40));
        graphics2D.fillPolygon(hullPolygon);
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawPolygon(hullPolygon);

        int mastX = (int) drawX;
        int mastTop = (int) (drawY - iconSize * 0.55);
        graphics2D.drawLine(mastX, mastTop, mastX, hullTop);
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

    public void setHudInsets(int topInset, int bottomInset) {
        this.hudTopInset = topInset;
        this.hudBottomInset = bottomInset;
    }
}