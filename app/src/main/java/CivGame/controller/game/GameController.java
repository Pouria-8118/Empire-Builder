package civgame.controller.game;

import civgame.view.game.CameraState;
import civgame.view.game.GameFrame;
import civgame.view.game.MapPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class GameController {

    private final MapPanel mapPanel;
    private final CameraState cameraState;
    private int lastDragX;
    private int lastDragY;

    public GameController(GameFrame gameFrame) {
        this.mapPanel = gameFrame.getMapPanel();
        this.cameraState = gameFrame.getCameraState();
        registerListeners();
    }

    private void registerListeners() {
        DragListener dragListener = new DragListener();
        mapPanel.addMouseListener(dragListener);
        mapPanel.addMouseMotionListener(dragListener);
        mapPanel.addMouseWheelListener(new ZoomListener());
    }

    private class DragListener extends MouseAdapter {

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

    // TODO : add a click listener here for tile and unit selection
}