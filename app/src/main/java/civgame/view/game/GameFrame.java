package civgame.view.game;

import civgame.model.game.GameSession;
import civgame.util.Constants;

import javax.swing.JFrame;
import java.awt.BorderLayout;

public class GameFrame extends JFrame {

    private final MapPanel mapPanel;
    private final CameraState cameraState;
    private final SelectionState selectionState;
    private final UnitMovementAnimator movementAnimator;
    private final EndTurnPanel endTurnPanel;

    public GameFrame(GameSession gameSession) {
        super(Constants.WINDOW_TITLE + " - Game");
        this.cameraState = new CameraState();
        this.selectionState = new SelectionState();
        this.movementAnimator = new UnitMovementAnimator();
        this.mapPanel = new MapPanel(gameSession, cameraState, selectionState, movementAnimator);
        this.endTurnPanel = new EndTurnPanel();
        this.movementAnimator.setRepaintCallback(createRepaintCallback());
        setupFrame();
    }

    private Runnable createRepaintCallback() {
        return new Runnable() {
            @Override
            public void run() {
                mapPanel.repaint();
            }
        };
    }

    private void setupFrame() {
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        add(mapPanel, BorderLayout.CENTER);
        add(endTurnPanel, BorderLayout.SOUTH);
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

    public UnitMovementAnimator getMovementAnimator() {
        return movementAnimator;
    }

    public EndTurnPanel getEndTurnPanel() {
        return endTurnPanel;
    }

    // TODO : host the full HUD panel here (resources/units/production queue),
    // replacing or wrapping this temporary EndTurnPanel
}