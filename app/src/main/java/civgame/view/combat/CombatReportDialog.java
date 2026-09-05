package civgame.view.combat;

import civgame.network.message.combat.CombatResultBroadcast;
import civgame.view.game.StyledButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

public class CombatReportDialog extends JDialog {

    private final JPanel contentPanel;

    public CombatReportDialog(java.awt.Frame owner, CombatResultBroadcast broadcast, boolean isDefender) {
        super(owner, "Combat Report", true);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(20, 20, 30));

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        addTitleSection(broadcast, isDefender);
        contentPanel.add(Box.createVerticalStrut(10));
        addDiceSection(broadcast);
        contentPanel.add(Box.createVerticalStrut(10));
        addLossesSection(broadcast);
        contentPanel.add(Box.createVerticalStrut(10));
        addBuildingDamageSection(broadcast);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(20, 20, 30));
        scrollPane.setPreferredSize(new Dimension(450, 400));
        add(scrollPane, BorderLayout.CENTER);

        add(createButtonPanel(), BorderLayout.SOUTH);

        setSize(500, 500);
        setLocationRelativeTo(owner);
    }

    private void addTitleSection(CombatResultBroadcast broadcast, boolean isDefender) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        
        String title = isDefender ? "You Were Attacked!" : "Attack Report";
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(255, 200, 100));
        titleLabel.setFont(titleLabel.getFont().deriveFont(18.0f));
        panel.add(titleLabel);
        
        contentPanel.add(panel);
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        infoPanel.setOpaque(false);
        
        JLabel attackerLabel = new JLabel("Attacker: " + broadcast.getAttackerUsername());
        attackerLabel.setForeground(new Color(255, 100, 100));
        infoPanel.add(attackerLabel);
        
        JLabel defenderLabel = new JLabel("Defender: " + broadcast.getDefenderUsername());
        defenderLabel.setForeground(new Color(100, 200, 100));
        infoPanel.add(defenderLabel);
        
        contentPanel.add(infoPanel);
    }

    private void addDiceSection(CombatResultBroadcast broadcast) {
        CombatResultBroadcast.CombatResultDTO result = broadcast.getCombatResult();
        if (result == null) {
            return;
        }

        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);
        panel.setBackground(new Color(30, 30, 45));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 80)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel headerLabel = new JLabel("Dice Rolls");
        headerLabel.setForeground(new Color(200, 200, 255));
        headerLabel.setFont(headerLabel.getFont().deriveFont(14.0f));
        panel.add(headerLabel, BorderLayout.NORTH);

        JPanel dicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        dicePanel.setOpaque(false);

        JLabel attackerDiceLabel = new JLabel("Attacker: " + formatDice(result.getAttackerDice()));
        attackerDiceLabel.setForeground(new Color(255, 150, 150));
        dicePanel.add(attackerDiceLabel);

        JLabel defenderDiceLabel = new JLabel("Defender: " + formatDice(result.getDefenderDice()));
        defenderDiceLabel.setForeground(new Color(150, 255, 150));
        dicePanel.add(defenderDiceLabel);

        panel.add(dicePanel, BorderLayout.CENTER);

        JPanel hitsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        hitsPanel.setOpaque(false);

        JLabel attackerHitsLabel = new JLabel("Attacker Hits: " + result.getAttackerHits());
        attackerHitsLabel.setForeground(new Color(255, 100, 100));
        hitsPanel.add(attackerHitsLabel);

        JLabel defenderHitsLabel = new JLabel("Defender Hits: " + result.getDefenderHits());
        defenderHitsLabel.setForeground(new Color(100, 255, 100));
        hitsPanel.add(defenderHitsLabel);

        panel.add(hitsPanel, BorderLayout.SOUTH);

        contentPanel.add(panel);
    }

    private void addLossesSection(CombatResultBroadcast broadcast) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);
        panel.setBackground(new Color(30, 30, 45));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 80)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel headerLabel = new JLabel("Unit Losses");
        headerLabel.setForeground(new Color(200, 200, 255));
        headerLabel.setFont(headerLabel.getFont().deriveFont(14.0f));
        panel.add(headerLabel, BorderLayout.NORTH);

        JPanel lossesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        lossesPanel.setOpaque(false);

        String attackerLossText = formatLosses(broadcast.getAttackerLosses());
        JLabel attackerLossLabel = new JLabel("Attacker lost: " + attackerLossText);
        attackerLossLabel.setForeground(new Color(255, 150, 150));
        lossesPanel.add(attackerLossLabel);

        String defenderLossText = formatLosses(broadcast.getDefenderLosses());
        JLabel defenderLossLabel = new JLabel("Defender lost: " + defenderLossText);
        defenderLossLabel.setForeground(new Color(150, 255, 150));
        lossesPanel.add(defenderLossLabel);

        panel.add(lossesPanel, BorderLayout.CENTER);
        contentPanel.add(panel);
    }

    private void addBuildingDamageSection(CombatResultBroadcast broadcast) {
        if (broadcast.getBuildingDamage() == 0 && !broadcast.isBuildingDestroyed()) {
            return;
        }

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setOpaque(false);
        panel.setBackground(new Color(45, 30, 30));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 100, 100)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        String damageText = "Building Damage: " + broadcast.getBuildingDamage();
        JLabel damageLabel = new JLabel(damageText);
        damageLabel.setForeground(new Color(255, 150, 100));
        panel.add(damageLabel);

        if (broadcast.isBuildingDestroyed()) {
            JLabel destroyedLabel = new JLabel(" (DESTROYED!)");
            destroyedLabel.setForeground(new Color(255, 50, 50));
            destroyedLabel.setFont(destroyedLabel.getFont().deriveFont(java.awt.Font.BOLD));
            panel.add(destroyedLabel);
        }

        contentPanel.add(panel);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        StyledButton closeButton = new StyledButton("Close");
        closeButton.addActionListener(e -> dispose());
        panel.add(closeButton);
        return panel;
    }

    private String formatDice(List<Integer> dice) {
        if (dice == null || dice.isEmpty()) {
            return "None";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < dice.size(); i++) {
            sb.append(dice.get(i));
            if (i < dice.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String formatLosses(List<CombatResultBroadcast.UnitLossDTO> losses) {
        if (losses == null || losses.isEmpty()) {
            return "None";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < losses.size(); i++) {
            sb.append(losses.get(i).getUnitType());
            if (i < losses.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}