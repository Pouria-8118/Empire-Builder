package civgame.model.game;

import civgame.model.building.BuildingType;
import civgame.model.building.TownHallLevel;
import civgame.model.building.TownHallUpgradeType;
import civgame.model.map.HexCoordinate;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;

import java.util.HashSet;
import java.util.Set;

public class TownHallUpgradeService implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final Set<TownHallUpgradeType> unlockedUpgrades;
    private int storageUpgradeCount;

    public TownHallUpgradeService() {
        this.unlockedUpgrades = new HashSet<TownHallUpgradeType>();
        this.storageUpgradeCount = 0;
    }

    public boolean isUnlocked(TownHallUpgradeType upgradeType) {
        return unlockedUpgrades.contains(upgradeType);
    }

    public int getStorageUpgradeCount() {
        return storageUpgradeCount;
    }

    public boolean isPrerequisiteMet(GameSession gameSession, TownHallUpgradeType upgradeType) {
        if (upgradeType == TownHallUpgradeType.IRON_MINE_TECH) {
            return isUnlocked(TownHallUpgradeType.STONE_MINE_TECH);
        }
        if (upgradeType == TownHallUpgradeType.PROFESSIONAL_TOOLS_TECH) {
            boolean minesUnlocked = isUnlocked(TownHallUpgradeType.STONE_MINE_TECH) && isUnlocked(TownHallUpgradeType.IRON_MINE_TECH);
            boolean levelMet = gameSession.getTownHallLevelService().isAtLeastLevel(gameSession, TownHallLevel.SETTLEMENT);
            return minesUnlocked && levelMet;
        }
        if (upgradeType == TownHallUpgradeType.NAVAL_TECHNIQUES) {
            return gameSession.getTownHallLevelService().isAtLeastLevel(gameSession, TownHallLevel.SETTLEMENT);
        }
        if (upgradeType == TownHallUpgradeType.DEFENSIVE_ARCHITECTURE) {
            return gameSession.getTownHallLevelService().isAtLeastLevel(gameSession, TownHallLevel.CAPITAL);
        }
        return true;
    }

    public boolean isAlreadyMaxed(TownHallUpgradeType upgradeType) {
        if (upgradeType == TownHallUpgradeType.STORAGE_UPGRADE) {
            return storageUpgradeCount >= Constants.STORAGE_UPGRADE_MAX_COUNT;
        }
        return isUnlocked(upgradeType);
    }

    public void applyUpgradeEffects(GameSession gameSession, TownHallUpgradeType upgradeType) {
        if (upgradeType == TownHallUpgradeType.STORAGE_UPGRADE) {
            applyStorageUpgrade(gameSession);
            return;
        }
        if (upgradeType == TownHallUpgradeType.SETTLEMENT_TECH) {
            unlockedUpgrades.add(upgradeType);
            applySettlementUpgrade(gameSession);
            return;
        }
        if (upgradeType == TownHallUpgradeType.DEFENSIVE_ARCHITECTURE) {
            unlockedUpgrades.add(upgradeType);
            applyDefensiveArchitectureUpgrade(gameSession);
            return;
        }

        unlockedUpgrades.add(upgradeType);
    }

    private void applyStorageUpgrade(GameSession gameSession) {
        storageUpgradeCount = storageUpgradeCount + 1;
        gameSession.getResourceStorage().increaseCapacity(ResourceType.FOOD, Constants.STORAGE_UPGRADE_CAPACITY_BONUS);
        gameSession.getResourceStorage().increaseCapacity(ResourceType.WOOD, Constants.STORAGE_UPGRADE_CAPACITY_BONUS);
        gameSession.getResourceStorage().increaseCapacity(ResourceType.STONE, Constants.STORAGE_UPGRADE_CAPACITY_BONUS);
        gameSession.getResourceStorage().increaseCapacity(ResourceType.IRON, Constants.STORAGE_UPGRADE_CAPACITY_BONUS);
    }

    private void applySettlementUpgrade(GameSession gameSession) {
        gameSession.increaseUnitCap(Constants.SETTLEMENT_UNIT_CAP_BONUS);

        SettlementEligibilityChecker eligibilityChecker = new SettlementEligibilityChecker();
        HexCoordinate eligibleCoordinate = eligibilityChecker.findEligibleTile(gameSession);

        if (eligibleCoordinate != null) {
            gameSession.addSettlementTile(eligibleCoordinate);
            gameSession.getHappinessService().applyOneTimeDelta(Constants.NEW_SETTLEMENT_HAPPINESS_PENALTY);
        }
    }

    private void applyDefensiveArchitectureUpgrade(GameSession gameSession) {
        gameSession.getTownHall().setDefense(Constants.DEFENSIVE_ARCHITECTURE_DEFENSE_VALUE);
        gameSession.getTownHall().setMaxHitPoints(Constants.DEFENSIVE_ARCHITECTURE_MAX_HIT_POINTS);

        // TODO: once the Wall model exists, automatically build a stone Wall
        // on every edge hex around the Town Hall's coordinate here.
    }

    public double getProductionMultiplier(BuildingType buildingType) {
        boolean toolsUnlocked = isUnlocked(TownHallUpgradeType.PROFESSIONAL_TOOLS_TECH);
        boolean isStoneOrIron = buildingType == BuildingType.STONE_MINE || buildingType == BuildingType.IRON_MINE;

        if (toolsUnlocked && isStoneOrIron) {
            return Constants.PROFESSIONAL_TOOLS_PRODUCTION_MULTIPLIER;
        }
        return 1.0;
    }
}