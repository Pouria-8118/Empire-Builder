package civgame.model.building;

import civgame.util.Constants;

public class TownHallUpgradeCostProvider {

    private TownHallUpgradeCostProvider() {
    }

    public static int getWoodCost(TownHallUpgradeType upgradeType) {
        if (upgradeType == TownHallUpgradeType.STORAGE_UPGRADE) {
            return Constants.STORAGE_UPGRADE_WOOD_COST;
        }
        if (upgradeType == TownHallUpgradeType.STONE_MINE_TECH) {
            return Constants.STONE_MINE_TECH_WOOD_COST;
        }
        if (upgradeType == TownHallUpgradeType.IRON_MINE_TECH) {
            return Constants.IRON_MINE_TECH_WOOD_COST;
        }
        if (upgradeType == TownHallUpgradeType.SETTLEMENT_TECH) {
            return Constants.SETTLEMENT_TECH_WOOD_COST;
        }
        return 0;
    }

    public static int getStoneCost(TownHallUpgradeType upgradeType) {
        if (upgradeType == TownHallUpgradeType.STORAGE_UPGRADE) {
            return Constants.STORAGE_UPGRADE_STONE_COST;
        }
        if (upgradeType == TownHallUpgradeType.STONE_MINE_TECH) {
            return Constants.STONE_MINE_TECH_STONE_COST;
        }
        if (upgradeType == TownHallUpgradeType.IRON_MINE_TECH) {
            return Constants.IRON_MINE_TECH_STONE_COST;
        }
        if (upgradeType == TownHallUpgradeType.PROFESSIONAL_TOOLS_TECH) {
            return Constants.PROFESSIONAL_TOOLS_TECH_STONE_COST;
        }
        return Constants.SETTLEMENT_TECH_STONE_COST;
    }

    public static int getIronCost(TownHallUpgradeType upgradeType) {
        if (upgradeType == TownHallUpgradeType.PROFESSIONAL_TOOLS_TECH) {
            return Constants.PROFESSIONAL_TOOLS_TECH_IRON_COST;
        }
        if (upgradeType == TownHallUpgradeType.SETTLEMENT_TECH) {
            return Constants.SETTLEMENT_TECH_IRON_COST;
        }
        return 0;
    }

    public static int getDurationInTurns(TownHallUpgradeType upgradeType) {
        if (upgradeType == TownHallUpgradeType.STORAGE_UPGRADE) {
            return Constants.STORAGE_UPGRADE_TURNS;
        }
        if (upgradeType == TownHallUpgradeType.STONE_MINE_TECH) {
            return Constants.STONE_MINE_TECH_TURNS;
        }
        if (upgradeType == TownHallUpgradeType.IRON_MINE_TECH) {
            return Constants.IRON_MINE_TECH_TURNS;
        }
        if (upgradeType == TownHallUpgradeType.PROFESSIONAL_TOOLS_TECH) {
            return Constants.PROFESSIONAL_TOOLS_TECH_TURNS;
        }
        return Constants.SETTLEMENT_TECH_TURNS;
    }
}