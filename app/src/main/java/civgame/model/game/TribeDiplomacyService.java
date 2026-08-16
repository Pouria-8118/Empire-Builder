package civgame.model.game;

import civgame.model.resource.ResourceType;
import civgame.model.tribe.Tribe;
import civgame.model.tribe.TribeAllianceRules;
import civgame.util.Constants;

public class TribeDiplomacyService {

    private final TribeMissionService tribeMissionService = new TribeMissionService();

    public boolean declareWar(GameSession gameSession, Tribe tribe) {
        if (tribe.getRelationState().isAtWar()) {
            return false;
        }

        boolean wasAllied = tribe.isAllied();
        boolean wasFriendlyOrAllied = tribe.getRelationState().isTradeAllowed();

        tribe.setAllied(false);
        tribe.setRelationshipValue(-100);
        tribeMissionService.cancelMissionDueToWar(tribe);

        if (wasAllied) {
            gameSession.getHappinessService().applyOneTimeDelta(Constants.TRIBE_WAR_ON_ALLY_HAPPINESS_PENALTY);
        } else if (wasFriendlyOrAllied) {
            gameSession.getHappinessService().applyOneTimeDelta(Constants.TRIBE_WAR_ON_FRIENDLY_HAPPINESS_PENALTY);
        }

        return true;
    }

    public boolean canRequestPeace(GameSession gameSession, Tribe tribe) {
        if (!tribe.getRelationState().isAtWar()) {
            return false;
        }
        return gameSession.getResourceStorage().hasEnough(ResourceType.FOOD, Constants.TRIBE_PEACE_FOOD_COST)
                && gameSession.getResourceStorage().hasEnough(ResourceType.WOOD, Constants.TRIBE_PEACE_WOOD_COST)
                && gameSession.getResourceStorage().hasEnough(ResourceType.IRON, Constants.TRIBE_PEACE_IRON_COST);
    }

    public boolean requestPeace(GameSession gameSession, Tribe tribe) {
        if (!canRequestPeace(gameSession, tribe)) {
            return false;
        }

        gameSession.getResourceStorage().removeResource(ResourceType.FOOD, Constants.TRIBE_PEACE_FOOD_COST);
        gameSession.getResourceStorage().removeResource(ResourceType.WOOD, Constants.TRIBE_PEACE_WOOD_COST);
        gameSession.getResourceStorage().removeResource(ResourceType.IRON, Constants.TRIBE_PEACE_IRON_COST);

        tribe.setRelationshipValue(Constants.TRIBE_PEACE_RESULTING_RELATIONSHIP);

        return true;
    }

    // TODO: also require no failed mission from this tribe in the last 5 turns.
    public boolean canRequestAlliance(GameSession gameSession, Tribe tribe) {
        if (tribe.getRelationState().isAtWar()) {
            return false;
        }
        if (tribe.getRelationshipValue() < Constants.TRIBE_ALLIANCE_MIN_RELATIONSHIP) {
            return false;
        }
        return TribeAllianceRules.canFormAlliance(gameSession.getTribes(), tribe);
    }

    public boolean requestAlliance(GameSession gameSession, Tribe tribe) {
        if (!canRequestAlliance(gameSession, tribe)) {
            return false;
        }

        tribe.setAllied(true);
        return true;
    }
}