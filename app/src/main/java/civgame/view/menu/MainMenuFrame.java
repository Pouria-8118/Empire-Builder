package civgame.view.menu;

import civgame.util.Constants;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.net.URL;

public class MainMenuFrame extends JFrame {

    public static final String MENU_CARD_NAME = "MENU_CARD";
    public static final String SETTINGS_CARD_NAME = "SETTINGS_CARD";

    private CardLayout cardLayout;
    private JPanel cardContainer;
    private MainMenuPanel mainMenuPanel;
    private SettingsPanel settingsPanel;

    public MainMenuFrame() {
        super(Constants.WINDOW_TITLE);
        setupFrame();
        setupCards();
    }

    private void setupFrame() {
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        loadApplicationIcon();
    }

    private void loadApplicationIcon() {
        URL iconUrl = getClass().getResource(Constants.APP_ICON_PATH);
        if (iconUrl != null) {
            setIconImage(new ImageIcon(iconUrl).getImage());
        }
    }

    private void setupCards() {
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);

        mainMenuPanel = new MainMenuPanel();
        settingsPanel = new SettingsPanel();

        cardContainer.add(mainMenuPanel, MENU_CARD_NAME);
        cardContainer.add(settingsPanel, SETTINGS_CARD_NAME);

        add(cardContainer);
    }

    public void showMenuCard() {
        cardLayout.show(cardContainer, MENU_CARD_NAME);
    }

    public void showSettingsCard() {
        cardLayout.show(cardContainer, SETTINGS_CARD_NAME);
    }

    public MainMenuPanel getMainMenuPanel() {
        return mainMenuPanel;
    }

    public SettingsPanel getSettingsPanel() {
        return settingsPanel;
    }
}