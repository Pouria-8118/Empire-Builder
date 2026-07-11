package civgame.model.game;

import java.util.List;

public class TurnSummary {

    private final List<String> destroyedBuildingIds;
    private final boolean starvationTriggered;
    private final int newTurnNumber;
    private final String completedProductionDescription;

    public TurnSummary(List<String> destroyedBuildingIds, boolean starvationTriggered, int newTurnNumber, String completedProductionDescription) {
        this.destroyedBuildingIds = destroyedBuildingIds;
        this.starvationTriggered = starvationTriggered;
        this.newTurnNumber = newTurnNumber;
        this.completedProductionDescription = completedProductionDescription;
    }

    public List<String> getDestroyedBuildingIds() {
        return destroyedBuildingIds;
    }

    public boolean isStarvationTriggered() {
        return starvationTriggered;
    }

    public int getNewTurnNumber() {
        return newTurnNumber;
    }

    public String getCompletedProductionDescription() {
        return completedProductionDescription;
    }
}