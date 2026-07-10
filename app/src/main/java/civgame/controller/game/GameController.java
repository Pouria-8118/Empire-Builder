package civgame.controller.game;

import civgame.model.game.ConstructionService;
import civgame.model.game.GameSession;
import civgame.model.game.HexPathFinder;
import civgame.model.game.MovementPlan;
import civgame.model.game.StationingService;
import civgame.model.map.HexCoordinate;
import civgame.model.unit.Builder;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;
import civgame.model.unit.Worker;
import civgame.util.HexGeometry;
import civgame.view.game.CameraState;
import civgame.view.game.ConstructionMenuFactory;
import civgame.view.game.GameFrame;
import civgame.view.game.MapPanel;
import civgame.view.game.SelectionState;
import civgame.view.game.StationingMenuFactory;
import civgame.view.game.UnitMovementAnimator;

import javax.swing.JPopupMenu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

public class GameController {

    private final MapPanel mapPanel;
    private final CameraState cameraState;
    private final SelectionState selectionState;
    private final UnitMovementAnimator movementAnimator;
    private final GameSession gameSession;
    private final HexPathFinder pathFinder;
    private final ConstructionMenuFactory constructionMenuFactory;
    private final StationingMenuFactory stationingMenuFactory;
    private int lastDragX;
    private int lastDragY;
    private boolean wasDragged;

    public GameController(GameFrame gameFrame, GameSession gameSession) {
        this.mapPanel = gameFrame.getMapPanel();
        this.cameraState = gameFrame.getCameraState();
        this.selectionState = gameFrame.getSelectionState();
        this.movementAnimator = gameFrame.getMovementAnimator();
        this.gameSession = gameSession;
        this.pathFinder = new HexPathFinder();
        this.constructionMenuFactory = new ConstructionMenuFactory(new ConstructionService());
        this.stationingMenuFactory = new StationingMenuFactory(new StationingService());
        registerListeners();
    }

    private void registerListeners() {
        MapMouseListener mapMouseListener = new MapMouseListener();
        mapPanel.addMouseListener(mapMouseListener);
        mapPanel.addMouseMotionListener(mapMouseListener);
        mapPanel.addMouseWheelListener(new ZoomListener());
    }

    private void handleLeftClick(int screenX, int screenY) {
        HexCoordinate clickedCoordinate = resolveClickedCoordinate(screenX, screenY);

        if (!gameSession.getHexMap().containsCoordinate(clickedCoordinate)) {
            return;
        }

        Unit clickedUnit = findUnitAtCoordinate(clickedCoordinate);

        if (clickedUnit != null) {
            selectionState.setSelectedUnit(clickedUnit);
            mapPanel.repaint();
            return;
        }

        attemptMoveSelectedUnit(clickedCoordinate);
    }

    private void handleRightClick(int screenX, int screenY, MouseEvent mouseEvent) {
        HexCoordinate clickedCoordinate = resolveClickedCoordinate(screenX, screenY);

        if (!gameSession.getHexMap().containsCoordinate(clickedCoordinate)) {
            return;
        }

        Unit clickedUnit = findUnitAtCoordinate(clickedCoordinate);
        if (clickedUnit == null) {
            return;
        }

        if (clickedUnit.getUnitType() == UnitType.BUILDER) {
            showConstructionMenu((Builder) clickedUnit, mouseEvent);
        } else if (clickedUnit.getUnitType() == UnitType.WORKER) {
            showStationingMenu((Worker) clickedUnit, mouseEvent);
        }
    }

    private void showConstructionMenu(Builder builder, MouseEvent mouseEvent) {
        Runnable onSuccess = createRepaintRunnable();
        JPopupMenu popupMenu = constructionMenuFactory.buildMenu(gameSession, builder, onSuccess);
        popupMenu.show(mapPanel, mouseEvent.getX(), mouseEvent.getY());
    }

    private void showStationingMenu(Worker worker, MouseEvent mouseEvent) {
        Runnable onSuccess = createRepaintRunnable();
        JPopupMenu popupMenu = stationingMenuFactory.buildMenu(gameSession, worker, onSuccess);
        popupMenu.show(mapPanel, mouseEvent.getX(), mouseEvent.getY());
    }

    private Runnable createRepaintRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                mapPanel.repaint();
            }
        };
    }

    private HexCoordinate resolveClickedCoordinate(int screenX, int screenY) {
        double hexSize = mapPanel.getCurrentHexSize();
        double centerX = mapPanel.getPanelCenterX();
        double centerY = mapPanel.getPanelCenterY();

        double relativeX = screenX - centerX;
        double relativeY = screenY - centerY;

        return HexGeometry.pixelToAxial(relativeX, relativeY, hexSize);
    }

    private void attemptMoveSelectedUnit(HexCoordinate destination) {
        if (!selectionState.hasSelection()) {
            return;
        }
        if (movementAnimator.isAnimating()) {
            return;
        }

        Unit selectedUnit = selectionState.getSelectedUnit();
        List<HexCoordinate> fullPath = pathFinder.findPath(gameSession.getHexMap(), selectedUnit.getCoordinate(), destination);

        if (fullPath == null || fullPath.isEmpty()) {
            return;
        }

        MovementPlan movementPlan = pathFinder.truncateToAffordablePath(
                gameSession.getHexMap(),
                fullPath,
                selectedUnit.getCurrentActionPoints()
        );

        if (movementPlan.getPath().isEmpty()) {
            return;
        }

        selectedUnit.consumeActionPoints(movementPlan.getTotalCost());
        movementAnimator.startMovement(selectedUnit, movementPlan.getPath(), null);
    }

    private Unit findUnitAtCoordinate(HexCoordinate coordinate) {
        List<Unit> units = gameSession.getUnits();

        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (unit.getCoordinate().equals(coordinate)) {
                return unit;
            }
        }

        return null;
    }

    private class MapMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            lastDragX = mouseEvent.getX();
            lastDragY = mouseEvent.getY();
            wasDragged = false;
        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent) {
            int deltaX = mouseEvent.getX() - lastDragX;
            int deltaY = mouseEvent.getY() - lastDragY;

            cameraState.pan(deltaX, deltaY);

            lastDragX = mouseEvent.getX();
            lastDragY = mouseEvent.getY();
            wasDragged = true;

            mapPanel.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            if (wasDragged) {
                return;
            }

            if (mouseEvent.isPopupTrigger() || mouseEvent.getButton() == MouseEvent.BUTTON3) {
                handleRightClick(mouseEvent.getX(), mouseEvent.getY(), mouseEvent);
            } else {
                handleLeftClick(mouseEvent.getX(), mouseEvent.getY());
            }
        }
    }

    private class ZoomListener implements MouseWheelListener {

        @Override
        public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
            if (mouseWheelEvent.getWheelRotation() < 0) {
                cameraState.zoomIn();
            } else {
                cameraState.zoomOut();
            }

            mapPanel.repaint();
        }
    }

    // TODO : resource production is calculated from building.calculateProduction()
    // once per turn inside the turn engine, not here
}