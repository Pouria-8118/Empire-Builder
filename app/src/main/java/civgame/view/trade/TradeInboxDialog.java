package civgame.view.trade;

import civgame.network.message.trade.TradeInboxUpdateBroadcast;
import civgame.view.game.StyledButton;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TradeInboxDialog extends JDialog {

    private final JPanel offersPanel;
    private final List<TradeOfferEntry> offerEntries;
    private TradeActionListener actionListener;

    public TradeInboxDialog(java.awt.Frame owner) {
        super(owner, "Trade Inbox", true);
        this.offerEntries = new ArrayList<TradeOfferEntry>();

        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(20, 20, 30));

        JLabel titleLabel = new JLabel("Pending Trade Offers", javax.swing.SwingConstants.CENTER);
        titleLabel.setForeground(new Color(200, 200, 255));
        titleLabel.setFont(titleLabel.getFont().deriveFont(16.0f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        offersPanel = new JPanel();
        offersPanel.setLayout(new BoxLayout(offersPanel, BoxLayout.Y_AXIS));
        offersPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(offersPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(20, 20, 30));
        scrollPane.setPreferredSize(new Dimension(400, 300));
        add(scrollPane, BorderLayout.CENTER);

        add(createButtonPanel(), BorderLayout.SOUTH);

        setSize(450, 450);
        setLocationRelativeTo(owner);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        StyledButton closeButton = new StyledButton("Close");
        closeButton.addActionListener(e -> dispose());
        panel.add(closeButton);

        return panel;
    }

    public void updateOffers(List<TradeInboxUpdateBroadcast.TradeOfferDTO> offers) {
        offersPanel.removeAll();
        offerEntries.clear();

        if (offers.isEmpty()) {
            JLabel emptyLabel = new JLabel("No pending trade offers", javax.swing.SwingConstants.CENTER);
            emptyLabel.setForeground(new Color(150, 150, 150));
            emptyLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            offersPanel.add(emptyLabel);
        } else {
            for (TradeInboxUpdateBroadcast.TradeOfferDTO offer : offers) {
                TradeOfferEntry entry = new TradeOfferEntry(offer);
                offerEntries.add(entry);
                offersPanel.add(entry);
                offersPanel.add(Box.createVerticalStrut(10));
            }
        }

        offersPanel.revalidate();
        offersPanel.repaint();
    }

    public void setActionListener(TradeActionListener listener) {
        this.actionListener = listener;
    }

    private class TradeOfferEntry extends JPanel {

        public TradeOfferEntry(TradeInboxUpdateBroadcast.TradeOfferDTO offer) {
            setLayout(new BorderLayout(10, 5));
            setBackground(new Color(30, 30, 45));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 60, 80)),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

            JLabel senderLabel = new JLabel("From: " + offer.getSenderUsername());
            senderLabel.setForeground(new Color(150, 200, 255));
            add(senderLabel, BorderLayout.NORTH);

            JPanel resourcePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
            resourcePanel.setOpaque(false);

            StringBuilder offerText = new StringBuilder("Gives: ");
            Map<String, Integer> offered = offer.getOfferedResources();
            for (String key : offered.keySet()) {
                offerText.append(offered.get(key)).append(" ").append(key).append("  ");
            }
            JLabel offerLabel = new JLabel(offerText.toString());
            offerLabel.setForeground(new Color(100, 255, 100));
            resourcePanel.add(offerLabel);

            StringBuilder requestText = new StringBuilder("Wants: ");
            Map<String, Integer> requested = offer.getRequestedResources();
            for (String key : requested.keySet()) {
                requestText.append(requested.get(key)).append(" ").append(key).append("  ");
            }
            JLabel requestLabel = new JLabel(requestText.toString());
            requestLabel.setForeground(new Color(255, 100, 100));
            resourcePanel.add(requestLabel);

            add(resourcePanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setOpaque(false);

            String offerId = offer.getOfferId();

            StyledButton acceptButton = new StyledButton("Accept");
            acceptButton.addActionListener(e -> {
                if (actionListener != null) {
                    actionListener.onAccept(offerId);
                }
            });
            buttonPanel.add(acceptButton);

            StyledButton rejectButton = new StyledButton("Reject");
            rejectButton.addActionListener(e -> {
                if (actionListener != null) {
                    actionListener.onReject(offerId);
                }
            });
            buttonPanel.add(rejectButton);

            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    public interface TradeActionListener {
        void onAccept(String offerId);
        void onReject(String offerId);
    }
}