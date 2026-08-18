package civgame.view.game;

import civgame.model.game.CombatResult;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CombatResultDialog extends JDialog {

    private static final int ROLL_DURATION_MILLISECONDS = 900;
    private static final int ROLL_TICK_MILLISECONDS = 60;

    private final Random random;
    private final List<DiceFacePanel> attackerDicePanels;
    private final List<DiceFacePanel> defenderDicePanels;
    private final CombatResult combatResult;
    private final JLabel summaryLabel;
    private final JLabel winnerLabel;

    public CombatResultDialog(CombatResult combatResult) {
        super((java.awt.Frame) null, "Combat Result", true);
        this.combatResult = combatResult;
        this.random = new Random();
        this.attackerDicePanels = new ArrayList<DiceFacePanel>();
        this.defenderDicePanels = new ArrayList<DiceFacePanel>();

        setLayout(new BorderLayout(10, 10));

        JPanel attackerPanel = buildDicePanel("Attacker", combatResult.getAttackerDice(), attackerDicePanels);
        JPanel defenderPanel = buildDicePanel("Defender", combatResult.getDefenderDice(), defenderDicePanels);

        JPanel dicePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        dicePanel.add(attackerPanel);
        dicePanel.add(defenderPanel);

        winnerLabel = new JLabel("Rolling the dice...", SwingConstants.CENTER);
        winnerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        summaryLabel = new JLabel(" ", SwingConstants.CENTER);
        summaryLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(winnerLabel, BorderLayout.NORTH);
        topPanel.add(summaryLabel, BorderLayout.SOUTH);

        JButton closeButton = new JButton("Close");
        closeButton.setEnabled(false);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        });

        add(topPanel, BorderLayout.NORTH);
        add(dicePanel, BorderLayout.CENTER);
        add(closeButton, BorderLayout.SOUTH);

        setSize(460, 300);
        setLocationRelativeTo(null);

        startRollAnimation(closeButton);
    }

    private JPanel buildDicePanel(String title, List<Integer> finalValues, List<DiceFacePanel> panelStore) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setAlignmentX(0.5f);
        panel.add(titleLabel);

        JPanel diceRow = new JPanel();
        for (int i = 0; i < finalValues.size(); i++) {
            DiceFacePanel diceFace = new DiceFacePanel();
            panelStore.add(diceFace);
            diceRow.add(diceFace);
        }
        diceRow.setAlignmentX(0.5f);
        panel.add(diceRow);

        if (finalValues.isEmpty()) {
            JLabel noneLabel = new JLabel("No dice", SwingConstants.CENTER);
            noneLabel.setAlignmentX(0.5f);
            panel.add(noneLabel);
        }

        return panel;
    }

    private void startRollAnimation(JButton closeButton) {
        long startTime = System.currentTimeMillis();

        Timer timer = new Timer(ROLL_TICK_MILLISECONDS, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                long elapsed = System.currentTimeMillis() - startTime;

                if (elapsed >= ROLL_DURATION_MILLISECONDS) {
                    settleFinalValues();
                    winnerLabel.setText(buildWinnerText());
                    summaryLabel.setText(buildSummaryText());
                    closeButton.setEnabled(true);
                    ((Timer) actionEvent.getSource()).stop();
                    return;
                }

                randomizePanels(attackerDicePanels);
                randomizePanels(defenderDicePanels);
            }
        });
        timer.start();
    }

    private void randomizePanels(List<DiceFacePanel> panels) {
        for (int i = 0; i < panels.size(); i++) {
            panels.get(i).setValue(1 + random.nextInt(6));
        }
    }

    private void settleFinalValues() {
        List<Integer> attackerDice = combatResult.getAttackerDice();
        List<Integer> defenderDice = combatResult.getDefenderDice();

        for (int i = 0; i < attackerDicePanels.size(); i++) {
            attackerDicePanels.get(i).setValue(attackerDice.get(i));
        }
        for (int i = 0; i < defenderDicePanels.size(); i++) {
            defenderDicePanels.get(i).setValue(defenderDice.get(i));
        }

        highlightPairs();
    }

    private void highlightPairs() {
        int pairCount = Math.min(attackerDicePanels.size(), defenderDicePanels.size());

        for (int i = 0; i < pairCount; i++) {
            int attackerValue = combatResult.getAttackerDice().get(i);
            int defenderValue = combatResult.getDefenderDice().get(i);

            if (attackerValue > defenderValue) {
                attackerDicePanels.get(i).setHighlight(true, false);
                defenderDicePanels.get(i).setHighlight(false, true);
            } else {
                defenderDicePanels.get(i).setHighlight(true, false);
                attackerDicePanels.get(i).setHighlight(false, true);
            }
        }
    }

    private String buildWinnerText() {
        int attackerHits = combatResult.getAttackerHits();
        int defenderHits = combatResult.getDefenderHits();

        if (defenderHits > attackerHits) {
            return "Attacker comes out ahead!";
        }
        if (attackerHits > defenderHits) {
            return "Defender holds the line!";
        }
        return "Both sides take losses.";
    }

    private String buildSummaryText() {
        return "Attacker losses: " + combatResult.getAttackerHits() + "   Defender losses: " + combatResult.getDefenderHits();
    }
}