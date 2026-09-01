package civgame.view.lobby;

import civgame.util.Constants;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.net.URL;

public class LobbyFrame extends JFrame {
    
    private final LobbyPanel lobbyPanel;
    
    public LobbyFrame() {
        super("Multiplayer Lobby - " + Constants.WINDOW_TITLE);
        this.lobbyPanel = new LobbyPanel();
        setupFrame();
    }
    
    private void setupFrame() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new Dimension(600, 700));
        setMinimumSize(new Dimension(500, 600));
        setLocationRelativeTo(null);
        setResizable(true);
        loadApplicationIcon();
        add(lobbyPanel);
    }
    
    private void loadApplicationIcon() {
        URL iconUrl = getClass().getResource(Constants.APP_ICON_PATH);
        if (iconUrl != null) {
            setIconImage(new ImageIcon(iconUrl).getImage());
        }
    }
    
    public LobbyPanel getLobbyPanel() {
        return lobbyPanel;
    }
}