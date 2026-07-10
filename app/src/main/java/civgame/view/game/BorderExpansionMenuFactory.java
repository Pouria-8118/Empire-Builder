package civgame.view.game;

import civgame.model.game.BorderExpansionService;
import civgame.model.game.GameSession;
import civgame.model.unit.BorderExpander;
import civgame.util.Constants;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BorderExpansionMenuFactory {

    private final BorderExpansionService borderExpansionService;

    public BorderExpansionMenuFactory(BorderExpansionService borderExpansionService) {
        this.borderExpansionService = borderExpansionService;
    }

    public JPopupMenu buildMenu(GameSession gameSession, BorderExpander expander, Runnable onActionSuccess) {
        JPopupMenu popupMenu = new JPopupMenu();

        String label = "Claim Territory Here (AP cost: " + Constants.BORDER_EXPAND_ACTION_POINT_COST + ")";
        boolean canExpand = borderExpansionService.canExpand(gameSession, expander);

        if (!canExpand) {
            label = label + " [Tile not explored or no AP]";
        }

        JMenuItem menuItem = new JMenuItem(label);
        menuItem.setEnabled(canExpand);

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean success = borderExpansionService.expand(gameSession, expander);
                if (success && onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        });

        popupMenu.add(menuItem);
        return popupMenu;
    }

}