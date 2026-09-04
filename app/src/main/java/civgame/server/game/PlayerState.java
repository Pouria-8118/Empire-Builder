package civgame.server.game;

import civgame.model.game.GameSession;
import civgame.model.map.HexCoordinate;
import civgame.model.trade.TradeInbox;
import civgame.server.trade.TradeService;

import java.util.ArrayList;
import java.util.List;

public class PlayerState {

    private final String clientId;
    private final String username;
    private final int playerIndex;
    private final GameSession session;
    private final HexCoordinate spawnCoordinate;
    private final TradeInbox tradeInbox;
    private final TradeService tradeService;
    private boolean eliminated;
    private boolean disconnected;

    public PlayerState(String clientId,
                       String username,
                       int playerIndex,
                       GameSession session,
                       HexCoordinate spawnCoordinate) {
        this.clientId = clientId;
        this.username = username;
        this.playerIndex = playerIndex;
        this.session = session;
        this.spawnCoordinate = spawnCoordinate;
        this.tradeInbox = new TradeInbox();
        this.tradeService = new TradeService();
        this.eliminated = false;
        this.disconnected = false;
    }

    public String getClientId() {
        return clientId;
    }

    public String getUsername() {
        return username;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public GameSession getSession() {
        return session;
    }

    public HexCoordinate getSpawnCoordinate() {
        return spawnCoordinate;
    }

    public boolean isEliminated() {
        return eliminated;
    }

    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }

    public boolean isDisconnected() {
        return disconnected;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    public boolean isActive() {
        return !eliminated && !disconnected;
    }

    public int countActiveTownHalls() {
        return session.countActiveTownHalls();
    }

    public List<String> getVisibleUnitIds() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < session.getUnits().size(); i++) {
            ids.add(session.getUnits().get(i).getId());
        }
        return ids;
    }

    public TradeInbox getTradeInbox() {
        return tradeInbox;
    }

    public TradeService getTradeService() {
        return tradeService;
    }
}