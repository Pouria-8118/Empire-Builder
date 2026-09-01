package civgame.server.game;

import civgame.model.building.TownHall;
import civgame.model.game.FogOfWarCalculator;
import civgame.model.game.GameSession;
import civgame.model.map.HexCoordinate;
import civgame.model.map.prebuilt.PrebuiltMap;
import civgame.model.map.prebuilt.PrebuiltMapProvider;
import civgame.model.resource.ResourceType;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitFactory;
import civgame.model.unit.UnitType;
import civgame.server.lobby.Lobby;
import civgame.server.lobby.PlayerSlot;
import civgame.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiplayerGameSessionManager {

    private final Map<String, MultiplayerGameState> activeGames;
    private final Map<String, String> clientToGame;
    private final PrebuiltMapProvider mapProvider;
    private final UnitFactory unitFactory;

    public MultiplayerGameSessionManager() {
        this.activeGames = new ConcurrentHashMap<>();
        this.clientToGame = new ConcurrentHashMap<>();
        this.mapProvider = new PrebuiltMapProvider();
        this.unitFactory = new UnitFactory();
    }

    public synchronized MultiplayerGameState createGameFromLobby(Lobby lobby) {
        String mapName = lobby.getSelectedMapName();
        PrebuiltMap prebuiltMap = mapProvider.getMapByName(mapName);
        if (prebuiltMap == null) {
            prebuiltMap = mapProvider.getMapByIndex(0);
        }
        if (prebuiltMap == null) {
            return null;
        }

        List<String> orderedClientIds = new ArrayList<>(lobby.getPlayerClientIds());
        if (orderedClientIds.size() > prebuiltMap.getSpawnLocationCount()) {
            orderedClientIds = orderedClientIds.subList(0, prebuiltMap.getSpawnLocationCount());
        }

        String gameId = generateGameId();
        MultiplayerGameState gameState = new MultiplayerGameState(
                gameId,
                lobby.getLobbyId(),
                prebuiltMap,
                orderedClientIds
        );

        for (int i = 0; i < orderedClientIds.size(); i++) {
            String clientId = orderedClientIds.get(i);
            PlayerSlot slot = lobby.getPlayer(clientId);
            String username = slot != null ? slot.getUsername() : clientId;
            HexCoordinate spawn = prebuiltMap.getSpawnLocation(i);

            GameSession playerSession = buildPlayerSession(prebuiltMap, spawn);
            PlayerState playerState = new PlayerState(clientId, username, i, playerSession, spawn);
            gameState.addPlayer(playerState);
            clientToGame.put(clientId, gameId);
        }

        activeGames.put(gameId, gameState);
        System.out.println("Created multiplayer game " + gameId + " with " + orderedClientIds.size() + " players");
        return gameState;
    }

    private GameSession buildPlayerSession(PrebuiltMap prebuiltMap, HexCoordinate spawnCoordinate) {
        civgame.model.map.HexMap playerMap = prebuiltMap.getHexMap();
        TownHall townHall = new TownHall(spawnCoordinate);
        GameSession session = new GameSession(playerMap, townHall, Constants.INITIAL_UNIT_CAP);

        session.getResourceStorage().addResource(ResourceType.FOOD, Constants.STARTING_FOOD);
        session.getResourceStorage().addResource(ResourceType.WOOD, Constants.STARTING_WOOD);
        session.getResourceStorage().addResource(ResourceType.STONE, Constants.STARTING_STONE);
        session.getResourceStorage().addResource(ResourceType.IRON, Constants.STARTING_IRON);

        spawnInitialUnits(session, spawnCoordinate);
        return session;
    }

    private void spawnInitialUnits(GameSession session, HexCoordinate spawnCoordinate) {
        List<HexCoordinate> spawnPositions = spawnCoordinate.getNeighbors();
        int nextIndex = 0;

        for (int i = 0; i < Constants.STARTING_BUILDER_COUNT; i++) {
            nextIndex = spawnUnit(session, UnitType.BUILDER, spawnPositions, nextIndex);
        }
        for (int i = 0; i < Constants.STARTING_WORKER_COUNT; i++) {
            nextIndex = spawnUnit(session, UnitType.WORKER, spawnPositions, nextIndex);
        }
        for (int i = 0; i < Constants.STARTING_EXPLORER_COUNT; i++) {
            nextIndex = spawnUnit(session, UnitType.EXPLORER, spawnPositions, nextIndex);
        }
    }

    private int spawnUnit(GameSession session, UnitType unitType, List<HexCoordinate> spawnPositions, int index) {
        if (index >= spawnPositions.size()) {
            return index;
        }
        HexCoordinate coordinate = spawnPositions.get(index);
        Unit unit = unitFactory.createUnit(unitType, coordinate);
        session.addUnit(unit);
        return index + 1;
    }

    public MultiplayerGameState getGameForClient(String clientId) {
        String gameId = clientToGame.get(clientId);
        if (gameId == null) {
            return null;
        }
        return activeGames.get(gameId);
    }

    public synchronized void handleClientDisconnect(String clientId) {
        MultiplayerGameState game = getGameForClient(clientId);
        if (game == null) {
            return;
        }
        PlayerState state = game.getPlayer(clientId);
        if (state != null) {
            state.setDisconnected(true);
        }
        if (clientId.equals(game.getCurrentTurnClientId())) {
            game.advanceTurn();
        }
    }

    public synchronized void closeGame(String gameId) {
        MultiplayerGameState game = activeGames.remove(gameId);
        if (game == null) {
            return;
        }
        for (PlayerState state : game.getAllPlayers()) {
            clientToGame.remove(state.getClientId());
        }
    }

    public FogOfWarCalculator createFogCalculator() {
        return new FogOfWarCalculator();
    }

    private String generateGameId() {
        return "game_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 1000);
    }
}