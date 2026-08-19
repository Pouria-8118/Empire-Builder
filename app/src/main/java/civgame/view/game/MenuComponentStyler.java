package civgame.view.game;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Color;
import java.awt.Font;

public class MenuComponentStyler {

    private static final Color BACKGROUND_COLOR = new Color(32, 32, 42);
    private static final Color FOREGROUND_COLOR = new Color(235, 235, 235);
    private static final Font ITEM_FONT = new Font("SansSerif", Font.PLAIN, 13);

    private MenuComponentStyler() {
    }

    public static void style(JMenuItem menuItem) {
        menuItem.setBackground(BACKGROUND_COLOR);
        menuItem.setForeground(FOREGROUND_COLOR);
        menuItem.setFont(ITEM_FONT);
        menuItem.setOpaque(true);
    }

    public static void style(JMenu menu) {
        menu.setBackground(BACKGROUND_COLOR);
        menu.setForeground(FOREGROUND_COLOR);
        menu.setFont(ITEM_FONT);
        menu.setOpaque(true);
    }
}