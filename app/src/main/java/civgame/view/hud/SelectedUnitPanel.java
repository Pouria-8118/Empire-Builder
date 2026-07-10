package civgame.view.hud;

import civgame.model.unit.Builder;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class SelectedUnitPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(20, 20, 30, 210);
    private static final Color BORDER_COLOR = new Color(200, 170, 90);
    private static final Color TEXT_COLOR = new Color(235, 235, 235);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 13);

    private final JLabel infoLabel;

    public SelectedUnitPanel() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 14, 10));

        infoLabel = new JLabel("No unit selected");
        infoLabel.setFont(LABEL_FONT);
        infoLabel.setForeground(TEXT_COLOR);
        add(infoLabel);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.setColor(BACKGROUND_COLOR);
        graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        graphics2D.setColor(BORDER_COLOR);
        graphics2D.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

        graphics2D.dispose();
        super.paintComponent(graphics);
    }

    public void showUnit(Unit unit) {
        String apText = unit.getCurrentActionPoints() + "/" + unit.getMaxActionPoints();
        String text = unit.getUnitType().name() + " | AP: " + apText;

        if (unit.getUnitType() == UnitType.BUILDER) {
            Builder builder = (Builder) unit;
            text = text + " | Charges: " + builder.getRemainingCharges();
        }

        infoLabel.setText(text);
    }

    public void showNothingSelected() {
        infoLabel.setText("No unit selected");
    }

    // TODO : show extra info once Border Expander has an in-game action
}