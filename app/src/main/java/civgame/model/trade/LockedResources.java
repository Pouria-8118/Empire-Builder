package civgame.model.trade;

import civgame.model.resource.ResourceType;

import java.util.HashMap;
import java.util.Map;

public class LockedResources implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<String, Map<ResourceType, Integer>> locksByOfferId;

    public LockedResources() {
        this.locksByOfferId = new HashMap<String, Map<ResourceType, Integer>>();
    }

    public void lockResources(String offerId, Map<ResourceType, Integer> resources) {
        locksByOfferId.put(offerId, new HashMap<ResourceType, Integer>(resources));
    }

    public Map<ResourceType, Integer> getLockedResources(String offerId) {
        return locksByOfferId.get(offerId);
    }

    public Map<ResourceType, Integer> releaseLock(String offerId) {
        return locksByOfferId.remove(offerId);
    }

    public boolean hasLock(String offerId) {
        return locksByOfferId.containsKey(offerId);
    }
}