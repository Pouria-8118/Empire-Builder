package civgame.view.game;

import civgame.model.game.GameSession;
import civgame.util.Constants;

import javax.swing.JFrame;

public class GameFrame extends JFrame {

    private final MapPanel mapPanel;
    private final CameraState cameraState;
    private final SelectionState selectionState;

    public GameFrame(GameSession gameSession) {
        super(Constants.WINDOW_TITLE + " - Game");
        this.cameraState = new CameraState();
        this.selectionState = new SelectionState();
        this.mapPanel = new MapPanel(gameSession, cameraState, selectionState);
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

    public SelectionState getSelectionState() {
        return selectionState;
    }

    // TODO : host the HUD panel alongside the map panel here
}