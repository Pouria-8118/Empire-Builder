package civgame.view.diplomacy;

import civgame.client.diplomacy.DiplomacyClientState;
import civgame.model.diplomacy.PlayerRelation;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DiplomacyDialog extends JDialog {
    
    private final DiplomacyClientState state;
    private final DiplomacyPanel.DiplomacyActionListener listener;
    private final JPanel rowsPanel;
    
    public DiplomacyDialog(Frame owner, DiplomacyClientState state, DiplomacyPanel.DiplomacyActionListener listener) {
        super(owner, "Diplomacy Relations", true);
        this.state = state;
        this.listener = listener;
        this.rowsPanel = new JPanel(new GridBagLayout());
        
        setupDialog();
    }
    
    private void setupDialog() {
        setSize(new Dimension(600, 500));
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(8, 8));
        
        JLabel titleLabel = new JLabel("Diplomacy Relations", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);
        
        rowsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildRows();
        add(rowsPanel, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.setPreferredSize(new Dimension(120, 36));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void buildRows() {
        rowsPanel.removeAll();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        
        List<DiplomacyClientState.OpponentInfo> opponents = state.getAllOpponents();
        for (int i = 0; i < opponents.size(); i++) {
            DiplomacyClientState.OpponentInfo opponent = opponents.get(i);
            PlayerRelation relation = state.getRelationWithMe(opponent.getClientId());
            
            constraints.gridx = 0;
            constraints.gridy = i;
            constraints.weightx = 0.2;
            JLabel nameLabel = new JLabel(opponent.getUsername());
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            rowsPanel.add(nameLabel, constraints);
            
            constraints.gridx = 1;
            constraints.weightx = 0.3;
            JLabel relationLabel = new JLabel("Relation: " + relation.getDisplayName());
            relationLabel.setForeground(getRelationColor(relation));
            relationLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            rowsPanel.add(relationLabel, constraints);
            
            constraints.gridx = 2;
            constraints.weightx = 0.5;
            JPanel actionsPanel = createActionPanel(opponent, relation);
            rowsPanel.add(actionsPanel, constraints);
        }
        
        revalidate();
        repaint();
    }
    
    private JPanel createActionPanel(DiplomacyClientState.OpponentInfo opponent, PlayerRelation relation) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        
        if (relation != PlayerRelation.WAR) {
            JButton declareWarButton = new JButton("Declare War");
            declareWarButton.setForeground(new Color(230, 90, 90));
            declareWarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (listener != null) {
                        listener.onActionRequested(opponent, DiplomacyPanel.DiplomacyAction.DECLARE_WAR);
                    }
                }
            });
            panel.add(declareWarButton);
        }
        
        if (relation == PlayerRelation.WAR) {
            JButton offerPeaceButton = new JButton("Offer Peace");
            offerPeaceButton.setForeground(new Color(110, 220, 120));
            offerPeaceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (listener != null) {
                        listener.onActionRequested(opponent, DiplomacyPanel.DiplomacyAction.OFFER_PEACE);
                    }
                }
            });
            panel.add(offerPeaceButton);
        }
        
        if ((relation == PlayerRelation.FRIENDLY || relation == PlayerRelation.NEUTRAL) 
                && relation != PlayerRelation.ALLY) {
            JButton proposeAllianceButton = new JButton("Propose Alliance");
            proposeAllianceButton.setForeground(new Color(120, 180, 255));
            proposeAllianceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (listener != null) {
                        listener.onActionRequested(opponent, DiplomacyPanel.DiplomacyAction.PROPOSE_ALLIANCE);
                    }
                }
            });
            panel.add(proposeAllianceButton);
        }
        
        return panel;
    }
    
    private Color getRelationColor(PlayerRelation relation) {
        switch (relation) {
            case ALLY:
                return new Color(110, 220, 120);
            case FRIENDLY:
                return new Color(140, 200, 140);
            case NEUTRAL:
                return new Color(100, 100, 100);
            case HOSTILE:
                return new Color(240, 170, 90);
            case WAR:
                return new Color(230, 90, 90);
            default:
                return Color.BLACK;
        }
    }
}