package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.building.BuildingType;
import civgame.model.map.HexCoordinate;
import civgame.model.resource.ResourceType;
import civgame.model.tribe.MissionStatus;
import civgame.model.tribe.Tribe;
import civgame.model.tribe.TribeMission;
import civgame.model.tribe.TribeMissionDefinitionProvider;
import civgame.model.tribe.TribeType;
import civgame.model.unit.UnitFactory;
import civgame.model.unit.UnitType;
import civgame.util.Constants;

import java.util.List;

public class TribeMissionService {

    public boolean canAcceptMission(GameSession gameSession, Tribe tribe) {
        if (tribe.getRelationState().isAtWar()) {
            return false;
        }
        if (tribe.getRelationshipValue() < Constants.TRIBE_MISSION_MIN_RELATIONSHIP) {
            return false;
        }
        if (tribe.getActiveMission() != null) {
            return false;
        }
        if (!tribe.isMissionOfferAvailable()) {
            return false;
        }
        return gameSession.getCurrentTurnNumber() >= tribe.getMissionCooldownUntilTurn();
    }

    public boolean acceptMission(GameSession gameSession, Tribe tribe) {
        if (!canAcceptMission(gameSession, tribe)) {
            return false;
        }

        String title = TribeMissionDefinitionProvider.getTitle(tribe.getTribeType());
        int deadlineTurn = gameSession.getCurrentTurnNumber() + TribeMissionDefinitionProvider.getDeadlineTurns(tribe.getTribeType());
        tribe.setActiveMission(new TribeMission(tribe.getTribeType(), title, deadlineTurn));
        tribe.setMissionOfferAvailable(false);

        return true;
    }

    public boolean isResourcePaymentMission(TribeType tribeType) {
        return tribeType == TribeType.FARMER || tribeType == TribeType.MOUNTAIN;
    }

    public boolean canPayResourceMissionCost(GameSession gameSession, Tribe tribe) {
        TribeMission mission = tribe.getActiveMission();
        if (mission == null || mission.getStatus() != MissionStatus.ACTIVE) {
            return false;
        }
        if (!isResourcePaymentMission(tribe.getTribeType())) {
            return false;
        }

        return gameSession.getResourceStorage().hasEnough(ResourceType.WOOD, getMissionWoodCost(tribe.getTribeType()))
                && gameSession.getResourceStorage().hasEnough(ResourceType.STONE, getMissionStoneCost(tribe.getTribeType()))
                && gameSession.getResourceStorage().hasEnough(ResourceType.IRON, getMissionIronCost(tribe.getTribeType()));
    }

    public boolean payResourceMissionCost(GameSession gameSession, Tribe tribe) {
        if (!canPayResourceMissionCost(gameSession, tribe)) {
            return false;
        }

        gameSession.getResourceStorage().removeResource(ResourceType.WOOD, getMissionWoodCost(tribe.getTribeType()));
        gameSession.getResourceStorage().removeResource(ResourceType.STONE, getMissionStoneCost(tribe.getTribeType()));
        gameSession.getResourceStorage().removeResource(ResourceType.IRON, getMissionIronCost(tribe.getTribeType()));

        tribe.getActiveMission().setStatus(MissionStatus.READY_TO_DELIVER);
        return true;
    }

    public void checkAllActiveMissionConditions(GameSession gameSession) {
        List<Tribe> tribes = gameSession.getTribes();
        for (int i = 0; i < tribes.size(); i++) {
            refreshMissionCondition(gameSession, tribes.get(i));
        }
    }

