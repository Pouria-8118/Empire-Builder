package civgame.view.menu;

import civgame.util.Constants;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;

public class MainMenuPanel extends JPanel {

    private JButton startButton;
    private JButton settingsButton;
    private JButton exitButton;
    private Image backgroundImage;

    public MainMenuPanel() {
        setLayout(new GridBagLayout());
        loadBackgroundImage();
        createButtons();
        placeButtons();
    }

    private void loadBackgroundImage() {
        URL imageUrl = getClass().getResource(Constants.MENU_BACKGROUND_IMAGE_PATH);
        if (imageUrl != null) {
            backgroundImage = new ImageIcon(imageUrl).getImage();
        }
    }

    private void createButtons() {
        startButton = new JButton("Start");
        settingsButton = new JButton("Settings");
        exitButton = new JButton("Exit");

        setButtonIcon(startButton, Constants.START_ICON_PATH);
        setButtonIcon(settingsButton, Constants.SETTINGS_ICON_PATH);
        setButtonIcon(exitButton, Constants.EXIT_ICON_PATH);

        Dimension buttonSize = new Dimension(200, 50);
        startButton.setPreferredSize(buttonSize);
        settingsButton.setPreferredSize(buttonSize);
        exitButton.setPreferredSize(buttonSize);
    }

    private void setButtonIcon(JButton button, String iconPath) {
        URL iconUrl = getClass().getResource(iconPath);
        if (iconUrl != null) {
            button.setIcon(new ImageIcon(iconUrl));
        }
    }

    private void placeButtons() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.gridx = 0;

        constraints.gridy = 0;
        add(startButton, constraints);

        constraints.gridy = 1;
        add(settingsButton, constraints);

        constraints.gridy = 2;
        add(exitButton, constraints);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (backgroundImage != null) {
            graphics.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getSettingsButton() {
        return settingsButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }
}