package civgame.model.game;

import java.util.List;

public class TurnSummary {

    private final List<String> destroyedBuildingIds;
    private final boolean starvationTriggered;
    private final int newTurnNumber;

    public TurnSummary(List<String> destroyedBuildingIds, boolean starvationTriggered, int newTurnNumber) {
        this.destroyedBuildingIds = destroyedBuildingIds;
        this.starvationTriggered = starvationTriggered;
        this.newTurnNumber = newTurnNumber;
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
}