package civgame.view.game;

import civgame.model.game.BorderExpansionService;
import civgame.model.game.GameSession;
import civgame.model.unit.BorderExpander;
import civgame.util.Constants;
import civgame.util.MenuIconResolver;

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
        PopupMenuStyler.style(popupMenu);

        boolean canExpand = borderExpansionService.canExpand(gameSession, expander);
        String reason = "";
        if (!canExpand) {
            reason = "Tile not explored yet, or not enough AP";
        }

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean success = borderExpansionService.expand(gameSession, expander);
                if (success && onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        };

        JMenuItem menuItem = StyledMenuItemFactory.createItem(
                MenuIconResolver.resolveForUnit(expander.getUnitType()),
                "Claim Territory Here",
                "AP cost: " + Constants.BORDER_EXPAND_ACTION_POINT_COST + "   |   Unit is consumed on use",
                reason,
                canExpand,
                actionListener
        );

        popupMenu.add(menuItem);
        return popupMenu;
    }
}