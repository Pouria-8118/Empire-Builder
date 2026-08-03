package civgame.model.game;

import civgame.model.building.TownHallLevel;

public class TownHallLevelUpCommand implements ProductionCommand {

    private final TownHallLevel targetLevel;
    private final int totalTurns;
    private int turnsRemaining;

    public TownHallLevelUpCommand(TownHallLevel targetLevel, int totalTurns) {
        this.targetLevel = targetLevel;
        this.totalTurns = totalTurns;
        this.turnsRemaining = totalTurns;
    }

    @Override
    public void execute(GameSession gameSession) {
        gameSession.getTownHallLevelService().applyLevelUpEffects(gameSession, targetLevel);
    }

    @Override
    public void cancel(GameSession gameSession) {
        // Resources already spent when this command was queued are not refunded on cancellation.
    }

    @Override
    public void advanceOneTurn() {
        if (turnsRemaining > 0) {
            turnsRemaining = turnsRemaining - 1;
        }
    }

    @Override
    public boolean isFinished() {
        return turnsRemaining <= 0;
    }

    @Override
    public int getTotalTurns() {
        return totalTurns;
    }

    @Override
    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    @Override
    public String describeForHud() {
        return "Town Hall Level Up: " + targetLevel.name() + " - " + turnsRemaining + " turns left";
    }

    public TownHallLevel getTargetLevel() {
        return targetLevel;
    }
}