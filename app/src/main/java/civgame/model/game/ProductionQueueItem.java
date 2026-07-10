package civgame.model.game;

import civgame.model.building.TownHallUpgradeType;
import civgame.model.unit.UnitType;

public class ProductionQueueItem {

    private final QueueItemCategory category;
    private final UnitType unitType;
    private final TownHallUpgradeType upgradeType;
    private final int totalTurns;
    private int turnsRemaining;

    private ProductionQueueItem(QueueItemCategory category, UnitType unitType, TownHallUpgradeType upgradeType, int totalTurns) {
        this.category = category;
        this.unitType = unitType;
        this.upgradeType = upgradeType;
        this.totalTurns = totalTurns;
        this.turnsRemaining = totalTurns;
    }

    public static ProductionQueueItem forUnit(UnitType unitType, int totalTurns) {
        return new ProductionQueueItem(QueueItemCategory.UNIT, unitType, null, totalTurns);
    }

    public static ProductionQueueItem forUpgrade(TownHallUpgradeType upgradeType, int totalTurns) {
        return new ProductionQueueItem(QueueItemCategory.UPGRADE, null, upgradeType, totalTurns);
    }

    public QueueItemCategory getCategory() {
        return category;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public TownHallUpgradeType getUpgradeType() {
        return upgradeType;
    }

    public int getTotalTurns() {
        return totalTurns;
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    public void advanceOneTurn() {
        if (turnsRemaining > 0) {
            turnsRemaining = turnsRemaining - 1;
        }
    }

    public boolean isFinished() {
        return turnsRemaining <= 0;
    }

    public String describeForHud() {
        if (category == QueueItemCategory.UNIT) {
            return unitType.name() + " - " + turnsRemaining + " turns left";
        }
        return upgradeType.name() + " - " + turnsRemaining + " turns left";
    }

    public enum QueueItemCategory {
        UNIT,
        UPGRADE
    }
}