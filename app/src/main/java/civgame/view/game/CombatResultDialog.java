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
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CombatResultDialog extends JDialog {

    private static final int ROLL_DURATION_MILLISECONDS = 700;
    private static final int ROLL_TICK_MILLISECONDS = 60;

    private final Random random;
    private final List<JLabel> attackerDiceLabels;
    private final List<JLabel> defenderDiceLabels;
    private final CombatResult combatResult;
    private final JLabel summaryLabel;

    public CombatResultDialog(CombatResult combatResult) {
        super((java.awt.Frame) null, "Combat Result", true);
        this.combatResult = combatResult;
        this.random = new Random();
        this.attackerDiceLabels = new ArrayList<JLabel>();
        this.defenderDiceLabels = new ArrayList<JLabel>();

        setLayout(new BorderLayout(10, 10));

        JPanel attackerPanel = buildDicePanel("Attacker", combatResult.getAttackerDice(), attackerDiceLabels);
        JPanel defenderPanel = buildDicePanel("Defender", combatResult.getDefenderDice(), defenderDiceLabels);

        JPanel dicePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        dicePanel.add(attackerPanel);
        dicePanel.add(defenderPanel);

        summaryLabel = new JLabel("Rolling...", SwingConstants.CENTER);
        summaryLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton closeButton = new JButton("Close");
        closeButton.setEnabled(false);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        });

        add(summaryLabel, BorderLayout.NORTH);
        add(dicePanel, BorderLayout.CENTER);
        add(closeButton, BorderLayout.SOUTH);

        setSize(420, 260);
        setLocationRelativeTo(null);

        startRollAnimation(closeButton);
    }

    private JPanel buildDicePanel(String title, List<Integer> finalValues, List<JLabel> labelStore) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(titleLabel);

        for (int i = 0; i < finalValues.size(); i++) {
            JLabel dieLabel = new JLabel("-", SwingConstants.CENTER);
            dieLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
            dieLabel.setOpaque(true);
            dieLabel.setBackground(Color.WHITE);
            dieLabel.setForeground(Color.BLACK);
            labelStore.add(dieLabel);
            panel.add(dieLabel);
        }

        if (finalValues.isEmpty()) {
            JLabel noneLabel = new JLabel("No dice", SwingConstants.CENTER);
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
                    summaryLabel.setText(buildSummaryText());
                    closeButton.setEnabled(true);
                    ((Timer) actionEvent.getSource()).stop();
                    return;
                }

                randomizeLabels(attackerDiceLabels);
                randomizeLabels(defenderDiceLabels);
            }
        });
        timer.start();
    }

    private void randomizeLabels(List<JLabel> labels) {
        for (int i = 0; i < labels.size(); i++) {
            labels.get(i).setText(String.valueOf(1 + random.nextInt(6)));
        }
    }

    private void settleFinalValues() {
        List<Integer> attackerDice = combatResult.getAttackerDice();
        List<Integer> defenderDice = combatResult.getDefenderDice();

        for (int i = 0; i < attackerDiceLabels.size(); i++) {
            attackerDiceLabels.get(i).setText(String.valueOf(attackerDice.get(i)));
        }
        for (int i = 0; i < defenderDiceLabels.size(); i++) {
            defenderDiceLabels.get(i).setText(String.valueOf(defenderDice.get(i)));
        }

        highlightPairs();
    }

    private void highlightPairs() {
        int pairCount = Math.min(attackerDiceLabels.size(), defenderDiceLabels.size());

        for (int i = 0; i < pairCount; i++) {
            int attackerValue = combatResult.getAttackerDice().get(i);
            int defenderValue = combatResult.getDefenderDice().get(i);

            if (attackerValue > defenderValue) {
                attackerDiceLabels.get(i).setBackground(new Color(160, 220, 160));
                defenderDiceLabels.get(i).setBackground(new Color(230, 160, 160));
            } else {
                defenderDiceLabels.get(i).setBackground(new Color(160, 220, 160));
                attackerDiceLabels.get(i).setBackground(new Color(230, 160, 160));
            }
        }
    }

    private String buildSummaryText() {
        return "Attacker hits taken: " + combatResult.getAttackerHits() + "   Defender hits taken: " + combatResult.getDefenderHits();
    }
}