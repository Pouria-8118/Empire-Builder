package civgame.view.trade;

import civgame.model.resource.ResourceType;
import civgame.util.ResourceIconResolver;
import civgame.view.game.StyledButton;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeDialog extends JDialog {

    private JComboBox<String> playerComboBox;
    private final Map<ResourceType, JSpinner> offerSpinners;
    private final Map<ResourceType, JSpinner> requestSpinners;
    private final List<String> playerClientIds;
    private TradeSubmitListener submitListener;

    public TradeDialog(java.awt.Frame owner, List<String> playerNames, List<String> playerClientIds) {
        super(owner, "Propose Trade", true);
        this.playerClientIds = new ArrayList<String>(playerClientIds);
        this.offerSpinners = new HashMap<ResourceType, JSpinner>();
        this.requestSpinners = new HashMap<ResourceType, JSpinner>();

        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(20, 20, 30));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel playerPanel = createPlayerSelectionPanel(playerNames);
        mainPanel.add(playerPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        JPanel resourcePanel = createResourcePanel();
        mainPanel.add(resourcePanel);

        add(mainPanel, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        setSize(450, 550);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private JPanel createPlayerSelectionPanel(List<String> playerNames) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        JLabel label = new JLabel("Trade with:");
        label.setForeground(new Color(200, 200, 200));
        panel.add(label);

        playerComboBox = new JComboBox<String>();
        for (String name : playerNames) {
            playerComboBox.addItem(name);
        }
        playerComboBox.setPreferredSize(new Dimension(200, 30));
        panel.add(playerComboBox);

        return panel;
    }

    private JPanel createResourcePanel() {
        JPanel panel = new JPanel(new GridLayout(5, 3, 10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 100)),
                "Resources",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                null,
                new Color(150, 150, 200)));

        JLabel offerHeader = new JLabel("You Give");
        offerHeader.setForeground(new Color(255, 100, 100));
        offerHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panel.add(new JLabel(""));
        panel.add(offerHeader);

        JLabel requestHeader = new JLabel("You Receive");
        requestHeader.setForeground(new Color(100, 255, 100));
        requestHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panel.add(requestHeader);

        ResourceType[] types = {ResourceType.FOOD, ResourceType.WOOD, ResourceType.STONE, ResourceType.IRON};
        String[] names = {"Food", "Wood", "Stone", "Iron"};

        for (int i = 0; i < types.length; i++) {
            JLabel nameLabel = new JLabel(names[i], ResourceIconResolver.resolveForResource(types[i]), javax.swing.SwingConstants.LEFT);
            nameLabel.setForeground(new Color(200, 200, 200));
            panel.add(nameLabel);

            JSpinner offerSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
            offerSpinner.setPreferredSize(new Dimension(80, 25));
            offerSpinners.put(types[i], offerSpinner);
            panel.add(offerSpinner);

            JSpinner requestSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
            requestSpinner.setPreferredSize(new Dimension(80, 25));
            requestSpinners.put(types[i], requestSpinner);
            panel.add(requestSpinner);
        }

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setOpaque(false);

        StyledButton sendButton = new StyledButton("Send Offer");
        sendButton.addActionListener(e -> handleSubmit());
        panel.add(sendButton);

        StyledButton cancelButton = new StyledButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        panel.add(cancelButton);

        return panel;
    }

    private void handleSubmit() {
        int selectedIndex = playerComboBox.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= playerClientIds.size()) {
            return;
        }
        String receiverId = playerClientIds.get(selectedIndex);
        Map<ResourceType, Integer> offered = new HashMap<ResourceType, Integer>();
        Map<ResourceType, Integer> requested = new HashMap<ResourceType, Integer>();

        for (ResourceType type : offerSpinners.keySet()) {
            int value = (Integer) offerSpinners.get(type).getValue();
            if (value > 0) {
                offered.put(type, value);
            }
        }
        for (ResourceType type : requestSpinners.keySet()) {
            int value = (Integer) requestSpinners.get(type).getValue();
            if (value > 0) {
                requested.put(type, value);
            }
        }

        if (offered.isEmpty() || requested.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please specify at least one resource to give and one to receive.",
                    "Invalid Trade",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (submitListener != null) {
            submitListener.onTradeSubmitted(receiverId, offered, requested);
        }
        dispose();
    }

    public void setSubmitListener(TradeSubmitListener listener) {
        this.submitListener = listener;
    }

    public interface TradeSubmitListener {
        void onTradeSubmitted(String receiverClientId,
                              Map<ResourceType, Integer> offered,
                              Map<ResourceType, Integer> requested);
    }
}