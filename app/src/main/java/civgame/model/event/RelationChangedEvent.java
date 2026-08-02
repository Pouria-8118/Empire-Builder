package civgame.model.event;

// TODO : tribeId will reference the actual Tribe model once neutral tribes are introduced.
public class RelationChangedEvent implements GameEvent {

    private final String tribeId;
    private final int oldRelationValue;
    private final int newRelationValue;

    public RelationChangedEvent(String tribeId, int oldRelationValue, int newRelationValue) {
        this.tribeId = tribeId;
        this.oldRelationValue = oldRelationValue;
        this.newRelationValue = newRelationValue;
    }

    public String getTribeId() {
        return tribeId;
    }

    public int getOldRelationValue() {
        return oldRelationValue;
    }

    public int getNewRelationValue() {
        return newRelationValue;
    }
}