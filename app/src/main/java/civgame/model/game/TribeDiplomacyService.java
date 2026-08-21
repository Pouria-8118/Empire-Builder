package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.resource.ResourceType;
import civgame.model.tribe.Tribe;
import civgame.model.tribe.TribeAllianceRules;
import civgame.model.tribe.TribeType;
import civgame.model.unit.UnitFactory;
import civgame.model.unit.UnitType;
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

    public boolean canRequestAlliance(GameSession gameSession, Tribe tribe) {
        if (tribe.getRelationState().isAtWar()) {
            return false;
        }
        if (tribe.getRelationshipValue() < Constants.TRIBE_ALLIANCE_MIN_RELATIONSHIP) {
            return false;
        }
        if (gameSession.getCurrentTurnNumber() - tribe.getLastMissionFailureTurn() < Constants.MISSION_FAILURE_COOLDOWN_TURNS) {
            return false;
        }
        return TribeAllianceRules.canFormAlliance(gameSession.getTribes(), tribe);
    }

    public boolean requestAlliance(GameSession gameSession, Tribe tribe) {
        if (!canRequestAlliance(gameSession, tribe)) {
            return false;
        }

        tribe.setAllied(true);
        applyAllianceSigningBonus(gameSession, tribe);

        return true;
    }

    private void applyAllianceSigningBonus(GameSession gameSession, Tribe tribe) {
        if (tribe.getTribeType() == TribeType.WARRIOR) {
            MilitarySpawnFinder spawnFinder = new MilitarySpawnFinder();
            UnitFactory unitFactory = new UnitFactory();
            HexCoordinate spawnCoordinate = spawnFinder.findSpawnCoordinate(gameSession, UnitType.SWORDSMAN);
            gameSession.addUnit(unitFactory.createUnit(UnitType.SWORDSMAN, spawnCoordinate));
        } else if (tribe.getTribeType() == TribeType.TRADER) {
            tribe.setTradeRateBonus(tribe.getTradeRateBonus() + Constants.TRADER_ALLY_TRADE_RATE_BONUS);
        }
    }
}