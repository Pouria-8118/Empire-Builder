package civgame.view.game;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;


public class StyledMenuItemFactory {

    private static final Color BACKGROUND_COLOR = new Color(32, 32, 42);
    private static final Color DISABLED_TEXT_COLOR = new Color(150, 150, 150);
    private static final Color ENABLED_TEXT_COLOR = new Color(235, 235, 235);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 13);

    private StyledMenuItemFactory() {
    }

    public static JMenuItem createItem(Icon icon, String title, String detailText, String blockedReason, boolean enabled, ActionListener actionListener) {
        String htmlText = buildHtmlText(title, detailText, blockedReason, enabled);

        JMenuItem menuItem = new JMenuItem(htmlText);
        menuItem.setIcon(icon);
        menuItem.setFont(TITLE_FONT);
        menuItem.setBackground(BACKGROUND_COLOR);
        menuItem.setOpaque(true);
        menuItem.setBorder(new EmptyBorder(6, 8, 6, 12));
        menuItem.setIconTextGap(10);
        menuItem.setEnabled(enabled);

        if (enabled) {
            menuItem.setForeground(ENABLED_TEXT_COLOR);
        } else {
            menuItem.setForeground(DISABLED_TEXT_COLOR);
        }

        if (actionListener != null) {
            menuItem.addActionListener(actionListener);
        }

        return menuItem;
    }

    private static String buildHtmlText(String title, String detailText, String blockedReason, boolean enabled) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><body style='width:230px'>");
        htmlBuilder.append("<b>").append(title).append("</b><br>");
        htmlBuilder.append("<span style='font-size:10px;color:#AAAAAA'>").append(detailText).append("</span>");

        if (!enabled && blockedReason != null && !blockedReason.isEmpty()) {
            htmlBuilder.append("<br><span style='font-size:10px;color:#E6785A'>").append(blockedReason).append("</span>");
        }

        htmlBuilder.append("</body></html>");
        return htmlBuilder.toString();
    }
}