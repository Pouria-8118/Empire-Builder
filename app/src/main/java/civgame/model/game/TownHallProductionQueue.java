package civgame.model.game;

public class TownHallProductionQueue {

    private ProductionCommand currentCommand;

    public boolean isBusy() {
        return currentCommand != null;
    }

    public ProductionCommand getCurrentCommand() {
        return currentCommand;
    }

    public boolean startCommand(ProductionCommand command) {
        if (isBusy()) {
            return false;
        }
        currentCommand = command;
        return true;
    }

    public void cancelCurrentCommand(GameSession gameSession) {
        if (currentCommand == null) {
            return;
        }
        currentCommand.cancel(gameSession);
        currentCommand = null;
    }

    public ProductionCommand advanceOneTurn(GameSession gameSession) {
        if (currentCommand == null) {
            return null;
        }

        currentCommand.advanceOneTurn();

        if (currentCommand.isFinished()) {
            ProductionCommand finishedCommand = currentCommand;
            currentCommand = null;
            finishedCommand.execute(gameSession);
            return finishedCommand;
        }

        return null;
    }
}