    public void refreshMissionCondition(GameSession gameSession, Tribe tribe) {
        TribeMission mission = tribe.getActiveMission();
        if (mission == null) {
            return;
        }

        if (mission.getStatus() == MissionStatus.ACTIVE) {
            if (tribe.getTribeType() == TribeType.TRADER && new TribeRoadConnectivityChecker().isConnected(gameSession, tribe.getCampCoordinate())) {
                mission.setStatus(MissionStatus.READY_TO_DELIVER);
            } else if (tribe.getTribeType() == TribeType.COASTAL && isDockNearby(gameSession, tribe)) {
                mission.setStatus(MissionStatus.READY_TO_DELIVER);
            } else if (tribe.getTribeType() == TribeType.WARRIOR && mission.getWarriorKillCount() >= Constants.WARRIOR_MISSION_REQUIRED_KILLS) {
                mission.setStatus(MissionStatus.READY_TO_DELIVER);
            }
        }

        if (mission.getStatus() != MissionStatus.READY_TO_DELIVER && gameSession.getCurrentTurnNumber() > mission.getDeadlineTurn()) {
            failMission(gameSession, tribe, mission);
        }
    }

    public boolean canDeliverMission(GameSession gameSession, Tribe tribe) {
        TribeMission mission = tribe.getActiveMission();
        if (mission == null || mission.getStatus() != MissionStatus.READY_TO_DELIVER) {
            return false;
        }
        return hasRoomForReward(gameSession, tribe.getTribeType());
    }

    private boolean hasRoomForReward(GameSession gameSession, TribeType tribeType) {
        if (tribeType == TribeType.FARMER) {
            return hasRoomFor(gameSession, ResourceType.FOOD, Constants.FARMER_MISSION_REWARD_FOOD);
        }
        if (tribeType == TribeType.MOUNTAIN) {
            return hasRoomFor(gameSession, ResourceType.STONE, Constants.MOUNTAIN_MISSION_REWARD_STONE);
        }
        if (tribeType == TribeType.COASTAL) {
            return hasRoomFor(gameSession, ResourceType.FOOD, Constants.COASTAL_MISSION_REWARD_FOOD);
        }
        return true;
    }

    private boolean hasRoomFor(GameSession gameSession, ResourceType resourceType, int amount) {
        int remaining = gameSession.getResourceStorage().getCapacity(resourceType) - gameSession.getResourceStorage().getAmount(resourceType);
        return remaining >= amount;
    }

    public boolean deliverMission(GameSession gameSession, Tribe tribe) {
        if (!canDeliverMission(gameSession, tribe)) {
            return false;
        }

        applyMissionReward(gameSession, tribe);
        tribe.getActiveMission().setStatus(MissionStatus.COMPLETED);
        tribe.setActiveMission(null);

        return true;
    }

    private void applyMissionReward(GameSession gameSession, Tribe tribe) {
        TribeType tribeType = tribe.getTribeType();

        if (tribeType == TribeType.FARMER) {
            gameSession.getResourceStorage().addResource(ResourceType.FOOD, Constants.FARMER_MISSION_REWARD_FOOD);
            tribe.changeRelationship(Constants.FARMER_MISSION_REWARD_RELATIONSHIP);
        } else if (tribeType == TribeType.MOUNTAIN) {
            gameSession.getResourceStorage().addResource(ResourceType.STONE, Constants.MOUNTAIN_MISSION_REWARD_STONE);
            tribe.changeRelationship(Constants.MOUNTAIN_MISSION_REWARD_RELATIONSHIP);
        } else if (tribeType == TribeType.TRADER) {
            tribe.setTradeRateBonus(tribe.getTradeRateBonus() + Constants.TRADER_MISSION_TRADE_RATE_BONUS);
            tribe.changeRelationship(Constants.TRADER_MISSION_REWARD_RELATIONSHIP);
        } else if (tribeType == TribeType.WARRIOR) {
            spawnAlliedSwordsmen(gameSession, Constants.WARRIOR_MISSION_REWARD_SWORDSMEN_COUNT);
            tribe.changeRelationship(Constants.WARRIOR_MISSION_REWARD_RELATIONSHIP);
        } else if (tribeType == TribeType.COASTAL) {
            gameSession.getResourceStorage().addResource(ResourceType.FOOD, Constants.COASTAL_MISSION_REWARD_FOOD);
            gameSession.setPendingDockDiscount(true);
        }
    }

