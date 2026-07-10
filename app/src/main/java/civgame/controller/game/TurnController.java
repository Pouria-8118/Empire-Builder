package civgame.controller.game;

import civgame.model.game.FogOfWarCalculator;
import civgame.model.game.GameSession;
import civgame.model.game.TurnEngine;
import civgame.model.game.TurnSummary;
import civgame.model.unit.Unit;
import civgame.view.game.EndTurnPanel;
import civgame.view.game.GameFrame;
import civgame.view.game.MapPanel;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TurnController {

    private final GameSession gameSession;
    private final TurnEngine turnEngine;
    private final FogOfWarCalculator fogOfWarCalculator;
    private final EndTurnPanel endTurnPanel;
    private final MapPanel mapPanel;

    public TurnController(GameFrame gameFrame, GameSession gameSession, TurnEngine turnEngine, FogOfWarCalculator fogOfWarCalculator) {
        this.gameSession = gameSession;
        this.turnEngine = turnEngine;
        this.fogOfWarCalculator = fogOfWarCalculator;
        this.endTurnPanel = gameFrame.getEndTurnPanel();
        this.mapPanel = gameFrame.getMapPanel();
        registerListeners();
    }

    private void registerListeners() {
        endTurnPanel.getEndTurnButton().addActionListener(new EndTurnListener());
    }

    private class EndTurnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (hasIdleUnitsWithRemainingActionPoints() && !confirmEndTurnWithIdleUnits()) {
                return;
            }

            TurnSummary turnSummary = turnEngine.executeTurn(gameSession);
            fogOfWarCalculator.updateVisibility(gameSession);

            endTurnPanel.updateTurnNumber(turnSummary.getNewTurnNumber());
            endTurnPanel.setStarvationWarningVisible(turnSummary.isStarvationTriggered());

            showTurnFeedbackIfNeeded(turnSummary);

            mapPanel.repaint();
        }
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

    private void showTurnFeedbackIfNeeded(TurnSummary turnSummary) {
        if (turnSummary.isStarvationTriggered()) {
            JOptionPane.showMessageDialog(
                    mapPanel,
                    "Food storage ran out! Units have lost some Action Points this turn.",
                    "Starvation Crisis",
                    JOptionPane.WARNING_MESSAGE
            );
        }

        if (!turnSummary.getDestroyedBuildingIds().isEmpty()) {
            JOptionPane.showMessageDialog(
                    mapPanel,
                    "Some buildings collapsed due to unpaid upkeep: " + turnSummary.getDestroyedBuildingIds(),
                    "Buildings Collapsed",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    // TODO : once the full HUD exists, move these warnings into
    // a persistent HUD banner instead of a modal popup, per the doc's HUD section
}