package civgame.model.event;

public class TurnEndedEvent implements GameEvent {

    private final int turnNumber;

    public TurnEndedEvent(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public int getTurnNumber() {
        return turnNumber;
    }
}