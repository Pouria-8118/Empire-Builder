package civgame.view.game;

import civgame.model.map.HexMap;
import civgame.util.Constants;

import javax.swing.JFrame;

public class GameFrame extends JFrame {

    private final MapPanel mapPanel;
    private final CameraState cameraState;

    public GameFrame(HexMap hexMap) {
        super(Constants.WINDOW_TITLE + " - Game");
        this.cameraState = new CameraState();
        this.mapPanel = new MapPanel(hexMap, cameraState);
        setupFrame();
    }

    private void setupFrame() {
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        add(mapPanel);
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public CameraState getCameraState() {
        return cameraState;
    }

    // TODO : host the HUD panel alongside the map panel here
}