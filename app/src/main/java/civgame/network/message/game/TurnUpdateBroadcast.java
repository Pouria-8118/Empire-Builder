package civgame.network.message.game;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;

import java.util.ArrayList;
import java.util.List;

public class TurnUpdateBroadcast extends Broadcast {

    private int newTurnNumber;
    private String currentTurnClientId;
    private String currentTurnUsername;
    private TurnSummaryDTO summary;
    private ResourceSnapshotDTO resources;
    private ResourceSnapshotDTO resourcesBeforeTurn;

    public TurnUpdateBroadcast() {
        super();
        setType(MessageType.TURN_UPDATE_BROADCAST);
    }

    public TurnUpdateBroadcast(String broadcasterId) {
        super(MessageType.TURN_UPDATE_BROADCAST, broadcasterId);
    }

    public int getNewTurnNumber() {
        return newTurnNumber;
    }

    public void setNewTurnNumber(int newTurnNumber) {
        this.newTurnNumber = newTurnNumber;
    }

    public String getCurrentTurnClientId() {
        return currentTurnClientId;
    }

    public void setCurrentTurnClientId(String currentTurnClientId) {
        this.currentTurnClientId = currentTurnClientId;
    }

    public String getCurrentTurnUsername() {
        return currentTurnUsername;
    }

    public void setCurrentTurnUsername(String currentTurnUsername) {
        this.currentTurnUsername = currentTurnUsername;
    }

    public TurnSummaryDTO getSummary() {
        return summary;
    }

    public void setSummary(TurnSummaryDTO summary) {
        this.summary = summary;
    }

    public ResourceSnapshotDTO getResources() {
        return resources;
    }

    public void setResources(ResourceSnapshotDTO resources) {
        this.resources = resources;
    }

    public ResourceSnapshotDTO getResourcesBeforeTurn() {
        return resourcesBeforeTurn;
    }

    public void setResourcesBeforeTurn(ResourceSnapshotDTO resourcesBeforeTurn) {
        this.resourcesBeforeTurn = resourcesBeforeTurn;
    }

    public static class TurnSummaryDTO {
        private List<String> destroyedBuildingIds;
        private boolean starvationTriggered;
        private String completedProductionDescription;
        private List<String> depletedResourceDescriptions;

        public TurnSummaryDTO() {
            this.destroyedBuildingIds = new ArrayList<>();
            this.depletedResourceDescriptions = new ArrayList<>();
        }

        public List<String> getDestroyedBuildingIds() {
            return destroyedBuildingIds;
        }

        public void setDestroyedBuildingIds(List<String> destroyedBuildingIds) {
            this.destroyedBuildingIds = destroyedBuildingIds;
        }

        public boolean isStarvationTriggered() {
            return starvationTriggered;
        }

        public void setStarvationTriggered(boolean starvationTriggered) {
            this.starvationTriggered = starvationTriggered;
        }

        public String getCompletedProductionDescription() {
            return completedProductionDescription;
        }

        public void setCompletedProductionDescription(String completedProductionDescription) {
            this.completedProductionDescription = completedProductionDescription;
        }

        public List<String> getDepletedResourceDescriptions() {
            return depletedResourceDescriptions;
        }

        public void setDepletedResourceDescriptions(List<String> depletedResourceDescriptions) {
            this.depletedResourceDescriptions = depletedResourceDescriptions;
        }
    }

    public static class ResourceSnapshotDTO {
        private int food;
        private int wood;
        private int stone;
        private int iron;

        public ResourceSnapshotDTO() {
        }

        public int getFood() {
            return food;
        }

        public void setFood(int food) {
            this.food = food;
        }

        public int getWood() {
            return wood;
        }

        public void setWood(int wood) {
            this.wood = wood;
        }

        public int getStone() {
            return stone;
        }

        public void setStone(int stone) {
            this.stone = stone;
        }

        public int getIron() {
            return iron;
        }

        public void setIron(int iron) {
            this.iron = iron;
        }
    }
}