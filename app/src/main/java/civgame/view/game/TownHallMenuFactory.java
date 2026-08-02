package civgame.view.game;

import civgame.model.building.TownHallUpgradeCostProvider;
import civgame.model.building.TownHallUpgradeType;
import civgame.model.game.GameSession;
import civgame.model.game.TownHallUpgradeCommand;
import civgame.model.game.UnitProductionCommand;
import civgame.model.game.TownHallProductionQueue;
import civgame.model.game.TownHallUpgradeService;
import civgame.model.resource.ResourceType;
import civgame.model.unit.UnitProductionCostProvider;
import civgame.model.unit.UnitType;
import civgame.util.MenuIconResolver;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TownHallMenuFactory {

    public JPopupMenu buildMenu(GameSession gameSession, Runnable onActionSuccess) {
        JPopupMenu popupMenu = new JPopupMenu();
        PopupMenuStyler.style(popupMenu);

        addUnitProductionItems(popupMenu, gameSession, onActionSuccess);
        popupMenu.add(new JSeparator());
        addUpgradeItems(popupMenu, gameSession, onActionSuccess);

        return popupMenu;
    }

    private void addUnitProductionItems(JPopupMenu popupMenu, GameSession gameSession, Runnable onActionSuccess) {
        popupMenu.add(createUnitItem(gameSession, UnitType.EXPLORER, onActionSuccess));
        popupMenu.add(createUnitItem(gameSession, UnitType.BUILDER, onActionSuccess));
        popupMenu.add(createUnitItem(gameSession, UnitType.WORKER, onActionSuccess));
        popupMenu.add(createUnitItem(gameSession, UnitType.BORDER_EXPANDER, onActionSuccess));
    }

    private JMenuItem createUnitItem(GameSession gameSession, UnitType unitType, Runnable onActionSuccess) {
        int woodCost = UnitProductionCostProvider.getWoodCost(unitType);
        int foodCost = UnitProductionCostProvider.getFoodCost(unitType);
        int turns = UnitProductionCostProvider.getDurationInTurns(unitType);

        String detailText = "Wood: " + woodCost + "   Food: " + foodCost + "   " + turns + " turns";
        String reason = buildUnitUnavailabilityReason(gameSession, woodCost, foodCost);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean started = gameSession.getTownHallProductionQueue().startCommand(new UnitProductionCommand(unitType, turns));
                if (started) {
                    gameSession.getResourceStorage().removeResource(ResourceType.WOOD, woodCost);
                    gameSession.getResourceStorage().removeResource(ResourceType.FOOD, foodCost);
                    if (onActionSuccess != null) {
                        onActionSuccess.run();
                    }
                }
            }
        };

        return StyledMenuItemFactory.createItem(
                MenuIconResolver.resolveForUnit(unitType),
                "Produce " + unitType.name(),
                detailText,
                reason,
                reason.isEmpty(),
                actionListener
        );
    }

    private String buildUnitUnavailabilityReason(GameSession gameSession, int woodCost, int foodCost) {
        TownHallProductionQueue queue = gameSession.getTownHallProductionQueue();
        if (queue.isBusy()) {
            return "Production queue is busy";
        }
        if (gameSession.hasReachedUnitCap()) {
            return "Unit cap reached";
        }
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.WOOD, woodCost)) {
            return "Not enough Wood";
        }
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.FOOD, foodCost)) {
            return "Not enough Food";
        }
        return "";
    }

    private void addUpgradeItems(JPopupMenu popupMenu, GameSession gameSession, Runnable onActionSuccess) {
        popupMenu.add(createUpgradeItem(gameSession, TownHallUpgradeType.STORAGE_UPGRADE, onActionSuccess));
        popupMenu.add(createUpgradeItem(gameSession, TownHallUpgradeType.STONE_MINE_TECH, onActionSuccess));
        popupMenu.add(createUpgradeItem(gameSession, TownHallUpgradeType.IRON_MINE_TECH, onActionSuccess));
        popupMenu.add(createUpgradeItem(gameSession, TownHallUpgradeType.PROFESSIONAL_TOOLS_TECH, onActionSuccess));
        popupMenu.add(createUpgradeItem(gameSession, TownHallUpgradeType.SETTLEMENT_TECH, onActionSuccess));
    }

    private JMenuItem createUpgradeItem(GameSession gameSession, TownHallUpgradeType upgradeType, Runnable onActionSuccess) {
        int woodCost = TownHallUpgradeCostProvider.getWoodCost(upgradeType);
        int stoneCost = TownHallUpgradeCostProvider.getStoneCost(upgradeType);
        int ironCost = TownHallUpgradeCostProvider.getIronCost(upgradeType);
        int turns = TownHallUpgradeCostProvider.getDurationInTurns(upgradeType);

        String title = buildUpgradeTitle(gameSession, upgradeType);
        String detailText = "Wood:" + woodCost + " Stone:" + stoneCost + " Iron:" + ironCost + "   " + turns + " turns";
        String reason = buildUpgradeUnavailabilityReason(gameSession, upgradeType, woodCost, stoneCost, ironCost);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean started = gameSession.getTownHallProductionQueue().startCommand(new TownHallUpgradeCommand(upgradeType, turns));
                if (started) {
                    gameSession.getResourceStorage().removeResource(ResourceType.WOOD, woodCost);
                    gameSession.getResourceStorage().removeResource(ResourceType.STONE, stoneCost);
                    gameSession.getResourceStorage().removeResource(ResourceType.IRON, ironCost);
                    if (onActionSuccess != null) {
                        onActionSuccess.run();
                    }
                }
            }
        };

        return StyledMenuItemFactory.createItem(null, title, detailText, reason, reason.isEmpty(), actionListener);
    }

    private String buildUpgradeTitle(GameSession gameSession, TownHallUpgradeType upgradeType) {
        if (upgradeType == TownHallUpgradeType.STORAGE_UPGRADE) {
            int currentCount = gameSession.getTownHallUpgradeService().getStorageUpgradeCount();
            return "Storage Upgrade [" + currentCount + "/2]";
        }
        return upgradeType.name();
    }

    private String buildUpgradeUnavailabilityReason(GameSession gameSession, TownHallUpgradeType upgradeType, int woodCost, int stoneCost, int ironCost) {
        TownHallProductionQueue queue = gameSession.getTownHallProductionQueue();
        TownHallUpgradeService upgradeService = gameSession.getTownHallUpgradeService();

        if (queue.isBusy()) {
            return "Production queue is busy";
        }
        if (upgradeService.isAlreadyMaxed(upgradeType)) {
            return "Already unlocked";
        }
        if (!upgradeService.isPrerequisiteMet(upgradeType)) {
            return "Requires previous technology";
        }
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.WOOD, woodCost)) {
            return "Not enough Wood";
        }
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.STONE, stoneCost)) {
            return "Not enough Stone";
        }
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.IRON, ironCost)) {
            return "Not enough Iron";
        }
        return "";
    }
}