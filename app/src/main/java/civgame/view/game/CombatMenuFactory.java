package civgame.view.game;

import civgame.model.game.GameSession;
import civgame.model.unit.Unit;

import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CombatMenuFactory {

    public JPopupMenu buildMenu(GameSession gameSession, Unit unit, Runnable onAttackModeActivated) {
        JPopupMenu popupMenu = new JPopupMenu();
        PopupMenuStyler.style(popupMenu);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onAttackModeActivated.run();
            }
        };

        popupMenu.add(StyledMenuItemFactory.createItem(null, "Attack", "Click a highlighted hex on the map to choose a target", "", true, actionListener));

        return popupMenu;
    }
}