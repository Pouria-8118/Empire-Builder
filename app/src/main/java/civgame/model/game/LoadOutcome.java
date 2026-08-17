package civgame.model.game;

public class LoadOutcome {

    private final boolean success;
    private final String message;
    private final GameSession gameSession;

    private LoadOutcome(boolean success, String message, GameSession gameSession) {
        this.success = success;
        this.message = message;
        this.gameSession = gameSession;
    }

    public static LoadOutcome success(GameSession gameSession) {
        return new LoadOutcome(true, "Loaded successfully.", gameSession);
    }

    public static LoadOutcome failure(String message) {
        return new LoadOutcome(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public GameSession getGameSession() {
        return gameSession;
    }
}