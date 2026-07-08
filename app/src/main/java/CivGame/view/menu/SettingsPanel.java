package civgame.view.menu;

import civgame.util.Constants;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class SettingsPanel extends JPanel {

    private JScrollBar volumeScrollBar;
    private JLabel volumeLabel;
    private JButton backButton;

    public SettingsPanel() {
        setLayout(new GridBagLayout());
        createComponents();
        placeComponents();
    }

    private void createComponents() {
        volumeLabel = new JLabel("Music Volume: " + Constants.DEFAULT_VOLUME_PERCENT + "%");

        volumeScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, Constants.DEFAULT_VOLUME_PERCENT, 1, 0, 101);
        volumeScrollBar.setPreferredSize(new Dimension(300, 20));

        backButton = new JButton("Back");
    }

    private void placeComponents() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.gridx = 0;

        constraints.gridy = 0;
        add(volumeLabel, constraints);

        constraints.gridy = 1;
        add(volumeScrollBar, constraints);

        constraints.gridy = 2;
        add(backButton, constraints);
    }

    public void updateVolumeLabel(int percent) {
        volumeLabel.setText("Music Volume: " + percent + "%");
    }

    public JScrollBar getVolumeScrollBar() {
        return volumeScrollBar;
    }

    public JButton getBackButton() {
        return backButton;
    }
}