package civgame.model.game;

public interface ProductionCommand {

    void execute(GameSession gameSession);

    void cancel(GameSession gameSession);

    void advanceOneTurn();

    boolean isFinished();

    int getTotalTurns();

    int getTurnsRemaining();

    String describeForHud();
}