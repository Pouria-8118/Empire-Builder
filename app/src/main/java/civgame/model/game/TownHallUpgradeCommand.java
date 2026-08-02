package civgame.model.game;

import civgame.model.building.TownHallUpgradeType;

public class TownHallUpgradeCommand implements ProductionCommand {

    private final TownHallUpgradeType upgradeType;
    private final int totalTurns;
    private int turnsRemaining;

    public TownHallUpgradeCommand(TownHallUpgradeType upgradeType, int totalTurns) {
        this.upgradeType = upgradeType;
        this.totalTurns = totalTurns;
        this.turnsRemaining = totalTurns;
    }

    @Override
    public void execute(GameSession gameSession) {
        gameSession.getTownHallUpgradeService().applyUpgradeEffects(gameSession, upgradeType);
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
        return upgradeType.name() + " - " + turnsRemaining + " turns left";
    }

    public TownHallUpgradeType getUpgradeType() {
        return upgradeType;
    }
}