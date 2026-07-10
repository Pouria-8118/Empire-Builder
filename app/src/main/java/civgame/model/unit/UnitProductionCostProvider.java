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
        return Constants.BORDER_EXPANDER_PRODUCTION_TURNS;
    }
}