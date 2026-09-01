package civgame.view.lobby;

import civgame.network.NetworkConfig;
import civgame.view.game.StyledButton;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ConnectDialog extends JDialog {
    
    private final JTextField serverIpField;
    private final JTextField serverPortField;
    private final JTextField usernameField;
    private final StyledButton connectButton;
    private final StyledButton cancelButton;
    
    private boolean confirmed;
    
    public ConnectDialog(Frame owner) {
        super(owner, "Connect to Server", true);
        this.serverIpField = new JTextField(NetworkConfig.DEFAULT_SERVER_IP, 20);
        this.serverPortField = new JTextField(String.valueOf(NetworkConfig.DEFAULT_TCP_PORT), 10);
        this.usernameField = new JTextField("Player" + (int)(Math.random() * 1000), 15);
        this.connectButton = new StyledButton("Connect");
        this.cancelButton = new StyledButton("Cancel");
        this.confirmed = false;
        
        setupDialog();
    }
    
    private void setupDialog() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.anchor = GridBagConstraints.WEST;
        
        constraints.gridx = 0;
        constraints.gridy = 0;
        formPanel.add(new JLabel("Server IP:"), constraints);
        
        constraints.gridx = 1;
        formPanel.add(serverIpField, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 1;
        formPanel.add(new JLabel("Server Port:"), constraints);
        
        constraints.gridx = 1;
        formPanel.add(serverPortField, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 2;
        formPanel.add(new JLabel("Username:"), constraints);
        
        constraints.gridx = 1;
        formPanel.add(usernameField, constraints);
        
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        connectButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(connectButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        connectButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        pack();
        setLocationRelativeTo(getOwner());
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public String getServerIp() {
        return serverIpField.getText().trim();
    }
    
    public int getServerPort() {
        try {
            return Integer.parseInt(serverPortField.getText().trim());
        } catch (NumberFormatException e) {
            return NetworkConfig.DEFAULT_TCP_PORT;
        }
    }
    
    public String getUsername() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            return "Player" + (int)(Math.random() * 1000);
        }
        return username;
    }
    
    public static ConnectResult showConnectDialog(JFrame parent) {
        ConnectDialog dialog = new ConnectDialog(parent);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            return new ConnectResult(
                    true,
                    dialog.getServerIp(),
                    dialog.getServerPort(),
                    dialog.getUsername()
            );
        }
        return new ConnectResult(false, null, 0, null);
    }
    
    public static class ConnectResult {
        private final boolean confirmed;
        private final String serverIp;
        private final int serverPort;
        private final String username;
        
        public ConnectResult(boolean confirmed, String serverIp, int serverPort, String username) {
            this.confirmed = confirmed;
            this.serverIp = serverIp;
            this.serverPort = serverPort;
            this.username = username;
        }
        
        public boolean isConfirmed() {
            return confirmed;
        }
        
        public String getServerIp() {
            return serverIp;
        }
        
        public int getServerPort() {
            return serverPort;
        }
        
        public String getUsername() {
            return username;
        }
    }
}