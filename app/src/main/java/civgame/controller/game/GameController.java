package civgame.controller.game;

import civgame.model.game.GameSession;
import civgame.model.map.HexCoordinate;
import civgame.model.unit.Unit;
import civgame.util.HexGeometry;
import civgame.view.game.CameraState;
import civgame.view.game.GameFrame;
import civgame.view.game.MapPanel;
import civgame.view.game.SelectionState;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

public class GameController {

    private final MapPanel mapPanel;
    private final CameraState cameraState;
    private final SelectionState selectionState;
    private final GameSession gameSession;
    private int lastDragX;
    private int lastDragY;

    public GameController(GameFrame gameFrame, GameSession gameSession) {
        this.mapPanel = gameFrame.getMapPanel();
        this.cameraState = gameFrame.getCameraState();
        this.selectionState = gameFrame.getSelectionState();
        this.gameSession = gameSession;
        registerListeners();
    }

    private void registerListeners() {
        MapMouseListener mapMouseListener = new MapMouseListener();
        mapPanel.addMouseListener(mapMouseListener);
        mapPanel.addMouseMotionListener(mapMouseListener);
        mapPanel.addMouseWheelListener(new ZoomListener());
    }

    private void selectUnitAtScreenPosition(int screenX, int screenY) {
        double hexSize = mapPanel.getCurrentHexSize();
        double centerX = mapPanel.getPanelCenterX();
        double centerY = mapPanel.getPanelCenterY();

        double relativeX = screenX - centerX;
        double relativeY = screenY - centerY;

        HexCoordinate clickedCoordinate = HexGeometry.pixelToAxial(relativeX, relativeY, hexSize);
        Unit clickedUnit = findUnitAtCoordinate(clickedCoordinate);

        if (clickedUnit != null) {
            selectionState.setSelectedUnit(clickedUnit);
        } else {
            selectionState.clearSelection();
        }

        mapPanel.repaint();
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
        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent) {
            int deltaX = mouseEvent.getX() - lastDragX;
            int deltaY = mouseEvent.getY() - lastDragY;

            cameraState.pan(deltaX, deltaY);

            lastDragX = mouseEvent.getX();
            lastDragY = mouseEvent.getY();

            mapPanel.repaint();
        }

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            selectUnitAtScreenPosition(mouseEvent.getX(), mouseEvent.getY());
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

    // TODO : after selecting a unit, clicking a destination tile should trigger movement
    // TODO : add a construction click-mode for builders placing buildings
}