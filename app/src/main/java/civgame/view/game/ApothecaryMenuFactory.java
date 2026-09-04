package civgame.view.game;

import civgame.model.building.Apothecary;
import civgame.model.item.ItemType;
import civgame.util.ItemIconResolver;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionListener;

public class ApothecaryMenuFactory {

    public static JPopupMenu createMenu(Apothecary apothecary,
                                         boolean canProduce,
                                         ActionListener produceListener) {
        JPopupMenu menu = new JPopupMenu();
        PopupMenuStyler.style(menu);

        JMenuItem teleportItem = createMenuItem("Craft Teleport Scroll", ItemType.TELEPORT, canProduce, produceListener);
        JMenuItem mobilityItem = createMenuItem("Craft Mobility Potion", ItemType.MOBILITY, canProduce, produceListener);
        JMenuItem combatItem = createMenuItem("Craft Combat Elixir", ItemType.COMBAT, canProduce, produceListener);

        menu.add(teleportItem);
        menu.add(mobilityItem);
        menu.add(combatItem);

        return menu;
    }

    private static JMenuItem createMenuItem(String text, ItemType itemType,
                                            boolean enabled, ActionListener listener) {
        Icon icon = ItemIconResolver.resolveForItem(itemType);
        String reason = enabled ? "" : "Requires Level 2 Town Hall and available Apothecary";
        
        JMenuItem item = StyledMenuItemFactory.createItem(
                icon,
                text,
                "",
                reason,
                enabled,
                e -> {
                    if (listener != null) {
                        listener.actionPerformed(new java.awt.event.ActionEvent(itemType, 0, text));
                    }
                }
        );
        return item;
    }
}