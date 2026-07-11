package civgame.controller.game;

import civgame.model.game.FogOfWarCalculator;
import civgame.model.game.GameSession;
import civgame.model.game.ResourceRateCalculator;
import civgame.model.game.TurnEngine;
import civgame.model.game.TurnSummary;
import civgame.model.resource.ResourceType;
import civgame.model.unit.Unit;
import civgame.util.Constants;
import civgame.view.game.EndTurnPanel;
import civgame.view.game.GameFrame;
import civgame.view.game.MapPanel;
import civgame.view.hud.HudPanel;
import civgame.view.hud.NotificationEntry;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurnController {

    private final GameSession gameSession;
    private final TurnEngine turnEngine;
    private final FogOfWarCalculator fogOfWarCalculator;
    private final ResourceRateCalculator resourceRateCalculator;
    private final EndTurnPanel endTurnPanel;
    private final HudPanel hudPanel;
    private final GameFrame gameFrame;
    private final MapPanel mapPanel;

    public TurnController(GameFrame gameFrame, GameSession gameSession, TurnEngine turnEngine, FogOfWarCalculator fogOfWarCalculator) {
        this.gameFrame = gameFrame;
        this.gameSession = gameSession;
        this.turnEngine = turnEngine;
        this.fogOfWarCalculator = fogOfWarCalculator;
        this.resourceRateCalculator = new ResourceRateCalculator();
        this.endTurnPanel = gameFrame.getEndTurnPanel();
        this.hudPanel = gameFrame.getHudPanel();
        this.mapPanel = gameFrame.getMapPanel();
        registerListeners();
        refreshHud();
    }

    private void registerListeners() {
        endTurnPanel.getEndTurnButton().addActionListener(new EndTurnListener());
    }

    private void refreshHud() {
        Map<ResourceType, Integer> netRates = resourceRateCalculator.calculateNetRates(gameSession);
        hudPanel.refresh(gameSession, netRates);
    }

    private class EndTurnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (hasIdleUnitsWithRemainingActionPoints() && !confirmEndTurnWithIdleUnits()) {
                return;
            }

            Map<ResourceType, Integer> amountsBeforeTurn = captureResourceAmounts();

            TurnSummary turnSummary = turnEngine.executeTurn(gameSession);
            fogOfWarCalculator.updateVisibility(gameSession);

            refreshHud();
            endTurnPanel.setStarvationWarningVisible(turnSummary.isStarvationTriggered());

            showTurnNotification(turnSummary, amountsBeforeTurn);

            mapPanel.repaint();
        }
    }

    private Map<ResourceType, Integer> captureResourceAmounts() {
        Map<ResourceType, Integer> amounts = new HashMap<ResourceType, Integer>();
        amounts.put(ResourceType.FOOD, gameSession.getResourceStorage().getAmount(ResourceType.FOOD));
        amounts.put(ResourceType.WOOD, gameSession.getResourceStorage().getAmount(ResourceType.WOOD));
        amounts.put(ResourceType.STONE, gameSession.getResourceStorage().getAmount(ResourceType.STONE));
        amounts.put(ResourceType.IRON, gameSession.getResourceStorage().getAmount(ResourceType.IRON));
        return amounts;
    }

    private void showTurnNotification(TurnSummary turnSummary, Map<ResourceType, Integer> amountsBeforeTurn) {
        List<NotificationEntry> entries = new ArrayList<NotificationEntry>();

        addResourceDeltaEntry(entries, ResourceType.FOOD, "Food", Constants.FOOD_ICON_PATH, amountsBeforeTurn);
        addResourceDeltaEntry(entries, ResourceType.WOOD, "Wood", Constants.WOOD_ICON_PATH, amountsBeforeTurn);
        addResourceDeltaEntry(entries, ResourceType.STONE, "Stone", Constants.STONE_ICON_PATH, amountsBeforeTurn);
        addResourceDeltaEntry(entries, ResourceType.IRON, "Iron", Constants.IRON_ICON_PATH, amountsBeforeTurn);

        if (turnSummary.getCompletedProductionDescription() != null) {
            entries.add(new NotificationEntry(null, turnSummary.getCompletedProductionDescription(), null, true));
        }

        if (turnSummary.isStarvationTriggered()) {
            entries.add(new NotificationEntry(null, "Starvation! Units lost Action Points", null, false));
        }

        if (!turnSummary.getDestroyedBuildingIds().isEmpty()) {
            entries.add(new NotificationEntry(null, "A building collapsed from unpaid upkeep", null, false));
        }

        if (entries.isEmpty()) {
            entries.add(new NotificationEntry(null, "No changes this turn", null, true));
        }

        gameFrame.showTurnNotification("Turn " + turnSummary.getNewTurnNumber(), entries);
    }

    private void addResourceDeltaEntry(List<NotificationEntry> entries, ResourceType resourceType, String displayName, String iconPath, Map<ResourceType, Integer> amountsBeforeTurn) {
        int amountBefore = amountsBeforeTurn.get(resourceType);
        int amountAfter = gameSession.getResourceStorage().getAmount(resourceType);
        int delta = amountAfter - amountBefore;

        if (delta == 0) {
            return;
        }

        String deltaText;
        if (delta > 0) {
            deltaText = "+" + delta;
        } else {
            deltaText = String.valueOf(delta);
        }

        entries.add(new NotificationEntry(iconPath, displayName, deltaText, delta > 0));
    }

    private boolean hasIdleUnitsWithRemainingActionPoints() {
        List<Unit> units = gameSession.getUnits();

        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).isIdleWithRemainingActionPoints()) {
                return true;
            }
        }

        return false;
    }

    private boolean confirmEndTurnWithIdleUnits() {
        int userChoice = JOptionPane.showConfirmDialog(
                mapPanel,
                "Some units still have unused Action Points. End turn anyway?",
                "Unused Action Points",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        return userChoice == JOptionPane.YES_OPTION;
    }
}