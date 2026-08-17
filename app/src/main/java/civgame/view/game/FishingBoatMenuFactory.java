package civgame.view.game;

import civgame.model.game.FishingService;
import civgame.model.game.GameSession;
import civgame.model.unit.FishingBoat;
import civgame.util.Constants;

import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FishingBoatMenuFactory {

    private final FishingService fishingService;

    public FishingBoatMenuFactory(FishingService fishingService) {
        this.fishingService = fishingService;
    }

    public JPopupMenu buildMenu(GameSession gameSession, FishingBoat boat, Runnable onActionSuccess) {
        JPopupMenu popupMenu = new JPopupMenu();
        PopupMenuStyler.style(popupMenu);

        boolean canHarvest = fishingService.canHarvest(gameSession, boat);
        String reason = canHarvest ? "" : "Not on a sea hex with fish, or not enough AP";

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean success = fishingService.harvest(gameSession, boat);
                if (success && onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        };

        popupMenu.add(StyledMenuItemFactory.createItem(null, "Harvest Fish", "+" + Constants.FISHING_BOAT_HARVEST_AMOUNT + " Food", reason, canHarvest, actionListener));

        return popupMenu;
    }
}