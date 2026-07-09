package civgame.view.game;

import civgame.util.Constants;

public class CameraState {

    private double offsetX;
    private double offsetY;
    private int zoomIndex;

    public CameraState() {
        this.offsetX = 0;
        this.offsetY = 0;
        this.zoomIndex = Constants.DEFAULT_ZOOM_INDEX;
    }

    public void pan(double deltaX, double deltaY) {
        this.offsetX = this.offsetX + deltaX;
        this.offsetY = this.offsetY + deltaY;
    }

    public void zoomIn() {
        if (zoomIndex < Constants.ZOOM_LEVELS.length - 1) {
            zoomIndex = zoomIndex + 1;
        }
    }

    public void zoomOut() {
        if (zoomIndex > 0) {
            zoomIndex = zoomIndex - 1;
        }
    }

    public double getCurrentZoom() {
        return Constants.ZOOM_LEVELS[zoomIndex];
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    // TODO : add keyboard-based panning if needed later
}