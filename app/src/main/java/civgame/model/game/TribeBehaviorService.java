package civgame.model.game;

import civgame.model.resource.ResourceType;
import civgame.model.tribe.Tribe;
import civgame.model.tribe.TribeGuardUnit;
import civgame.model.tribe.TribeType;
import civgame.model.unit.MilitaryUnitCapProvider;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;
import civgame.util.Constants;

import java.util.List;

public class TribeBehaviorService {

    public void processTribeTurns(GameSession gameSession) {
        gameSession.clearTribeNotifications();

        List<Tribe> tribes = gameSession.getTribes();
        for (int i = 0; i < tribes.size(); i++) {
            processSingleTribe(gameSession, tribes.get(i));
        }
    }

    private void processSingleTribe(GameSession gameSession, Tribe tribe) {
        if (tribe.isDefeated()) {
            return;
        }

        boolean militaryUnitInZone = hasPlayerMilitaryUnitInZone(gameSession, tribe);

        if (tribe.getRelationState().isAtWar()) {
            processEnemyBehavior(gameSession, tribe);
            return;
        }

        if (tribe.getRelationshipValue() <= Constants.TRIBE_DISPLEASED_MAX_RELATIONSHIP) {
            processDispleasedBehavior(gameSession, tribe, militaryUnitInZone);
            return;
        }

        if (tribe.getRelationshipValue() <= Constants.TRIBE_NEUTRAL_MAX_RELATIONSHIP) {
            processNeutralBehavior(gameSession, tribe, militaryUnitInZone);
            return;
        }

        processFriendlyOrAlliedBehavior(gameSession, tribe);
    }

    private boolean hasPlayerMilitaryUnitInZone(GameSession gameSession, Tribe tribe) {
        List<Unit> units = gameSession.getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (!MilitaryUnitCapProvider.isMilitaryUnitType(unit.getUnitType())) {
                continue;
            }
            if (tribe.getCampCoordinate().distanceTo(unit.getCoordinate()) <= 1) {
                return true;
            }
        }
        return false;
    }

    private void processNeutralBehavior(GameSession gameSession, Tribe tribe, boolean militaryUnitInZone) {
        if (!militaryUnitInZone) {
            tribe.setZoneWarningIssued(false);
            return;
        }

        if (!tribe.isZoneWarningIssued()) {
            tribe.setZoneWarningIssued(true);
            gameSession.addTribeNotification(tribe.getName() + " warns you about your military presence near their camp.");
            return;
        }

        tribe.changeRelationship(Constants.TRIBE_ZONE_INTRUSION_NEUTRAL_PENALTY);
        gameSession.addTribeNotification(tribe.getName() + " is upset by your continued military presence.");
    }

    private void processDispleasedBehavior(GameSession gameSession, Tribe tribe, boolean militaryUnitInZone) {
        if (militaryUnitInZone) {
            tribe.changeRelationship(Constants.TRIBE_ZONE_INTRUSION_DISPLEASED_PENALTY);
        }

        if (tribe.getRelationshipValue() <= Constants.TRIBE_ENEMY_MAX_RELATIONSHIP) {
            gameSession.addTribeNotification(tribe.getName() + " has become hostile toward you.");
        }
    }

    private void processFriendlyOrAlliedBehavior(GameSession gameSession, Tribe tribe) {
        if (tribe.isAllied()) {
            applyAlliedPerTurnBonus(gameSession, tribe);
        }

        if (tribe.getActiveMission() == null && !tribe.isMissionOfferAvailable()
                && gameSession.getCurrentTurnNumber() >= tribe.getNextMissionCheckTurn()) {
            tribe.setMissionOfferAvailable(true);
            tribe.setNextMissionCheckTurn(gameSession.getCurrentTurnNumber() + Constants.TRIBE_MISSION_OFFER_INTERVAL_TURNS);
            gameSession.addTribeNotification(tribe.getName() + " has a new mission available.");
        }
    }

    private void applyAlliedPerTurnBonus(GameSession gameSession, Tribe tribe) {
        if (tribe.getTribeType() == TribeType.FARMER) {
            gameSession.getResourceStorage().addResource(ResourceType.FOOD, Constants.FARMER_ALLY_FOOD_BONUS_PER_TURN);
        } else if (tribe.getTribeType() == TribeType.MOUNTAIN) {
            gameSession.getResourceStorage().addResource(ResourceType.STONE, Constants.MOUNTAIN_ALLY_STONE_BONUS_PER_TURN);
            gameSession.getResourceStorage().addResource(ResourceType.IRON, Constants.MOUNTAIN_ALLY_IRON_BONUS_PER_TURN);
        }
    }

    private void processEnemyBehavior(GameSession gameSession, Tribe tribe) {
        int cap = tribe.getTribeType() == TribeType.WARRIOR ? Constants.TRIBE_ENEMY_WARRIOR_GUARD_CAP : Constants.TRIBE_ENEMY_DEFAULT_GUARD_CAP;

        if (tribe.getGuards().size() >= cap) {
            return;
        }
        if (gameSession.getCurrentTurnNumber() < tribe.getNextGuardSpawnTurn()) {
            return;
        }

        tribe.getGuards().add(new TribeGuardUnit(UnitType.SWORDSMAN, Constants.TRIBE_GUARD_SWORDSMAN_HIT_POINTS));
        tribe.setNextGuardSpawnTurn(gameSession.getCurrentTurnNumber() + Constants.TRIBE_ENEMY_GUARD_SPAWN_INTERVAL_TURNS);
        gameSession.addTribeNotification(tribe.getName() + " has trained a new guard.");
    }
}