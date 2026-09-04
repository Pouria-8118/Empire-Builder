package civgame.server.trade;

import civgame.model.resource.ResourceStorage;
import civgame.model.resource.ResourceType;
import civgame.model.trade.LockedResources;
import civgame.model.trade.TradeOffer;

import java.util.Map;

public class TradeService {

    private final LockedResources lockedResources;

    public TradeService() {
        this.lockedResources = new LockedResources();
    }

    public boolean validateOffer(ResourceStorage senderStorage,
                                 Map<ResourceType, Integer> offered,
                                 Map<ResourceType, Integer> requested) {
        if (offered.isEmpty() || requested.isEmpty()) {
            return false;
        }
        for (ResourceType type : offered.keySet()) {
            int amount = offered.get(type);
            if (amount <= 0) {
                return false;
            }
            if (!senderStorage.hasEnough(type, amount)) {
                return false;
            }
        }
        return true;
    }

    public boolean lockOfferedResources(ResourceStorage senderStorage, TradeOffer offer) {
        Map<ResourceType, Integer> offered = offer.getOfferedResources();
        for (ResourceType type : offered.keySet()) {
            int amount = offered.get(type);
            if (!senderStorage.hasEnough(type, amount)) {
                return false;
            }
        }
        for (ResourceType type : offered.keySet()) {
            int amount = offered.get(type);
            senderStorage.removeResource(type, amount);
        }
        lockedResources.lockResources(offer.getId(), offered);
        return true;
    }

    public boolean acceptTrade(ResourceStorage senderStorage,
                               ResourceStorage receiverStorage,
                               TradeOffer offer) {
        Map<ResourceType, Integer> requested = offer.getRequestedResources();
        for (ResourceType type : requested.keySet()) {
            int amount = requested.get(type);
            if (!receiverStorage.hasEnough(type, amount)) {
                return false;
            }
        }

        for (ResourceType type : requested.keySet()) {
            int amount = requested.get(type);
            receiverStorage.removeResource(type, amount);
            senderStorage.addResource(type, amount);
        }

        Map<ResourceType, Integer> offered = offer.getOfferedResources();
        for (ResourceType type : offered.keySet()) {
            int amount = offered.get(type);
            receiverStorage.addResource(type, amount);
        }

        lockedResources.releaseLock(offer.getId());
        return true;
    }

    public void cancelOffer(ResourceStorage senderStorage, TradeOffer offer) {
        Map<ResourceType, Integer> locked = lockedResources.releaseLock(offer.getId());
        if (locked != null) {
            for (ResourceType type : locked.keySet()) {
                int amount = locked.get(type);
                senderStorage.addResource(type, amount);
            }
        }
    }

    public LockedResources getLockedResources() {
        return lockedResources;
    }
}