    private void spawnAlliedSwordsmen(GameSession gameSession, int count) {
        MilitarySpawnFinder spawnFinder = new MilitarySpawnFinder();
        UnitFactory unitFactory = new UnitFactory();

        for (int i = 0; i < count; i++) {
            HexCoordinate spawnCoordinate = spawnFinder.findSpawnCoordinate(gameSession, UnitType.SWORDSMAN);
            gameSession.addUnit(unitFactory.createUnit(UnitType.SWORDSMAN, spawnCoordinate));
        }
    }

    public boolean cancelMission(GameSession gameSession, Tribe tribe) {
        TribeMission mission = tribe.getActiveMission();
        if (mission == null) {
            return false;
        }

        mission.setStatus(MissionStatus.CANCELLED);
        tribe.setActiveMission(null);
        tribe.setMissionOfferAvailable(false);
        tribe.changeRelationship(Constants.MISSION_CANCEL_RELATIONSHIP_PENALTY);

        return true;
    }

    public void cancelMissionDueToWar(Tribe tribe) {
        TribeMission mission = tribe.getActiveMission();
        if (mission == null) {
            return;
        }
        mission.setStatus(MissionStatus.CANCELLED);
        tribe.setActiveMission(null);
    }

    private void failMission(GameSession gameSession, Tribe tribe, TribeMission mission) {
        mission.setStatus(MissionStatus.FAILED);
        tribe.setActiveMission(null);
        tribe.changeRelationship(Constants.MISSION_FAILURE_RELATIONSHIP_PENALTY);
        tribe.setMissionCooldownUntilTurn(gameSession.getCurrentTurnNumber() + Constants.MISSION_FAILURE_COOLDOWN_TURNS);
    }

    public void registerWarriorMissionKill(GameSession gameSession, HexCoordinate killCoordinate) {
        List<Tribe> tribes = gameSession.getTribes();
        for (int i = 0; i < tribes.size(); i++) {
            Tribe tribe = tribes.get(i);
            TribeMission mission = tribe.getActiveMission();
            if (mission == null || tribe.getTribeType() != TribeType.WARRIOR || mission.getStatus() != MissionStatus.ACTIVE) {
                continue;
            }
            if (tribe.getCampCoordinate().distanceTo(killCoordinate) <= Constants.WARRIOR_MISSION_RADIUS) {
                mission.setWarriorKillCount(mission.getWarriorKillCount() + 1);
            }
        }
    }

    private boolean isDockNearby(GameSession gameSession, Tribe tribe) {
        List<Building> allBuildings = gameSession.getAllBuildings();
        for (int i = 0; i < allBuildings.size(); i++) {
            Building building = allBuildings.get(i);
            if (building.isDestroyed() || building.getBuildingType() != BuildingType.DOCK) {
                continue;
            }
            if (tribe.getCampCoordinate().distanceTo(building.getCoordinate()) <= Constants.COASTAL_MISSION_DOCK_RADIUS) {
                return true;
            }
        }
        return false;
    }

    private int getMissionWoodCost(TribeType tribeType) {
        if (tribeType == TribeType.FARMER) {
            return Constants.FARMER_MISSION_WOOD_COST;
        }
        if (tribeType == TribeType.MOUNTAIN) {
            return Constants.MOUNTAIN_MISSION_WOOD_COST;
        }
        return 0;
    }

    private int getMissionStoneCost(TribeType tribeType) {
        if (tribeType == TribeType.FARMER) {
            return Constants.FARMER_MISSION_STONE_COST;
        }
        return 0;
    }

    private int getMissionIronCost(TribeType tribeType) {
        if (tribeType == TribeType.MOUNTAIN) {
            return Constants.MOUNTAIN_MISSION_IRON_COST;
        }
        return 0;
    }
}