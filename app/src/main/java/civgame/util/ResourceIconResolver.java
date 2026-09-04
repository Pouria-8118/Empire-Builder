package civgame.util;

import civgame.model.resource.ResourceType;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Image;

public class ResourceIconResolver {

    private static final int ICON_SIZE = 20;

    private ResourceIconResolver() {
    }

    public static Icon resolveForResource(ResourceType resourceType) {
        String path = getIconPath(resourceType);
        if (path != null) {
            Image image = IconProvider.loadImage(path);
            if (image != null) {
                return new ImageIcon(image.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
            }
        }
        return createFallbackIcon(resourceType);
    }

    private static String getIconPath(ResourceType resourceType) {
        if (resourceType == ResourceType.FOOD) {
            return Constants.FOOD_ICON_PATH;
        }
        if (resourceType == ResourceType.WOOD) {
            return Constants.WOOD_ICON_PATH;
        }
        if (resourceType == ResourceType.STONE) {
            return Constants.STONE_ICON_PATH;
        }
        if (resourceType == ResourceType.IRON) {
            return Constants.IRON_ICON_PATH;
        }
        return null;
    }

    private static Icon createFallbackIcon(ResourceType resourceType) {
        if (resourceType == ResourceType.FOOD) {
            return new LetterIcon("F", new Color(139, 69, 19), ICON_SIZE);
        }
        if (resourceType == ResourceType.WOOD) {
            return new LetterIcon("W", new Color(101, 67, 33), ICON_SIZE);
        }
        if (resourceType == ResourceType.STONE) {
            return new LetterIcon("S", new Color(128, 128, 128), ICON_SIZE);
        }
        return new LetterIcon("I", new Color(64, 64, 64), ICON_SIZE);
    }
}