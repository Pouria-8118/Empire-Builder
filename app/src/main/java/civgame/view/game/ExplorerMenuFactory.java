package civgame.view.game;

import civgame.model.unit.Explorer;
import civgame.util.MenuIconResolver;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExplorerMenuFactory {

    public JPopupMenu buildMenu(Explorer explorer, Runnable onActionSuccess) {
        JPopupMenu popupMenu = new JPopupMenu();
        PopupMenuStyler.style(popupMenu);

        String apText = explorer.getCurrentActionPoints() + "/" + explorer.getMaxActionPoints();
        JMenuItem infoItem = StyledMenuItemFactory.createItem(
                MenuIconResolver.resolveForUnit(explorer.getUnitType()),
                "Explorer",
                "Action Points: " + apText,
                "",
                false,
                null
        );
        popupMenu.add(infoItem);
        popupMenu.add(createAutoExploreToggleItem(explorer, onActionSuccess));

        return popupMenu;
    }

    private JMenuItem createAutoExploreToggleItem(Explorer explorer, Runnable onActionSuccess) {
        String title;
        String detailText;

        if (explorer.isAutoExploreEnabled()) {
            title = "Disable Auto-Explore";
            detailText = "Currently exploring automatically each turn";
        } else {
            title = "Enable Auto-Explore";
            detailText = "Will move toward unknown territory automatically each turn";
        }

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                explorer.setAutoExploreEnabled(!explorer.isAutoExploreEnabled());
                if (onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        };

        return StyledMenuItemFactory.createItem(null, title, detailText, "", true, actionListener);
    }
}