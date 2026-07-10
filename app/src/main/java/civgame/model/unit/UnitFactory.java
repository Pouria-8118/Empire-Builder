package civgame.model.unit;

import civgame.model.map.HexCoordinate;
import civgame.util.Constants;
import civgame.util.UnitIdGenerator;

public class UnitFactory {

    public Unit createUnit(UnitType unitType, HexCoordinate coordinate) {
        if (unitType == UnitType.EXPLORER) {
            return createExplorer(coordinate);
        }
        if (unitType == UnitType.BUILDER) {
            return createBuilder(coordinate);
        }
        if (unitType == UnitType.WORKER) {
            return createWorker(coordinate);
        }
        return createBorderExpander(coordinate);
    }

    private Explorer createExplorer(HexCoordinate coordinate) {
        String id = UnitIdGenerator.generateNextId("EXPLORER");
        return new Explorer(id, coordinate, Constants.EXPLORER_MAX_ACTION_POINTS, Constants.EXPLORER_VISION_RADIUS);
    }

    private Builder createBuilder(HexCoordinate coordinate) {
        String id = UnitIdGenerator.generateNextId("BUILDER");
        return new Builder(id, coordinate, Constants.BUILDER_MAX_ACTION_POINTS, Constants.BUILDER_VISION_RADIUS, Constants.BUILDER_STARTING_CHARGES);
    }

    private Worker createWorker(HexCoordinate coordinate) {
        String id = UnitIdGenerator.generateNextId("WORKER");
        return new Worker(id, coordinate, Constants.WORKER_MAX_ACTION_POINTS, Constants.WORKER_VISION_RADIUS);
    }

    private BorderExpander createBorderExpander(HexCoordinate coordinate) {
        String id = UnitIdGenerator.generateNextId("BORDER_EXPANDER");
        return new BorderExpander(id, coordinate, Constants.BORDER_EXPANDER_MAX_ACTION_POINTS, Constants.BORDER_EXPANDER_VISION_RADIUS);
    }
}