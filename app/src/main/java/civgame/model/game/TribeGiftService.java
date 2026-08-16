package civgame.model.game;

import civgame.model.resource.ResourceType;
import civgame.model.tribe.Tribe;
import civgame.util.Constants;

public class TribeGiftService {

    public boolean canSendGift(GameSession gameSession, Tribe tribe, ResourceType resourceType, int amount) {
        if (amount <= 0) {
            return false;
        }
        if (tribe.getRelationState().isAtWar()) {
            return false;
        }
        return gameSession.getResourceStorage().hasEnough(resourceType, amount);
    }

    public boolean sendGift(GameSession gameSession, Tribe tribe, ResourceType resourceType, int amount) {
        if (!canSendGift(gameSession, tribe, resourceType, amount)) {
            return false;
        }

        gameSession.getResourceStorage().removeResource(resourceType, amount);
        int relationshipGain = calculateRelationshipGain(resourceType, amount);
        tribe.changeRelationship(relationshipGain);

        return true;
    }

    private int calculateRelationshipGain(ResourceType resourceType, int amount) {
        if (resourceType == ResourceType.FOOD || resourceType == ResourceType.WOOD) {
            return (amount / 10) * Constants.TRIBE_GIFT_FOOD_WOOD_RELATIONSHIP_PER_UNIT;
        }
        if (resourceType == ResourceType.STONE) {
            return (amount / 10) * Constants.TRIBE_GIFT_STONE_RELATIONSHIP_PER_UNIT;
        }
        if (resourceType == ResourceType.IRON) {
            return (amount / 5) * Constants.TRIBE_GIFT_IRON_RELATIONSHIP_PER_UNIT;
        }
        return 0;
    }
}