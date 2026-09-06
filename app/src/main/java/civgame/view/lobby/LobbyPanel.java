package civgame.view.lobby;

import civgame.server.lobby.PlayerSlot;
import civgame.view.game.StyledButton;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class LobbyPanel extends JPanel {
    
    private final JLabel titleLabel;
    private final JLabel statusLabel;
    private final JLabel hostLabel;
    private final JLabel mapLabel;
    private final DefaultListModel<String> playerListModel;
    private final JList<String> playerList;
    private final JComboBox<String> mapComboBox;
    private final StyledButton readyButton;
    private final StyledButton startButton;
    private final StyledButton leaveButton;
    private final JCheckBox cheatsCheckBox;
    
    private String myClientId;
    private boolean isHost;
    
    public LobbyPanel() {
        this.titleLabel = new JLabel("Multiplayer Lobby");
        this.statusLabel = new JLabel("Status: Waiting for players...");
        this.hostLabel = new JLabel("Host: -");
        this.mapLabel = new JLabel("Selected Map: -");
        this.playerListModel = new DefaultListModel<>();
        this.playerList = new JList<>(playerListModel);
        this.mapComboBox = new JComboBox<>();
        this.readyButton = new StyledButton("Ready");
        this.startButton = new StyledButton("Start Game");
        this.leaveButton = new StyledButton("Leave Lobby");
        this.cheatsCheckBox = new JCheckBox("Enable Cheats (Host Only)");
        this.myClientId = null;
        this.isHost = false;
        
        setupLayout();
        setupStyling();
        setupInitialState();
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, constraints);
        
        constraints.gridy = 1;
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLabel, constraints);
        
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridx = 0;
        add(hostLabel, constraints);
        
        constraints.gridx = 1;
        add(mapLabel, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        JLabel playersTitleLabel = new JLabel("Players in Lobby:");
        playersTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(playersTitleLabel, constraints);
        
        constraints.gridy = 4;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        playerList.setVisibleRowCount(8);
        playerList.setCellRenderer(new PlayerListCellRenderer());
        JScrollPane scrollPane = new JScrollPane(playerList);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        add(scrollPane, constraints);
        
        constraints.gridy = 5;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel mapSelectionLabel = new JLabel("Select Map (Host Only):");
        add(mapSelectionLabel, constraints);
        
        constraints.gridy = 6;
        mapComboBox.setEnabled(false);
        add(mapComboBox, constraints);
        
        constraints.gridy = 7;
        cheatsCheckBox.setEnabled(false);
        add(cheatsCheckBox, constraints);
        
        constraints.gridy = 8;
        JPanel buttonPanel = new JPanel();
        Dimension buttonSize = new Dimension(180, 45);
        readyButton.setPreferredSize(buttonSize);
        startButton.setPreferredSize(buttonSize);
        leaveButton.setPreferredSize(buttonSize);
        buttonPanel.add(readyButton);
        buttonPanel.add(startButton);
        buttonPanel.add(leaveButton);
        add(buttonPanel, constraints);
    }
    
    private void setupStyling() {
        setBackground(new Color(40, 40, 50));
        titleLabel.setForeground(Color.WHITE);
        statusLabel.setForeground(new Color(200, 200, 200));
        hostLabel.setForeground(Color.WHITE);
        mapLabel.setForeground(Color.WHITE);
        playerList.setBackground(new Color(60, 60, 70));
        playerList.setForeground(Color.WHITE);
        playerList.setSelectionBackground(new Color(80, 120, 180));
    }
    
    private void setupInitialState() {
        startButton.setEnabled(false);
        readyButton.setEnabled(true);
        leaveButton.setEnabled(true);
        mapComboBox.setEnabled(false);
    }
    
    public void initializeWithClientId(String myClientId) {
        this.myClientId = myClientId;
    }
    
    public void updatePlayerList(List<PlayerSlot.PlayerSlotInfo> players, String hostClientId) {
        this.isHost = myClientId != null && myClientId.equals(hostClientId);
        
        playerListModel.clear();
        for (int i = 0; i < players.size(); i++) {
            PlayerSlot.PlayerSlotInfo player = players.get(i);
            String displayName = player.getUsername();
            if (player.isReady()) {
                displayName += "Ready";
            } else {
                displayName += "Not Ready";
            }
            if (player.getClientId().equals(hostClientId)) {
                displayName += " (HOST)";
            }
            playerListModel.addElement(displayName);
        }
        
        updateHostDisplay(hostClientId, players);
        updateControlStates();
    }
    
    private void updateHostDisplay(String hostClientId, List<PlayerSlot.PlayerSlotInfo> players) {
        String hostUsername = "Unknown";
        for (int i = 0; i < players.size(); i++) {
            PlayerSlot.PlayerSlotInfo player = players.get(i);
            if (player.getClientId().equals(hostClientId)) {
                hostUsername = player.getUsername();
                break;
            }
        }
        hostLabel.setText("Host: " + hostUsername);
    }
    
    public void updateMapSelection(String mapName) {
        if (mapName != null && !mapName.isEmpty()) {
            mapLabel.setText("Selected Map: " + mapName);
        } else {
            mapLabel.setText("Selected Map: -");
        }
    }
    
    public void setAvailableMaps(List<String> mapNames) {
        mapComboBox.removeAllItems();
        for (int i = 0; i < mapNames.size(); i++) {
            mapComboBox.addItem(mapNames.get(i));
        }
    }
    
    public void setStatusMessage(String message) {
        statusLabel.setText("Status: " + message);
    }
    
    public String getSelectedMapName() {
        Object selected = mapComboBox.getSelectedItem();
        return selected != null ? selected.toString() : null;
    }
    
    private void updateControlStates() {
        mapComboBox.setEnabled(isHost);
        startButton.setEnabled(isHost);
        cheatsCheckBox.setEnabled(isHost);
    }
    
    public StyledButton getReadyButton() {
        return readyButton;
    }
    
    public StyledButton getStartButton() {
        return startButton;
    }
    
    public StyledButton getLeaveButton() {
        return leaveButton;
    }
    
    public JComboBox<String> getMapComboBox() {
        return mapComboBox;
    }
    
    public JCheckBox getCheatsCheckBox() {
        return cheatsCheckBox;
    }
    public void updateCheatsStatus(boolean enabled) {
        cheatsCheckBox.setSelected(enabled);
    }
    
    private static class PlayerListCellRenderer extends JLabel implements ListCellRenderer<String> {
        
        public PlayerListCellRenderer() {
            setOpaque(true);
            setFont(new Font("Arial", Font.PLAIN, 14));
        }
        
        @Override
        public Component getListCellRendererComponent(JList<? extends String> list,
                                                       String value,
                                                       int index,
                                                       boolean isSelected,
                                                       boolean cellHasFocus) {
            setText(value);
            
            if (isSelected) {
                setBackground(new Color(80, 120, 180));
                setForeground(Color.WHITE);
            } else {
                setBackground(new Color(60, 60, 70));
                setForeground(Color.WHITE);
            }
            
            return this;
        }
    }
}