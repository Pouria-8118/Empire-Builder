package civgame.util;

import javax.swing.JFrame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class FullScreenToggler {

    private final JFrame frame;
    private boolean fullScreenActive;

    public FullScreenToggler(JFrame frame) {
        this.frame = frame;
        this.fullScreenActive = false;
    }

    public void toggle() {
        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        if (fullScreenActive) {
            graphicsDevice.setFullScreenWindow(null);
            fullScreenActive = false;
        } else {
            graphicsDevice.setFullScreenWindow(frame);
            fullScreenActive = true;
        }
    }

    public boolean isFullScreenActive() {
        return fullScreenActive;
    }
}