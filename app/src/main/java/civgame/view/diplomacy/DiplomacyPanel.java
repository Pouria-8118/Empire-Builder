package civgame.view.diplomacy;

import civgame.client.diplomacy.DiplomacyClientState;
import civgame.model.diplomacy.PlayerRelation;
import civgame.view.game.StyledButton;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

public class DiplomacyPanel extends JPanel {
    
    private static final Color BACKGROUND_COLOR = new Color(20, 20, 30, 210);
    private static final Color BORDER_COLOR = new Color(200, 170, 90);
    private static final Color TEXT_COLOR = new Color(235, 235, 235);
    
    private final JLabel titleLabel;
    private final JPanel relationsPanel;
    private final StyledButton openDialogButton;
    private DiplomacyActionListener actionListener;
    
    public DiplomacyPanel() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        
        this.titleLabel = new JLabel("Diplomacy:");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        this.relationsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        relationsPanel.setOpaque(false);
        
        this.openDialogButton = new StyledButton("Open", new Dimension(80, 28));
        openDialogButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (actionListener != null) {
                    actionListener.onOpenDialogRequested();
                }
            }
        });
        
        add(titleLabel);
        add(relationsPanel);
        add(openDialogButton);
    }
    
    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(BACKGROUND_COLOR);
        graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        graphics2D.setColor(BORDER_COLOR);
        graphics2D.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        graphics2D.dispose();
        super.paintComponent(graphics);
    }
    
    public void setActionListener(DiplomacyActionListener listener) {
        this.actionListener = listener;
    }
    
    public void refresh(DiplomacyClientState state) {
        relationsPanel.removeAll();
        
        if (state == null) {
            JLabel emptyLabel = new JLabel("No opponents");
            emptyLabel.setForeground(TEXT_COLOR);
            relationsPanel.add(emptyLabel);
            revalidate();
            repaint();
            return;
        }
        
        List<DiplomacyClientState.OpponentInfo> opponents = state.getAllOpponents();
        if (opponents.isEmpty()) {
            JLabel emptyLabel = new JLabel("No opponents");
            emptyLabel.setForeground(TEXT_COLOR);
            relationsPanel.add(emptyLabel);
            revalidate();
            repaint();
            return;
        }
        
        for (int i = 0; i < opponents.size(); i++) {
            DiplomacyClientState.OpponentInfo opponent = opponents.get(i);
            PlayerRelation relation = state.getRelationWithMe(opponent.getClientId());
            relationsPanel.add(createRelationChip(opponent, relation));
        }
        
        revalidate();
        repaint();
    }
    
    private JPanel createRelationChip(DiplomacyClientState.OpponentInfo opponent, PlayerRelation relation) {
        JPanel chip = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        chip.setOpaque(false);
        
        JLabel nameLabel = new JLabel(opponent.getUsername());
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        JLabel relationLabel = new JLabel("[" + relation.getDisplayName() + "]");
        relationLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        relationLabel.setForeground(getRelationColor(relation));
        
        chip.add(nameLabel);
        chip.add(relationLabel);
        
        return chip;
    }
    
    private Color getRelationColor(PlayerRelation relation) {
        switch (relation) {
            case ALLY:
                return new Color(110, 220, 120);
            case FRIENDLY:
                return new Color(140, 200, 140);
            case NEUTRAL:
                return new Color(200, 200, 200);
            case HOSTILE:
                return new Color(240, 170, 90);
            case WAR:
                return new Color(230, 90, 90);
            default:
                return TEXT_COLOR;
        }
    }
    
    public enum DiplomacyAction {
        DECLARE_WAR,
        OFFER_PEACE,
        PROPOSE_ALLIANCE
    }
    
    public interface DiplomacyActionListener {
        void onActionRequested(DiplomacyClientState.OpponentInfo opponent, DiplomacyAction action);
        void onOpenDialogRequested();
    }
}