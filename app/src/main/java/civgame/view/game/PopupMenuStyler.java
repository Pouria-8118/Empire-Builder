package civgame.view.game;

import javax.swing.JPopupMenu;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class PopupMenuStyler {

    private static final Color BACKGROUND_COLOR = new Color(32, 32, 42);
    private static final Color BORDER_COLOR = new Color(200, 170, 90);

    private PopupMenuStyler() {
    }

    public static void style(JPopupMenu popupMenu) {
        popupMenu.setBackground(BACKGROUND_COLOR);
        popupMenu.setBorder(new LineBorder(BORDER_COLOR, 2, true));
    }
}