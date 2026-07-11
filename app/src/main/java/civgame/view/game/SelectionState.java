package civgame.view.game;

import civgame.model.map.HexCoordinate;
import civgame.model.unit.Unit;

import java.util.HashSet;
import java.util.Set;

public class SelectionState {

    private Unit selectedUnit;
    private Set<HexCoordinate> reachableTiles;

    public SelectionState() {
        this.reachableTiles = new HashSet<HexCoordinate>();
    }

    public Unit getSelectedUnit() {
        return selectedUnit;
    }

    public void setSelectedUnit(Unit unit) {
        this.selectedUnit = unit;
    }

    public void clearSelection() {
        this.selectedUnit = null;
        this.reachableTiles = new HashSet<HexCoordinate>();
    }

    public boolean hasSelection() {
        return selectedUnit != null;
    }

    public Set<HexCoordinate> getReachableTiles() {
        return reachableTiles;
    }

    public void setReachableTiles(Set<HexCoordinate> reachableTiles) {
        this.reachableTiles = reachableTiles;
    }
}