package civgame.view.menu;

import civgame.util.Constants;
import civgame.view.game.StyledButton;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;

public class MainMenuPanel extends JPanel {

    private StyledButton startButton;
    private StyledButton settingsButton;
    private StyledButton exitButton;
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
        startButton = new StyledButton("Start");
        settingsButton = new StyledButton("Settings");
        exitButton = new StyledButton("Exit");

        setButtonIcon(startButton, Constants.START_ICON_PATH);
        setButtonIcon(settingsButton, Constants.SETTINGS_ICON_PATH);
        setButtonIcon(exitButton, Constants.EXIT_ICON_PATH);

        Dimension buttonSize = new Dimension(220, 55);
        startButton.setPreferredSize(buttonSize);
        settingsButton.setPreferredSize(buttonSize);
        exitButton.setPreferredSize(buttonSize);

        startButton.setIconTextGap(12);
        settingsButton.setIconTextGap(12);
        exitButton.setIconTextGap(12);
    }

    private void setButtonIcon(StyledButton button, String iconPath) {
        URL iconUrl = getClass().getResource(iconPath);
        if (iconUrl != null) {
            ImageIcon originalIcon = new ImageIcon(iconUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledImage));
        }
    }

    private void placeButtons() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(12, 10, 12, 10);
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.CENTER;

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

    public StyledButton getStartButton() {
        return startButton;
    }

    public StyledButton getSettingsButton() {
        return settingsButton;
    }

    public StyledButton getExitButton() {
        return exitButton;
    }
}