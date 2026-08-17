package civgame.view.game;

import civgame.model.building.Dock;
import civgame.model.building.TownHallLevel;
import civgame.model.game.GameSession;
import civgame.model.game.NavalUnitProductionCommand;
import civgame.model.resource.ResourceType;
import civgame.model.unit.UnitProductionCostProvider;
import civgame.model.unit.UnitType;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DockMenuFactory {

    public JPopupMenu buildMenu(GameSession gameSession, Dock dock, Runnable onActionSuccess) {
        JPopupMenu popupMenu = new JPopupMenu();
        PopupMenuStyler.style(popupMenu);

        popupMenu.add(createNavalUnitItem(gameSession, dock, UnitType.FISHING_BOAT, onActionSuccess));
        popupMenu.add(createNavalUnitItem(gameSession, dock, UnitType.GALLEY, onActionSuccess));

        return popupMenu;
    }

    private JMenuItem createNavalUnitItem(GameSession gameSession, Dock dock, UnitType unitType, Runnable onActionSuccess) {
        int woodCost = UnitProductionCostProvider.getWoodCost(unitType);
        int foodCost = UnitProductionCostProvider.getFoodCost(unitType);
        int turns = UnitProductionCostProvider.getDurationInTurns(unitType);

        String detailText = "Wood: " + woodCost + "   Food: " + foodCost + "   " + turns + " turns";
        String reason = buildUnavailabilityReason(gameSession, dock, unitType, woodCost, foodCost);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean started = dock.getNavalProductionQueue().startCommand(new NavalUnitProductionCommand(unitType, dock, turns));
                if (started) {
                    gameSession.getResourceStorage().removeResource(ResourceType.WOOD, woodCost);
                    gameSession.getResourceStorage().removeResource(ResourceType.FOOD, foodCost);
                    if (onActionSuccess != null) {
                        onActionSuccess.run();
                    }
                }
            }
        };

        return StyledMenuItemFactory.createItem(null, unitType.name(), detailText, reason, reason.isEmpty(), actionListener);
    }

    private String buildUnavailabilityReason(GameSession gameSession, Dock dock, UnitType unitType, int woodCost, int foodCost) {
        if (dock.getNavalProductionQueue().isBusy()) {
            return "Dock queue is busy";
        }
        if (unitType == UnitType.GALLEY && !gameSession.getTownHallLevelService().isAtLeastLevel(gameSession, TownHallLevel.SETTLEMENT)) {
            return "Requires Town Hall level 2";
        }
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.WOOD, woodCost)) {
            return "Not enough Wood";
        }
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.FOOD, foodCost)) {
            return "Not enough Food";
        }
        return "";
    }
}