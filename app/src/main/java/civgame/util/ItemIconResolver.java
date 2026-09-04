package civgame.util;

import civgame.model.item.ItemType;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Image;

public class ItemIconResolver {

    private static final int ICON_SIZE = 22;

    private ItemIconResolver() {
    }

    public static Icon resolveForItem(ItemType itemType) {
        String path = getIconPath(itemType);
        if (path != null) {
            Image image = IconProvider.loadImage(path);
            if (image != null) {
                return new ImageIcon(image.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
            }
        }
        return createFallbackIcon(itemType);
    }

    private static String getIconPath(ItemType itemType) {
        if (itemType == ItemType.TELEPORT) {
            return Constants.ITEM_TELEPORT_ICON_PATH;
        }
        if (itemType == ItemType.MOBILITY) {
            return Constants.ITEM_MOBILITY_ICON_PATH;
        }
        if (itemType == ItemType.COMBAT) {
            return Constants.ITEM_COMBAT_ICON_PATH;
        }
        return null;
    }

    private static Icon createFallbackIcon(ItemType itemType) {
        if (itemType == ItemType.TELEPORT) {
            return new LetterIcon("T", new Color(100, 150, 255), ICON_SIZE);
        }
        if (itemType == ItemType.MOBILITY) {
            return new LetterIcon("M", new Color(100, 255, 100), ICON_SIZE);
        }
        return new LetterIcon("C", new Color(255, 100, 100), ICON_SIZE);
    }
}