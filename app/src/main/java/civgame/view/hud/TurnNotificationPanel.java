package civgame.view.hud;

import civgame.util.IconProvider;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TurnNotificationPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(20, 20, 30, 235);
    private static final Color BORDER_COLOR = new Color(200, 170, 90);
    private static final Color POSITIVE_COLOR = new Color(110, 220, 120);
    private static final Color NEGATIVE_COLOR = new Color(230, 90, 90);
    private static final Color TEXT_COLOR = new Color(235, 235, 235);
    private static final int AUTO_HIDE_DELAY_MILLISECONDS = 3200;
    private static final int ICON_SIZE = 22;

    private final JLabel titleLabel;
    private final JPanel entriesPanel;
    private Timer autoHideTimer;

    public TurnNotificationPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));

        titleLabel = new JLabel("Turn");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        entriesPanel = new JPanel();
        entriesPanel.setOpaque(false);
        entriesPanel.setLayout(new BoxLayout(entriesPanel, BoxLayout.Y_AXIS));

        add(titleLabel, BorderLayout.NORTH);
        add(entriesPanel, BorderLayout.CENTER);

        setVisible(false);
    }

    public void showNotification(String title, List<NotificationEntry> entries) {
        titleLabel.setText(title);
        entriesPanel.removeAll();

        for (int i = 0; i < entries.size(); i++) {
            entriesPanel.add(createEntryRow(entries.get(i)));
            entriesPanel.add(Box.createVerticalStrut(4));
        }

        revalidate();
        repaint();
        setVisible(true);
        restartAutoHideTimer();
    }

    private JPanel createEntryRow(NotificationEntry entry) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
        rowPanel.setOpaque(false);

        if (entry.getIconPath() != null) {
            Image icon = IconProvider.loadImage(entry.getIconPath());
            if (icon != null) {
                Image scaledIcon = icon.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
                JLabel iconLabel = new JLabel(new ImageIcon(scaledIcon));
                rowPanel.add(iconLabel);
            }
        }

        JLabel textLabel = new JLabel(entry.getText());
        textLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textLabel.setForeground(TEXT_COLOR);
        rowPanel.add(textLabel);

        if (entry.getDeltaText() != null) {
            JLabel deltaLabel = new JLabel(entry.getDeltaText());
            deltaLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            if (entry.isDeltaPositive()) {
                deltaLabel.setForeground(POSITIVE_COLOR);
            } else {
                deltaLabel.setForeground(NEGATIVE_COLOR);
            }
            rowPanel.add(deltaLabel);
        }

        return rowPanel;
    }

    private void restartAutoHideTimer() {
        if (autoHideTimer != null && autoHideTimer.isRunning()) {
            autoHideTimer.stop();
        }

        autoHideTimer = new Timer(AUTO_HIDE_DELAY_MILLISECONDS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        });
        autoHideTimer.setRepeats(false);
        autoHideTimer.start();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.setColor(BACKGROUND_COLOR);
        graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);

        graphics2D.setColor(BORDER_COLOR);
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 22, 22);

        graphics2D.dispose();
        super.paintComponent(graphics);
    }
}