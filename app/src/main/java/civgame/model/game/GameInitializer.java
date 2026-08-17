package civgame.model.game;

import civgame.model.building.TownHall;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexMapGenerator;
import civgame.model.map.TradingPost;
import civgame.model.resource.ResourceType;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitFactory;
import civgame.model.unit.UnitType;
import civgame.util.Constants;

import java.util.List;

public class GameInitializer {

    private final UnitFactory unitFactory;

    public GameInitializer() {
        this.unitFactory = new UnitFactory();
    }

    public GameSession createNewGame() {
        HexCoordinate townHallCoordinate = new HexCoordinate(0, 0);
        HexMap hexMap = new HexMap(Constants.DEFAULT_MAP_RADIUS);

        HexMapGenerator mapGenerator = new HexMapGenerator();
        mapGenerator.generate(hexMap, townHallCoordinate);

        TownHall townHall = new TownHall(townHallCoordinate);
        GameSession gameSession = new GameSession(hexMap, townHall, Constants.INITIAL_UNIT_CAP);

        setupInitialResources(gameSession);
        setupInitialUnits(gameSession, townHallCoordinate);
        setupTradingPost(gameSession, mapGenerator, hexMap, townHallCoordinate);
        setupTribes(gameSession, mapGenerator, hexMap, townHallCoordinate);

        return gameSession;
    }

    private void setupTribes(GameSession gameSession, HexMapGenerator mapGenerator, HexMap hexMap, HexCoordinate townHallCoordinate) {
        List<civgame.model.tribe.Tribe> tribes = mapGenerator.placeTribes(hexMap, townHallCoordinate);
        for (int i = 0; i < tribes.size(); i++) {
            gameSession.addTribe(tribes.get(i));
        }
    }

    public GameSession createNewGameFromBlueprint(civgame.model.map.MapBlueprint blueprint) {
        HexCoordinate townHallCoordinate = blueprint.getTownHallCoordinate();
        if (townHallCoordinate == null) {
            throw new IllegalStateException("This map has no Town Hall placed.");
        }

        HexMap hexMap = blueprint.toHexMap();
        TownHall townHall = new TownHall(townHallCoordinate);
        GameSession gameSession = new GameSession(hexMap, townHall, Constants.INITIAL_UNIT_CAP);

        setupInitialResources(gameSession);
        setupInitialUnits(gameSession, townHallCoordinate);

        HexMapGenerator mapGenerator = new HexMapGenerator();
        setupTradingPost(gameSession, mapGenerator, hexMap, townHallCoordinate);
        setupTribes(gameSession, mapGenerator, hexMap, townHallCoordinate);

        return gameSession;
    }

    private void setupTradingPost(GameSession gameSession, HexMapGenerator mapGenerator, HexMap hexMap, HexCoordinate townHallCoordinate) {
        HexCoordinate tradingPostCoordinate = mapGenerator.pickTradingPostCoordinate(hexMap, townHallCoordinate);
        if (tradingPostCoordinate != null) {
            gameSession.addTradingPost(new TradingPost(tradingPostCoordinate));
        }
    }

    private void setupInitialResources(GameSession gameSession) {
        gameSession.getResourceStorage().addResource(ResourceType.FOOD, Constants.STARTING_FOOD);
        gameSession.getResourceStorage().addResource(ResourceType.WOOD, Constants.STARTING_WOOD);
        gameSession.getResourceStorage().addResource(ResourceType.STONE, Constants.STARTING_STONE);
        gameSession.getResourceStorage().addResource(ResourceType.IRON, Constants.STARTING_IRON);
    }

    private void setupInitialUnits(GameSession gameSession, HexCoordinate townHallCoordinate) {
        List<HexCoordinate> spawnPositions = townHallCoordinate.getNeighbors();
        int nextSpawnIndex = 0;

        for (int i = 0; i < Constants.STARTING_BUILDER_COUNT; i++) {
            nextSpawnIndex = spawnUnit(gameSession, UnitType.BUILDER, spawnPositions, nextSpawnIndex);
        }

        for (int i = 0; i < Constants.STARTING_WORKER_COUNT; i++) {
            nextSpawnIndex = spawnUnit(gameSession, UnitType.WORKER, spawnPositions, nextSpawnIndex);
        }

        for (int i = 0; i < Constants.STARTING_EXPLORER_COUNT; i++) {
            nextSpawnIndex = spawnUnit(gameSession, UnitType.EXPLORER, spawnPositions, nextSpawnIndex);
        }
    }

    private int spawnUnit(GameSession gameSession, UnitType unitType, List<HexCoordinate> spawnPositions, int spawnIndex) {
        HexCoordinate spawnCoordinate = spawnPositions.get(spawnIndex);
        Unit unit = unitFactory.createUnit(unitType, spawnCoordinate);
        gameSession.addUnit(unit);
        return spawnIndex + 1;
    }
}