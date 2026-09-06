package civgame.controller.game;

import civgame.client.GameClient;
import civgame.model.game.GameSession;
import civgame.model.resource.ResourceStorage;
import civgame.model.resource.ResourceType;
import civgame.model.unit.Unit;
import civgame.network.NetworkException;
import civgame.network.message.combat.CombatResultBroadcast;
import civgame.network.message.game.EndTurnRequest;
import civgame.network.message.game.EndTurnResponse;
import civgame.network.message.game.TurnUpdateBroadcast;
import civgame.util.Constants;
import civgame.view.combat.CombatReportDialog;
import civgame.view.game.EndTurnPanel;
import civgame.view.game.GameFrame;
import civgame.view.game.MapPanel;
import civgame.view.hud.NotificationEntry;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MultiplayerTurnController {

    private final GameSession gameSession;
    private final GameClient gameClient;
    private final String myClientId;
    private final EndTurnPanel endTurnPanel;
    private final GameFrame gameFrame;
    private final MapPanel mapPanel;
    private String currentTurnClientId;
    private List<CombatResultBroadcast> pendingCombatReports;

    public MultiplayerTurnController(GameFrame gameFrame,
                                     GameSession gameSession,
                                     GameClient gameClient,
                                     String myClientId,
                                     String initialTurnClientId) {
        this.gameFrame = gameFrame;
        this.gameSession = gameSession;
        this.gameClient = gameClient;
        this.myClientId = myClientId;
        this.endTurnPanel = gameFrame.getEndTurnPanel();
        this.mapPanel = gameFrame.getMapPanel();
        this.currentTurnClientId = initialTurnClientId;
        this.pendingCombatReports = new ArrayList<>();
        registerListeners();
        updateEndTurnButtonState();
        gameFrame.refreshHud(gameSession);
    }

    private void registerListeners() {
        endTurnPanel.getEndTurnButton().addActionListener(new EndTurnListener());
    }

    public void updateCurrentTurn(String newTurnClientId, String newTurnUsername) {
        this.currentTurnClientId = newTurnClientId;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateEndTurnButtonState();
            }
        });
    }

    private void updateEndTurnButtonState() {
        boolean isMyTurn = myClientId.equals(currentTurnClientId);
        endTurnPanel.getEndTurnButton().setEnabled(isMyTurn);
        if (isMyTurn) {
            gameFrame.getHudPanel().setTurnIndicatorText("Your Turn!");
        } else {
            gameFrame.getHudPanel().setTurnIndicatorText("Waiting for other players...");
        }
    }

    private class EndTurnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (!myClientId.equals(currentTurnClientId)) {
                return;
            }
            if (hasIdleUnitsWithRemainingActionPoints() && !confirmEndTurnWithIdleUnits()) {
                return;
            }
            sendEndTurnRequest();
        }
    }

    private void sendEndTurnRequest() {
        EndTurnRequest request = new EndTurnRequest(myClientId);
        try {
            gameClient.sendMessageObject(request);
        } catch (NetworkException e) {
            JOptionPane.showMessageDialog(
                    gameFrame,
                    "Failed to end turn: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void handleEndTurnResponse(EndTurnResponse response) {
        if (!response.isSuccess()) {
            JOptionPane.showMessageDialog(
                    gameFrame,
                    response.getReason(),
                    "End Turn Failed",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    public void handleTurnUpdate(TurnUpdateBroadcast broadcast) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                processTurnUpdate(broadcast);
                showPendingCombatReports();
            }
        });
    }

    private void processTurnUpdate(TurnUpdateBroadcast broadcast) {
        currentTurnClientId = broadcast.getCurrentTurnClientId();
        applyResourceSnapshot(broadcast.getResources());
        gameSession.incrementTurnNumber();
        
        if (myClientId.equals(currentTurnClientId)) {
            refreshMyUnitActionPoints();
        }
        
        updateEndTurnButtonState();
        endTurnPanel.setStarvationWarningVisible(
                broadcast.getSummary() != null && broadcast.getSummary().isStarvationTriggered()
        );
        showTurnNotification(broadcast);
        gameFrame.refreshHud(gameSession);
        mapPanel.repaint();
    }

    private void refreshMyUnitActionPoints() {
        List<Unit> units = gameSession.getUnits();
        for (int i = 0; i < units.size(); i++) {
            units.get(i).refreshActionPoints();
        }
    }

    private void applyResourceSnapshot(TurnUpdateBroadcast.ResourceSnapshotDTO resources) {
        if (resources == null) {
            return;
        }
        ResourceStorage storage = gameSession.getResourceStorage();
        storage.setAmount(ResourceType.FOOD, resources.getFood());
        storage.setAmount(ResourceType.WOOD, resources.getWood());
        storage.setAmount(ResourceType.STONE, resources.getStone());
        storage.setAmount(ResourceType.IRON, resources.getIron());
    }

    private void showTurnNotification(TurnUpdateBroadcast broadcast) {
        List<NotificationEntry> entries = new ArrayList<>();

        TurnUpdateBroadcast.ResourceSnapshotDTO resourcesBefore = broadcast.getResourcesBeforeTurn();
        TurnUpdateBroadcast.ResourceSnapshotDTO resourcesAfter = broadcast.getResources();

        if (resourcesBefore != null && resourcesAfter != null) {
            addResourceDeltaEntry(entries, ResourceType.FOOD, "Food", Constants.FOOD_ICON_PATH,
                    resourcesBefore.getFood(), resourcesAfter.getFood());
            addResourceDeltaEntry(entries, ResourceType.WOOD, "Wood", Constants.WOOD_ICON_PATH,
                    resourcesBefore.getWood(), resourcesAfter.getWood());
            addResourceDeltaEntry(entries, ResourceType.STONE, "Stone", Constants.STONE_ICON_PATH,
                    resourcesBefore.getStone(), resourcesAfter.getStone());
            addResourceDeltaEntry(entries, ResourceType.IRON, "Iron", Constants.IRON_ICON_PATH,
                    resourcesBefore.getIron(), resourcesAfter.getIron());
        }

        TurnUpdateBroadcast.TurnSummaryDTO summary = broadcast.getSummary();
        if (summary != null) {
            if (summary.getCompletedProductionDescription() != null) {
                entries.add(new NotificationEntry(null, summary.getCompletedProductionDescription(), null, true));
            }
            if (summary.isStarvationTriggered()) {
                entries.add(new NotificationEntry(null, "Starvation! Units lost Action Points", null, false));
            }
            if (!summary.getDestroyedBuildingIds().isEmpty()) {
                entries.add(new NotificationEntry(null, "A building collapsed from unpaid upkeep", null, false));
            }
            List<String> depleted = summary.getDepletedResourceDescriptions();
            for (int i = 0; i < depleted.size(); i++) {
                entries.add(new NotificationEntry(null, depleted.get(i), null, false));
            }
        }

        if (entries.isEmpty()) {
            entries.add(new NotificationEntry(null, "No changes this turn", null, true));
        }
        gameFrame.showTurnNotification("Turn " + broadcast.getNewTurnNumber(), entries);
    }

    private void addResourceDeltaEntry(List<NotificationEntry> entries, ResourceType resourceType,
                                       String displayName, String iconPath,
                                       int amountBefore, int amountAfter) {
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

    public void setTurnIndicator(String text) {
        gameFrame.getHudPanel().setTurnIndicatorText(text);
    }

    public void handleCombatReport(CombatResultBroadcast broadcast) {
        boolean isDefender = myClientId.equals(broadcast.getDefenderClientId());
        if (myClientId.equals(currentTurnClientId)) {
            showCombatReportDialog(broadcast, isDefender);
        } else {
            pendingCombatReports.add(broadcast);
        }
    }

    private void showPendingCombatReports() {
        for (int i = 0; i < pendingCombatReports.size(); i++) {
            CombatResultBroadcast report = pendingCombatReports.get(i);
            boolean isDefender = myClientId.equals(report.getDefenderClientId());
            showCombatReportDialog(report, isDefender);
        }
        pendingCombatReports.clear();
    }

    private void showCombatReportDialog(CombatResultBroadcast broadcast, boolean isDefender) {
        CombatReportDialog dialog = new CombatReportDialog(gameFrame, broadcast, isDefender);
        dialog.setVisible(true);
    }
}