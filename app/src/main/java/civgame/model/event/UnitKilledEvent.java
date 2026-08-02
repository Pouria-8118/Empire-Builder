package civgame.model.event;

import civgame.model.unit.Unit;

public class UnitKilledEvent implements GameEvent {

    private final Unit unit;

    public UnitKilledEvent(Unit unit) {
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }
}