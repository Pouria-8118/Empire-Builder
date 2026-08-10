package civgame.view.game;

import civgame.model.building.TownHallLevel;
import civgame.model.building.TownHallLevelCostProvider;
import civgame.model.building.TownHallUpgradeCostProvider;
import civgame.model.building.TownHallUpgradeType;
import civgame.model.game.GameSession;
import civgame.model.game.TownHallLevelUpCommand;
import civgame.model.game.TownHallProductionQueue;
import civgame.model.game.TownHallUpgradeCommand;
import civgame.model.game.TownHallUpgradeService;
import civgame.model.game.UnitProductionCommand;
import civgame.model.resource.ResourceType;
import civgame.model.unit.MilitaryUnitCapProvider;
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

        popupMenu.add(createCancelItem(gameSession, onActionSuccess));
        popupMenu.add(new JSeparator());
        addUnitProductionItems(popupMenu, gameSession, onActionSuccess);
        popupMenu.add(new JSeparator());
        addLevelUpItem(popupMenu, gameSession, onActionSuccess);
        popupMenu.add(new JSeparator());
        addUpgradeItems(popupMenu, gameSession, onActionSuccess);

        return popupMenu;
    }

    private JMenuItem createCancelItem(GameSession gameSession, Runnable onActionSuccess) {
        TownHallProductionQueue queue = gameSession.getTownHallProductionQueue();
        boolean enabled = queue.isBusy();
        String detailText = "Cancels the active order. Spent resources are not refunded.";
        String reason = "No active order";
        if (enabled) {
            reason = "";
        }

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                queue.cancelCurrentCommand(gameSession);
                if (onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        };

        return StyledMenuItemFactory.createItem(null, "Cancel Current Order", detailText, reason, enabled, actionListener);
    }

    private void addUnitProductionItems(JPopupMenu popupMenu, GameSession gameSession, Runnable onActionSuccess) {
        popupMenu.add(createUnitItem(gameSession, UnitType.EXPLORER, onActionSuccess));
        popupMenu.add(createUnitItem(gameSession, UnitType.BUILDER, onActionSuccess));
        popupMenu.add(createUnitItem(gameSession, UnitType.WORKER, onActionSuccess));
        popupMenu.add(createUnitItem(gameSession, UnitType.BORDER_EXPANDER, onActionSuccess));
        popupMenu.add(createUnitItem(gameSession, UnitType.SWORDSMAN, onActionSuccess));
        popupMenu.add(createUnitItem(gameSession, UnitType.ARCHER, onActionSuccess));
        popupMenu.add(createUnitItem(gameSession, UnitType.CAVALRY, onActionSuccess));
        popupMenu.add(createUnitItem(gameSession, UnitType.CATAPULT, onActionSuccess));
    }

    private JMenuItem createUnitItem(GameSession gameSession, UnitType unitType, Runnable onActionSuccess) {
        int woodCost = UnitProductionCostProvider.getWoodCost(unitType);
        int foodCost = UnitProductionCostProvider.getFoodCost(unitType);
        int turns = UnitProductionCostProvider.getDurationInTurns(unitType);

        String detailText = "Wood: " + woodCost + "   Food: " + foodCost + "   " + turns + " turns";
        if (MilitaryUnitCapProvider.isMilitaryUnitType(unitType)) {
            int cap = MilitaryUnitCapProvider.getCap(gameSession.getTownHall().getLevel());
            detailText = detailText + "   Military: " + gameSession.countActiveMilitaryUnits() + "/" + cap;
        }
        String reason = buildUnitUnavailabilityReason(gameSession, unitType, woodCost, foodCost);

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

    private String buildUnitUnavailabilityReason(GameSession gameSession, UnitType unitType, int woodCost, int foodCost) {
        TownHallProductionQueue queue = gameSession.getTownHallProductionQueue();
        if (queue.isBusy()) {
            return "Production queue is busy";
        }

        if (MilitaryUnitCapProvider.isMilitaryUnitType(unitType)) {
            if (gameSession.hasReachedMilitaryUnitCap()) {
                return "Military unit cap reached";
            }
            String prerequisiteReason = buildMilitaryPrerequisiteReason(gameSession, unitType);
            if (!prerequisiteReason.isEmpty()) {
                return prerequisiteReason;
            }
        } else if (gameSession.hasReachedUnitCap()) {
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

    private String buildMilitaryPrerequisiteReason(GameSession gameSession, UnitType unitType) {
        if (unitType == UnitType.ARCHER) {
            if (!gameSession.getTownHallLevelService().isAtLeastLevel(gameSession, TownHallLevel.SETTLEMENT)) {
                return "Requires Town Hall level 2";
            }
        }
        if (unitType == UnitType.CAVALRY) {
            if (!gameSession.getTownHallLevelService().isAtLeastLevel(gameSession, TownHallLevel.SETTLEMENT)) {
                return "Requires Town Hall level 2";
            }
            if (!gameSession.hasActiveStable()) {
                return "Requires an active Stable";
            }
        }
        if (unitType == UnitType.CATAPULT) {
            if (!gameSession.getTownHallLevelService().isAtLeastLevel(gameSession, TownHallLevel.CAPITAL)) {
                return "Requires Town Hall level 3";
            }
        }
        return "";
    }

    private void addUpgradeItems(JPopupMenu popupMenu, GameSession gameSession, Runnable onActionSuccess) {
        popupMenu.add(createUpgradeItem(gameSession, TownHallUpgradeType.STORAGE_UPGRADE, onActionSuccess));
        popupMenu.add(createUpgradeItem(gameSession, TownHallUpgradeType.STONE_MINE_TECH, onActionSuccess));
        popupMenu.add(createUpgradeItem(gameSession, TownHallUpgradeType.IRON_MINE_TECH, onActionSuccess));
        popupMenu.add(createUpgradeItem(gameSession, TownHallUpgradeType.PROFESSIONAL_TOOLS_TECH, onActionSuccess));
        popupMenu.add(createUpgradeItem(gameSession, TownHallUpgradeType.SETTLEMENT_TECH, onActionSuccess));
        popupMenu.add(createUpgradeItem(gameSession, TownHallUpgradeType.NAVAL_TECHNIQUES, onActionSuccess));
        popupMenu.add(createUpgradeItem(gameSession, TownHallUpgradeType.DEFENSIVE_ARCHITECTURE, onActionSuccess));
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
        if (!upgradeService.isPrerequisiteMet(gameSession, upgradeType)) {
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

    private void addLevelUpItem(JPopupMenu popupMenu, GameSession gameSession, Runnable onActionSuccess) {
        popupMenu.add(createLevelUpItem(gameSession, onActionSuccess));
    }

    private JMenuItem createLevelUpItem(GameSession gameSession, Runnable onActionSuccess) {
        TownHallLevel nextLevel = gameSession.getTownHallLevelService().getNextLevel(gameSession);

        if (nextLevel == null) {
            return StyledMenuItemFactory.createItem(null, "Upgrade Town Hall Level", "Town Hall is already at maximum level", "Maximum level reached", false, null);
        }

        int woodCost = TownHallLevelCostProvider.getWoodCost(nextLevel);
        int stoneCost = TownHallLevelCostProvider.getStoneCost(nextLevel);
        int ironCost = TownHallLevelCostProvider.getIronCost(nextLevel);
        int turns = TownHallLevelCostProvider.getDurationInTurns(nextLevel);

        String title = "Upgrade Town Hall -> " + nextLevel.name();
        String detailText = "Wood:" + woodCost + " Stone:" + stoneCost + " Iron:" + ironCost + "   " + turns + " turns";
        String reason = buildLevelUpUnavailabilityReason(gameSession, woodCost, stoneCost, ironCost);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean started = gameSession.getTownHallProductionQueue().startCommand(new TownHallLevelUpCommand(nextLevel, turns));

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

    private String buildLevelUpUnavailabilityReason(GameSession gameSession, int woodCost, int stoneCost, int ironCost) {
        TownHallProductionQueue queue = gameSession.getTownHallProductionQueue();
        if (queue.isBusy()) {
            return "Production queue is busy";
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