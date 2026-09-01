package civgame.view.lobby;

import civgame.util.Constants;
import civgame.view.chat.ChatPanel;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.net.URL;

public class LobbyFrame extends JFrame {
    
    private final LobbyPanel lobbyPanel;
    private final ChatPanel chatPanel;
    
    public LobbyFrame() {
        super("Multiplayer Lobby - " + Constants.WINDOW_TITLE);
        this.lobbyPanel = new LobbyPanel();
        this.chatPanel = new ChatPanel();
        setupFrame();
    }
    
    private void setupFrame() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new Dimension(950, 700));
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setResizable(true);
        loadApplicationIcon();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, lobbyPanel, chatPanel);
        splitPane.setDividerLocation(600);
        splitPane.setResizeWeight(0.7);
        splitPane.setDividerSize(4);
        add(splitPane);
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
    
    public ChatPanel getChatPanel() {
        return chatPanel;
    }
}