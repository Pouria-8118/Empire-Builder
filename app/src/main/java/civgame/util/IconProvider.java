package civgame.util;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class IconProvider {

    private static final Map<String, Image> imageCache = new HashMap<String, Image>();

    private IconProvider() {
    }

    public static Image loadImage(String resourcePath) {
        if (imageCache.containsKey(resourcePath)) {
            return imageCache.get(resourcePath);
        }

        URL imageUrl = IconProvider.class.getResource(resourcePath);
        Image loadedImage = null;
        if (imageUrl != null) {
            loadedImage = new ImageIcon(imageUrl).getImage();
        }

        imageCache.put(resourcePath, loadedImage);
        return loadedImage;
    }
}