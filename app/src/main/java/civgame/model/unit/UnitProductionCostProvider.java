package civgame.model.unit;

import civgame.util.Constants;

public class UnitProductionCostProvider {

    private UnitProductionCostProvider() {
    }

    public static int getWoodCost(UnitType unitType) {
        if (unitType == UnitType.EXPLORER) {
            return Constants.EXPLORER_PRODUCTION_WOOD_COST;
        }
        if (unitType == UnitType.BUILDER) {
            return Constants.BUILDER_PRODUCTION_WOOD_COST;
        }
        if (unitType == UnitType.WORKER) {
            return Constants.WORKER_PRODUCTION_WOOD_COST;
        }
        if (unitType == UnitType.SWORDSMAN) {
            return Constants.SWORDSMAN_PRODUCTION_WOOD_COST;
        }
        if (unitType == UnitType.ARCHER) {
            return Constants.ARCHER_PRODUCTION_WOOD_COST;
        }
        if (unitType == UnitType.CAVALRY) {
            return Constants.CAVALRY_PRODUCTION_WOOD_COST;
        }
        if (unitType == UnitType.CATAPULT) {
            return Constants.CATAPULT_PRODUCTION_WOOD_COST;
        }
        if (unitType == UnitType.FISHING_BOAT) {
            return Constants.FISHING_BOAT_PRODUCTION_WOOD_COST;
        }
        if (unitType == UnitType.GALLEY) {
            return Constants.GALLEY_PRODUCTION_WOOD_COST;
        }
        return Constants.BORDER_EXPANDER_PRODUCTION_WOOD_COST;
    }

    public static int getFoodCost(UnitType unitType) {
        if (unitType == UnitType.EXPLORER) {
            return Constants.EXPLORER_PRODUCTION_FOOD_COST;
        }
        if (unitType == UnitType.BUILDER) {
            return Constants.BUILDER_PRODUCTION_FOOD_COST;
        }
        if (unitType == UnitType.WORKER) {
            return Constants.WORKER_PRODUCTION_FOOD_COST;
        }
        if (unitType == UnitType.SWORDSMAN) {
            return Constants.SWORDSMAN_PRODUCTION_FOOD_COST;
        }
        if (unitType == UnitType.ARCHER) {
            return Constants.ARCHER_PRODUCTION_FOOD_COST;
        }
        if (unitType == UnitType.CAVALRY) {
            return Constants.CAVALRY_PRODUCTION_FOOD_COST;
        }
        if (unitType == UnitType.CATAPULT) {
            return Constants.CATAPULT_PRODUCTION_FOOD_COST;
        }
        if (unitType == UnitType.FISHING_BOAT) {
            return Constants.FISHING_BOAT_PRODUCTION_FOOD_COST;
        }
        if (unitType == UnitType.GALLEY) {
            return Constants.GALLEY_PRODUCTION_FOOD_COST;
        }
        return Constants.BORDER_EXPANDER_PRODUCTION_FOOD_COST;
    }

    public static int getDurationInTurns(UnitType unitType) {
        if (unitType == UnitType.EXPLORER) {
            return Constants.EXPLORER_PRODUCTION_TURNS;
        }
        if (unitType == UnitType.BUILDER) {
            return Constants.BUILDER_PRODUCTION_TURNS;
        }
        if (unitType == UnitType.WORKER) {
            return Constants.WORKER_PRODUCTION_TURNS;
        }
        if (unitType == UnitType.SWORDSMAN) {
            return Constants.SWORDSMAN_PRODUCTION_TURNS;
        }
        if (unitType == UnitType.ARCHER) {
            return Constants.ARCHER_PRODUCTION_TURNS;
        }
        if (unitType == UnitType.CAVALRY) {
            return Constants.CAVALRY_PRODUCTION_TURNS;
        }
        if (unitType == UnitType.CATAPULT) {
            return Constants.CATAPULT_PRODUCTION_TURNS;
        }
        if (unitType == UnitType.FISHING_BOAT) {
            return Constants.FISHING_BOAT_PRODUCTION_TURNS;
        }
        if (unitType == UnitType.GALLEY) {
            return Constants.GALLEY_PRODUCTION_TURNS;
        }
        return Constants.BORDER_EXPANDER_PRODUCTION_TURNS;
    }
}