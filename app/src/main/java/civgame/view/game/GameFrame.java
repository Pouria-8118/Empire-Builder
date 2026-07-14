package civgame.view.game;

import civgame.model.game.GameSession;
import civgame.model.game.ResourceRateCalculator;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;
import civgame.util.FullScreenToggler;
import civgame.view.hud.ControlsHintPanel;
import civgame.view.hud.HudPanel;
import civgame.view.hud.MinimapPanel;
import civgame.view.hud.NotificationEntry;
import civgame.view.hud.SelectedUnitPanel;
import civgame.view.hud.TurnNotificationPanel;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

public class GameFrame extends JFrame {

    private static final int HUD_MAX_WIDTH = 1000;
    private static final int HUD_HEIGHT = 60;
    private static final int CONTROLS_HINT_HEIGHT = 30;
    private static final int CONTROLS_HINT_GAP = 8;
    private static final int CONTROLS_HINT_MAX_WIDTH = 700;
    private static final int BOTTOM_PANEL_HEIGHT = 55;
    private static final int SELECTED_UNIT_PANEL_WIDTH = 340;
    private static final int END_TURN_PANEL_WIDTH = 260;
    private static final int MINIMAP_WIDTH = 230;
    private static final int MINIMAP_HEIGHT = 170;
    private static final int MARGIN = 15;
    private static final int MINIMUM_CENTERED_AREA_WIDTH = 300;

    private final JLayeredPane layeredPane;
    private final MapPanel mapPanel;
    private final CameraState cameraState;
    private final SelectionState selectionState;
    private final UnitMovementAnimator movementAnimator;
    private final EndTurnPanel endTurnPanel;
    private final HudPanel hudPanel;
    private final SelectedUnitPanel selectedUnitPanel;
    private final ControlsHintPanel controlsHintPanel;
    private final TurnNotificationPanel turnNotificationPanel;
    private final MinimapPanel minimapPanel;
    private final FullScreenToggler fullScreenToggler;
    private final ResourceRateCalculator resourceRateCalculator;

    public GameFrame(GameSession gameSession) {
        super(Constants.WINDOW_TITLE + " - Game");

        this.cameraState = new CameraState();
        this.selectionState = new SelectionState();
        this.movementAnimator = new UnitMovementAnimator();
        this.mapPanel = new MapPanel(gameSession, cameraState, selectionState, movementAnimator);
        this.hudPanel = new HudPanel();
        this.selectedUnitPanel = new SelectedUnitPanel();
        this.controlsHintPanel = new ControlsHintPanel();
        this.turnNotificationPanel = new TurnNotificationPanel();
        this.endTurnPanel = new EndTurnPanel();
        this.minimapPanel = new MinimapPanel(gameSession, cameraState);
        this.layeredPane = new JLayeredPane();
        this.fullScreenToggler = new FullScreenToggler(this);
        this.resourceRateCalculator = new ResourceRateCalculator();

        this.movementAnimator.setRepaintCallback(createRepaintCallback());
        this.minimapPanel.setMapRepaintCallback(createRepaintCallback());

        setupFrame();
        setupLayeredPane();
        registerResizeListener();
        registerFullScreenKeyBinding();
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
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void setupLayeredPane() {
        layeredPane.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));

        layeredPane.add(mapPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(hudPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(controlsHintPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(selectedUnitPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(endTurnPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(minimapPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(turnNotificationPanel, JLayeredPane.DRAG_LAYER);

        setContentPane(layeredPane);
        relayoutComponents();
    }

    private void registerResizeListener() {
        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                relayoutComponents();
            }
        });
    }

    private void registerFullScreenKeyBinding() {
        InputMap inputMap = layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = layeredPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "toggleFullScreen");
        actionMap.put("toggleFullScreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fullScreenToggler.toggle();
            }
        });
    }

    private void relayoutComponents() {
        int frameWidth = layeredPane.getWidth();
        int frameHeight = layeredPane.getHeight();

        if (frameWidth <= 0 || frameHeight <= 0) {
            return;
        }

        mapPanel.setBounds(0, 0, frameWidth, frameHeight);

        int minimapX = frameWidth - MINIMAP_WIDTH - MARGIN;
        minimapPanel.setBounds(minimapX, MARGIN, MINIMAP_WIDTH, MINIMAP_HEIGHT);

        int reservedRightWidth = MINIMAP_WIDTH + MARGIN * 2;
        int centeredAreaWidth = Math.max(MINIMUM_CENTERED_AREA_WIDTH, frameWidth - reservedRightWidth - MARGIN);

        int hudWidth = Math.min(HUD_MAX_WIDTH, centeredAreaWidth);
        int hudX = MARGIN + (centeredAreaWidth - hudWidth) / 2;
        hudPanel.setBounds(hudX, MARGIN, hudWidth, HUD_HEIGHT);

        int hintWidth = Math.min(CONTROLS_HINT_MAX_WIDTH, centeredAreaWidth);
        int hintX = MARGIN + (centeredAreaWidth - hintWidth) / 2;
        int hintY = MARGIN + HUD_HEIGHT + CONTROLS_HINT_GAP;
        controlsHintPanel.setBounds(hintX, hintY, hintWidth, CONTROLS_HINT_HEIGHT);

        int bottomY = frameHeight - BOTTOM_PANEL_HEIGHT - MARGIN;
        selectedUnitPanel.setBounds(MARGIN, bottomY, SELECTED_UNIT_PANEL_WIDTH, BOTTOM_PANEL_HEIGHT);

        int endTurnX = frameWidth - END_TURN_PANEL_WIDTH - MARGIN;
        endTurnPanel.setBounds(endTurnX, bottomY, END_TURN_PANEL_WIDTH, BOTTOM_PANEL_HEIGHT);

        if (turnNotificationPanel.isVisible()) {
            centerNotificationPanel();
        }
    }

    public void showTurnNotification(String title, List<NotificationEntry> entries) {
        turnNotificationPanel.showNotification(title, entries);
        turnNotificationPanel.setSize(turnNotificationPanel.getPreferredSize());
        centerNotificationPanel();
    }

    private void centerNotificationPanel() {
        int width = Math.max(320, turnNotificationPanel.getPreferredSize().width);
        int height = turnNotificationPanel.getPreferredSize().height;
        int x = (layeredPane.getWidth() - width) / 2;
        int y = (layeredPane.getHeight() - height) / 2 - 80;

        turnNotificationPanel.setBounds(x, y, width, height);
    }

    public void refreshHud(GameSession gameSession) {
        Map<ResourceType, Integer> netRates = resourceRateCalculator.calculateNetRates(gameSession);
        hudPanel.refresh(gameSession, netRates);
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
}