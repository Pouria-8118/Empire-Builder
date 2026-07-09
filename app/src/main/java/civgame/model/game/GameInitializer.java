package civgame.model.game;

import civgame.model.building.TownHall;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexMapGenerator;
import civgame.model.resource.ResourceType;
import civgame.model.unit.Builder;
import civgame.model.unit.Explorer;
import civgame.model.unit.Worker;
import civgame.util.Constants;
import civgame.util.UnitIdGenerator;

import java.util.List;

public class GameInitializer {

    public GameSession createNewGame() {
        HexCoordinate townHallCoordinate = new HexCoordinate(0, 0);
        HexMap hexMap = new HexMap(Constants.DEFAULT_MAP_RADIUS);

        HexMapGenerator mapGenerator = new HexMapGenerator();
        mapGenerator.generate(hexMap, townHallCoordinate);

        TownHall townHall = new TownHall(townHallCoordinate);
        GameSession gameSession = new GameSession(hexMap, townHall, Constants.INITIAL_UNIT_CAP);

        setupInitialResources(gameSession);
        setupInitialUnits(gameSession, townHallCoordinate);

        return gameSession;
    }

    private void setupInitialResources(GameSession gameSession) {
        gameSession.getResourceStorage().addResource(ResourceType.FOOD, Constants.STARTING_FOOD);
        gameSession.getResourceStorage().addResource(ResourceType.WOOD, Constants.STARTING_WOOD);
        gameSession.getResourceStorage().addResource(ResourceType.STONE, Constants.STARTING_STONE);
        gameSession.getResourceStorage().addResource(ResourceType.IRON, Constants.STARTING_IRON);
    }

    // Spawns each starting unit on a distinct neighboring hex of the Town Hall,
    // instead of stacking every unit on the exact same tile.
    private void setupInitialUnits(GameSession gameSession, HexCoordinate townHallCoordinate) {
        List<HexCoordinate> spawnPositions = townHallCoordinate.getNeighbors();
        int nextSpawnIndex = 0;

        for (int i = 0; i < Constants.STARTING_BUILDER_COUNT; i++) {
            HexCoordinate spawnCoordinate = spawnPositions.get(nextSpawnIndex);
            nextSpawnIndex = nextSpawnIndex + 1;

            String builderId = UnitIdGenerator.generateNextId("BUILDER");
            Builder builder = new Builder(
                    builderId,
                    spawnCoordinate,
                    Constants.BUILDER_MAX_ACTION_POINTS,
                    Constants.BUILDER_VISION_RADIUS,
                    Constants.BUILDER_STARTING_CHARGES
            );
            gameSession.addUnit(builder);
        }

        for (int i = 0; i < Constants.STARTING_WORKER_COUNT; i++) {
            HexCoordinate spawnCoordinate = spawnPositions.get(nextSpawnIndex);
            nextSpawnIndex = nextSpawnIndex + 1;

            String workerId = UnitIdGenerator.generateNextId("WORKER");
            Worker worker = new Worker(
                    workerId,
                    spawnCoordinate,
                    Constants.WORKER_MAX_ACTION_POINTS,
                    Constants.WORKER_VISION_RADIUS
            );
            gameSession.addUnit(worker);
        }

        for (int i = 0; i < Constants.STARTING_EXPLORER_COUNT; i++) {
            HexCoordinate spawnCoordinate = spawnPositions.get(nextSpawnIndex);
            nextSpawnIndex = nextSpawnIndex + 1;

            String explorerId = UnitIdGenerator.generateNextId("EXPLORER");
            Explorer explorer = new Explorer(
                    explorerId,
                    spawnCoordinate,
                    Constants.EXPLORER_MAX_ACTION_POINTS,
                    Constants.EXPLORER_VISION_RADIUS
            );
            gameSession.addUnit(explorer);
        }
    }

    // TODO : Border Expander units are produced later from the Town Hall queue,
    // not part of the starting 5 units
}