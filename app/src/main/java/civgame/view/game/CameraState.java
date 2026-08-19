package civgame.view.game;

import civgame.model.map.HexCoordinate;
import civgame.util.Constants;
import civgame.util.HexGeometry;

import java.awt.geom.Point2D;

public class CameraState {

    private double offsetX;
    private double offsetY;
    private double currentZoom;

    public CameraState() {
        this.offsetX = 0;
        this.offsetY = 0;
        this.currentZoom = Constants.ZOOM_LEVELS[Constants.DEFAULT_ZOOM_INDEX];
    }

    public void pan(double deltaX, double deltaY) {
        this.offsetX = this.offsetX + deltaX;
        this.offsetY = this.offsetY + deltaY;
    }

    public void zoom(double factor, double mouseX, double mouseY, double panelWidth, double panelHeight) {
        double oldZoom = currentZoom;
        double newZoom = currentZoom * factor;
        newZoom = Math.max(Constants.MIN_ZOOM, Math.min(Constants.MAX_ZOOM, newZoom));

        double halfW = panelWidth / 2.0;
        double halfH = panelHeight / 2.0;

        double baseWorldX = (mouseX - halfW - offsetX) / oldZoom;
        double baseWorldY = (mouseY - halfH - offsetY) / oldZoom;

        currentZoom = newZoom;

        offsetX = mouseX - halfW - baseWorldX * newZoom;
        offsetY = mouseY - halfH - baseWorldY * newZoom;
    }

    public void zoomIn() {
        double oldZoom = currentZoom;
        currentZoom = Math.min(Constants.MAX_ZOOM, currentZoom * 1.2);
        double ratio = currentZoom / oldZoom;
        offsetX = offsetX * ratio;
        offsetY = offsetY * ratio;
    }

    public void zoomOut() {
        double oldZoom = currentZoom;
        currentZoom = Math.max(Constants.MIN_ZOOM, currentZoom / 1.2);
        double ratio = currentZoom / oldZoom;
        offsetX = offsetX * ratio;
        offsetY = offsetY * ratio;
    }

    public double getCurrentZoom() {
        return currentZoom;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void centerOnTile(HexCoordinate coordinate, double hexSize) {
        Point2D pixelPosition = HexGeometry.axialToPixel(coordinate, hexSize);
        this.offsetX = -pixelPosition.getX();
        this.offsetY = -pixelPosition.getY();
    }
}