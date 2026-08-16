package civgame.model.tribe;

import civgame.util.Constants;

public class TribeMissionDefinitionProvider {

    private TribeMissionDefinitionProvider() {
    }

    public static String getTitle(TribeType tribeType) {
        if (tribeType == TribeType.FARMER) {
            return "Help Build a Granary";
        }
        if (tribeType == TribeType.TRADER) {
            return "Connect a Trade Route";
        }
        if (tribeType == TribeType.WARRIOR) {
            return "Military Aid";
        }
        if (tribeType == TribeType.MOUNTAIN) {
            return "Mining Tools";
        }
        return "Coastal Development";
    }

    public static String getDescription(TribeType tribeType) {
        if (tribeType == TribeType.FARMER) {
            return "Pay " + Constants.FARMER_MISSION_WOOD_COST + " Wood and " + Constants.FARMER_MISSION_STONE_COST + " Stone to the tribe.";
        }
        if (tribeType == TribeType.TRADER) {
            return "Build a continuous road from one of your buildings to a hex next to their camp.";
        }
        if (tribeType == TribeType.WARRIOR) {
            return "Defeat " + Constants.WARRIOR_MISSION_REQUIRED_KILLS + " enemy or barbarian units within " + Constants.WARRIOR_MISSION_RADIUS + " hexes of their camp.";
        }
        if (tribeType == TribeType.MOUNTAIN) {
            return "Pay " + Constants.MOUNTAIN_MISSION_WOOD_COST + " Wood and " + Constants.MOUNTAIN_MISSION_IRON_COST + " Iron to the tribe.";
        }
        return "Build a Dock within " + Constants.COASTAL_MISSION_DOCK_RADIUS + " hexes of their camp.";
    }

    public static int getDeadlineTurns(TribeType tribeType) {
        if (tribeType == TribeType.FARMER) {
            return Constants.FARMER_MISSION_DEADLINE_TURNS;
        }
        if (tribeType == TribeType.TRADER) {
            return Constants.TRADER_MISSION_DEADLINE_TURNS;
        }
        if (tribeType == TribeType.WARRIOR) {
            return Constants.WARRIOR_MISSION_DEADLINE_TURNS;
        }
        if (tribeType == TribeType.MOUNTAIN) {
            return Constants.MOUNTAIN_MISSION_DEADLINE_TURNS;
        }
        return Constants.COASTAL_MISSION_DEADLINE_TURNS;
    }
}