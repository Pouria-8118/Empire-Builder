package civgame.view.game;

import civgame.model.unit.Unit;

public class SelectionState {

    private Unit selectedUnit;

    public Unit getSelectedUnit() {
        return selectedUnit;
    }

    public void setSelectedUnit(Unit unit) {
        this.selectedUnit = unit;
    }

    public void clearSelection() {
        this.selectedUnit = null;
    }

    public boolean hasSelection() {
        return selectedUnit != null;
    }

    // TODO : selection will also drive which HUD action buttons are enabled
}