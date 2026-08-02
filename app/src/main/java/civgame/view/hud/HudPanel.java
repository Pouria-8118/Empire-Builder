package civgame.view.hud;

import civgame.model.game.GameSession;
import civgame.model.resource.ResourceStorage;
import civgame.model.resource.ResourceType;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;
import civgame.util.Constants;
import civgame.util.IconProvider;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.List;
import java.util.Map;

public class HudPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(20, 20, 30, 210);
    private static final Color BORDER_COLOR = new Color(200, 170, 90);
    private static final Color NEGATIVE_RATE_COLOR = new Color(230, 90, 90);
    private static final Color NEUTRAL_TEXT_COLOR = new Color(235, 235, 235);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 13);
    private static final int RESOURCE_ICON_SIZE = 16;

    private final JLabel turnLabel;
    private final JLabel foodLabel;
    private final JLabel woodLabel;
    private final JLabel stoneLabel;
    private final JLabel ironLabel;
    private final JLabel unitCountLabel;
    private final JLabel queueLabel;

    public HudPanel() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 18, 10));

        turnLabel = createStyledLabel("Turn: 1", null);
        foodLabel = createStyledLabel("Food: 0/0", Constants.FOOD_ICON_PATH);
        woodLabel = createStyledLabel("Wood: 0/0", Constants.WOOD_ICON_PATH);
        stoneLabel = createStyledLabel("Stone: 0/0", Constants.STONE_ICON_PATH);
        ironLabel = createStyledLabel("Iron: 0/0", Constants.IRON_ICON_PATH);
        unitCountLabel = createStyledLabel("Units: 0/0", null);
        queueLabel = createStyledLabel("Queue: Idle", null);

        add(turnLabel);
        add(foodLabel);
        add(woodLabel);
        add(stoneLabel);
        add(ironLabel);
        add(unitCountLabel);
        add(queueLabel);
    }

    private JLabel createStyledLabel(String text, String iconPath) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(NEUTRAL_TEXT_COLOR);

        if (iconPath != null) {
            Image icon = IconProvider.loadImage(iconPath);
            if (icon != null) {
                Image scaledIcon = icon.getScaledInstance(RESOURCE_ICON_SIZE, RESOURCE_ICON_SIZE, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledIcon));
                label.setIconTextGap(6);
            }
        }

        return label;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.setColor(BACKGROUND_COLOR);
        graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

        graphics2D.setColor(BORDER_COLOR);
        graphics2D.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);

        graphics2D.dispose();
        super.paintComponent(graphics);
    }

    public void refresh(GameSession gameSession, Map<ResourceType, Integer> netRates) {
        turnLabel.setText("Turn: " + gameSession.getCurrentTurnNumber());

        updateResourceLabel(foodLabel, "Food", gameSession.getResourceStorage(), ResourceType.FOOD, netRates);
        updateResourceLabel(woodLabel, "Wood", gameSession.getResourceStorage(), ResourceType.WOOD, netRates);
        updateResourceLabel(stoneLabel, "Stone", gameSession.getResourceStorage(), ResourceType.STONE, netRates);
        updateResourceLabel(ironLabel, "Iron", gameSession.getResourceStorage(), ResourceType.IRON, netRates);

        updateUnitCountLabel(gameSession);
        updateQueueLabel(gameSession);
    }

    private void updateResourceLabel(JLabel label, String displayName, ResourceStorage storage, ResourceType resourceType, Map<ResourceType, Integer> netRates) {
        int currentAmount = storage.getAmount(resourceType);
        int capacity = storage.getCapacity(resourceType);
        int netRate = netRates.get(resourceType);

        String rateSign = "+";
        if (netRate < 0) {
            rateSign = "";
        }

        String labelText = displayName + ": " + currentAmount + "/" + capacity + " (" + rateSign + netRate + ")";
        label.setText(labelText);

        if (netRate < 0) {
            label.setForeground(NEGATIVE_RATE_COLOR);
        } else {
            label.setForeground(NEUTRAL_TEXT_COLOR);
        }
    }

    private void updateUnitCountLabel(GameSession gameSession) {
        List<Unit> units = gameSession.getUnits();
        int explorerCount = countUnitsOfType(units, UnitType.EXPLORER);
        int builderCount = countUnitsOfType(units, UnitType.BUILDER);
        int workerCount = countUnitsOfType(units, UnitType.WORKER);
        int borderExpanderCount = countUnitsOfType(units, UnitType.BORDER_EXPANDER);

        String breakdown = "E:" + explorerCount + " B:" + builderCount + " W:" + workerCount + " BE:" + borderExpanderCount;
        unitCountLabel.setText("Units: " + units.size() + "/" + gameSession.getUnitCap() + " (" + breakdown + ")");
    }

    private int countUnitsOfType(List<Unit> units, UnitType unitType) {
        int count = 0;
        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getUnitType() == unitType) {
                count = count + 1;
            }
        }
        return count;
    }

    private void updateQueueLabel(GameSession gameSession) {
        civgame.model.game.ProductionCommand currentCommand = gameSession.getTownHallProductionQueue().getCurrentCommand();
        if (currentCommand == null) {
            queueLabel.setText("Queue: Idle");
        } else {
            queueLabel.setText("Queue: " + currentCommand.describeForHud());
        }
    }
}