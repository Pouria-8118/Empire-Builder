package civgame.model.game;

import civgame.model.building.TownHallLevel;
import civgame.model.building.TownHallLevelCostProvider;
import civgame.model.resource.ResourceType;
import civgame.util.Constants;

public class TownHallLevelService {

    public boolean canLevelUp(GameSession gameSession) {
        return getNextLevel(gameSession) != null;
    }

    public TownHallLevel getNextLevel(GameSession gameSession) {
        TownHallLevel currentLevel = gameSession.getTownHall().getLevel();
        TownHallLevel[] allLevels = TownHallLevel.values();
        int nextOrdinal = currentLevel.ordinal() + 1;

        if (nextOrdinal >= allLevels.length) {
            return null;
        }

        return allLevels[nextOrdinal];
    }

    public boolean isAtLeastLevel(GameSession gameSession, TownHallLevel requiredLevel) {
        return gameSession.getTownHall().isAtLeastLevel(requiredLevel);
    }

    public void applyLevelUpEffects(GameSession gameSession, TownHallLevel targetLevel) {
        gameSession.getTownHall().setLevel(targetLevel);

        int storageBonus = TownHallLevelCostProvider.getStorageCapacityBonus(targetLevel);
        gameSession.getResourceStorage().increaseCapacity(ResourceType.FOOD, storageBonus);
        gameSession.getResourceStorage().increaseCapacity(ResourceType.WOOD, storageBonus);
        gameSession.getResourceStorage().increaseCapacity(ResourceType.STONE, storageBonus);
        gameSession.getResourceStorage().increaseCapacity(ResourceType.IRON, storageBonus);

        if (targetLevel == TownHallLevel.SETTLEMENT) {
            gameSession.getTownHall().heal(Constants.TOWN_HALL_LEVEL_2_HEAL_AMOUNT);
        }
    }
}