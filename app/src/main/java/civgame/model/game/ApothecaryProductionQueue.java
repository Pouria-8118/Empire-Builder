package civgame.model.game;

public class ApothecaryProductionQueue implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private ItemProductionCommand currentCommand;

    public boolean isBusy() {
        return currentCommand != null;
    }

    public ItemProductionCommand getCurrentCommand() {
        return currentCommand;
    }

    public boolean startCommand(ItemProductionCommand command) {
        if (isBusy()) {
            return false;
        }
        currentCommand = command;
        return true;
    }

    public void cancelCurrentCommand() {
        currentCommand = null;
    }

    public ItemProductionCommand advanceOneTurn(GameSession gameSession) {
        if (currentCommand == null) {
            return null;
        }
        currentCommand.advanceOneTurn();
        if (currentCommand.isFinished()) {
            ItemProductionCommand finishedCommand = currentCommand;
            currentCommand = null;
            finishedCommand.execute(gameSession);
            return finishedCommand;
        }
        return null;
    }
}