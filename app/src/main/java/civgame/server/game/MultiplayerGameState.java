package civgame.server.game;

import civgame.model.map.HexMap;
import civgame.model.map.prebuilt.PrebuiltMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiplayerGameState {

    private final String gameId;
    private final String lobbyId;
    private final HexMap sharedMap;
    private final Map<String, PlayerState> playersByClientId;
    private final List<String> turnOrder;
    private final civgame.model.diplomacy.PlayerRelationMap relationMap;
    private int currentTurnIndex;
    private int turnNumber;
    private boolean finished;

    public MultiplayerGameState(String gameId,
                                String lobbyId,
                                PrebuiltMap prebuiltMap,
                                List<String> orderedClientIds) {
        this.gameId = gameId;
        this.lobbyId = lobbyId;
        this.sharedMap = prebuiltMap.getHexMap();
        this.playersByClientId = new ConcurrentHashMap<>();
        this.turnOrder = new ArrayList<>(orderedClientIds);
        this.relationMap = new civgame.model.diplomacy.PlayerRelationMap();
        for (int i = 0; i < orderedClientIds.size(); i++) {
            this.relationMap.addPlayer(orderedClientIds.get(i));
        }
        this.currentTurnIndex = 0;
        this.turnNumber = 1;
        this.finished = false;
    }

    public String getGameId() {
        return gameId;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public HexMap getSharedMap() {
        return sharedMap;
    }

    public civgame.model.diplomacy.PlayerRelationMap getRelationMap() {
        return relationMap;
    }

    public void addPlayer(PlayerState playerState) {
        playersByClientId.put(playerState.getClientId(), playerState);
    }

    public PlayerState getPlayer(String clientId) {
        return playersByClientId.get(clientId);
    }

    public List<PlayerState> getAllPlayers() {
        return new ArrayList<>(playersByClientId.values());
    }

    public List<String> getTurnOrder() {
        return new ArrayList<>(turnOrder);
    }

    public String getCurrentTurnClientId() {
        if (turnOrder.isEmpty()) {
            return null;
        }
        return turnOrder.get(currentTurnIndex);
    }

    public PlayerState getCurrentTurnPlayer() {
        String clientId = getCurrentTurnClientId();
        if (clientId == null) {
            return null;
        }
        return playersByClientId.get(clientId);
    }

    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public boolean isFinished() {
        return finished;
    }

    public synchronized void advanceTurn() {
        if (turnOrder.isEmpty()) {
            return;
        }
        currentTurnIndex = (currentTurnIndex + 1) % turnOrder.size();
        if (currentTurnIndex == 0) {
            turnNumber = turnNumber + 1;
        }
    }

    public synchronized void removePlayerFromTurnOrder(String clientId) {
        int removedIndex = turnOrder.indexOf(clientId);
        if (removedIndex < 0) {
            return;
        }
        turnOrder.remove(removedIndex);
        if (turnOrder.isEmpty()) {
            finished = true;
            return;
        }
        if (currentTurnIndex >= turnOrder.size()) {
            currentTurnIndex = 0;
        } else if (removedIndex < currentTurnIndex) {
            currentTurnIndex = currentTurnIndex - 1;
        }
    }

    public synchronized void eliminatePlayer(String clientId) {
        PlayerState player = playersByClientId.get(clientId);
        if (player == null) {
            return;
        }
        player.setEliminated(true);
        removePlayerFromTurnOrder(clientId);
        checkVictoryCondition();
    }

    private void checkVictoryCondition() {
        int activePlayers = 0;
        for (PlayerState state : playersByClientId.values()) {
            if (state.isActive() && state.countActiveTownHalls() > 0) {
                activePlayers = activePlayers + 1;
            }
        }
        if (activePlayers <= 1 && playersByClientId.size() > 1) {
            finished = true;
        }
    }

    public PlayerState getWinner() {
        for (PlayerState state : playersByClientId.values()) {
            if (state.isActive() && state.countActiveTownHalls() > 0) {
                return state;
            }
        }
        return null;
    }
}