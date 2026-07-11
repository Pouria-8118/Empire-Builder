package civgame.view.game;

import civgame.model.game.GameSession;
import civgame.util.Constants;
import civgame.view.hud.HudPanel;
import civgame.view.hud.SelectedUnitPanel;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import java.awt.Dimension;
import civgame.view.hud.ControlsHintPanel;
public class GameFrame extends JFrame {

    private static final int HUD_WIDTH = 1000;
    private static final int HUD_HEIGHT = 60;
    private static final int BOTTOM_PANEL_HEIGHT = 55;
    private static final int SELECTED_UNIT_PANEL_WIDTH = 340;
    private static final int END_TURN_PANEL_WIDTH = 260;
    private static final int MARGIN = 15;

    private final JLayeredPane layeredPane;
    private final MapPanel mapPanel;
    private final CameraState cameraState;
    private final SelectionState selectionState;
    private final UnitMovementAnimator movementAnimator;
    private final EndTurnPanel endTurnPanel;
    private final HudPanel hudPanel;
    private final SelectedUnitPanel selectedUnitPanel;
    private final ControlsHintPanel controlsHintPanel;


    public GameFrame(GameSession gameSession) {
        super(Constants.WINDOW_TITLE + " - Game");

        this.cameraState = new CameraState();
        this.selectionState = new SelectionState();
        this.movementAnimator = new UnitMovementAnimator();
        this.mapPanel = new MapPanel(gameSession, cameraState, selectionState, movementAnimator);
        this.hudPanel = new HudPanel();
        this.selectedUnitPanel = new SelectedUnitPanel();
        this.controlsHintPanel = new ControlsHintPanel();
        this.endTurnPanel = new EndTurnPanel();
        this.layeredPane = new JLayeredPane();

        this.movementAnimator.setRepaintCallback(createRepaintCallback());

        setupFrame();
        setupLayeredPane();
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
    }

    private void setupLayeredPane() {
        layeredPane.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));

        mapPanel.setBounds(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        layeredPane.add(mapPanel, JLayeredPane.DEFAULT_LAYER);

        int hudX = (Constants.WINDOW_WIDTH - HUD_WIDTH) / 2;
        hudPanel.setBounds(hudX, MARGIN, HUD_WIDTH, HUD_HEIGHT);
        layeredPane.add(hudPanel, JLayeredPane.PALETTE_LAYER);

        controlsHintPanel.setBounds(MARGIN, MARGIN, 620, 34);
        layeredPane.add(controlsHintPanel, JLayeredPane.PALETTE_LAYER);

        int bottomY = Constants.WINDOW_HEIGHT - BOTTOM_PANEL_HEIGHT - MARGIN;

        selectedUnitPanel.setBounds(MARGIN, bottomY, SELECTED_UNIT_PANEL_WIDTH, BOTTOM_PANEL_HEIGHT);
        layeredPane.add(selectedUnitPanel, JLayeredPane.PALETTE_LAYER);

        int endTurnX = Constants.WINDOW_WIDTH - END_TURN_PANEL_WIDTH - MARGIN;
        endTurnPanel.setBounds(endTurnX, bottomY, END_TURN_PANEL_WIDTH, BOTTOM_PANEL_HEIGHT);
        layeredPane.add(endTurnPanel, JLayeredPane.PALETTE_LAYER);

        setContentPane(layeredPane);
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

    public HudPanel getHudPanel() {
        return hudPanel;
    }

    public SelectedUnitPanel getSelectedUnitPanel() {
        return selectedUnitPanel;
    }

    // TODO : add a Town Hall upgrade menu button somewhere on this layered pane
}