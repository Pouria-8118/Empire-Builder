package civgame.util;

import civgame.model.map.TileResourceType;

public class ResourceIconProvider {

    private ResourceIconProvider() {
    }

    public static String getIconPath(TileResourceType resourceType) {
        if (resourceType == TileResourceType.WOOD) {
            return Constants.WOOD_ICON_PATH;
        }
        if (resourceType == TileResourceType.STONE) {
            return Constants.STONE_ICON_PATH;
        }
        if (resourceType == TileResourceType.WHEAT) {
            return Constants.WHEAT_ICON_PATH;
        }
        if (resourceType == TileResourceType.RICE) {
            return Constants.RICE_ICON_PATH;
        }
        if (resourceType == TileResourceType.CATTLE) {
            return Constants.CATTLE_ICON_PATH;
        }
        if (resourceType == TileResourceType.SHEEP) {
            return Constants.SHEEP_ICON_PATH;
        }
        if (resourceType == TileResourceType.FISH) {
            return Constants.FISH_ICON_PATH;
        }
        return null;
    }
}