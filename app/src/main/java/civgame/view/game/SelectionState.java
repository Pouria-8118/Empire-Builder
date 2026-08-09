package civgame.view.game;

import civgame.model.map.HexCoordinate;
import civgame.model.unit.Unit;

import java.util.HashSet;
import java.util.Set;

public class SelectionState {

    private Unit selectedUnit;
    private Set<HexCoordinate> reachableTiles;
    private Set<HexCoordinate> attackableTiles;
    private boolean attackTargetingActive;
    private HexCoordinate pendingAttackAttacker;
    private HexCoordinate pendingAttackDefender;

    public SelectionState() {
        this.reachableTiles = new HashSet<HexCoordinate>();
        this.attackableTiles = new HashSet<HexCoordinate>();
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
        this.attackableTiles = new HashSet<HexCoordinate>();
        this.attackTargetingActive = false;
        this.pendingAttackAttacker = null;
        this.pendingAttackDefender = null;
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

    public Set<HexCoordinate> getAttackableTiles() {
        return attackableTiles;
    }

    public void setAttackableTiles(Set<HexCoordinate> attackableTiles) {
        this.attackableTiles = attackableTiles;
    }

    public boolean isAttackTargetingActive() {
        return attackTargetingActive;
    }

    public void setAttackTargetingActive(boolean attackTargetingActive) {
        this.attackTargetingActive = attackTargetingActive;
    }

    public HexCoordinate getPendingAttackAttacker() {
        return pendingAttackAttacker;
    }

    public void setPendingAttackAttacker(HexCoordinate pendingAttackAttacker) {
        this.pendingAttackAttacker = pendingAttackAttacker;
    }

    public HexCoordinate getPendingAttackDefender() {
        return pendingAttackDefender;
    }

    public void setPendingAttackDefender(HexCoordinate pendingAttackDefender) {
        this.pendingAttackDefender = pendingAttackDefender;
    }
}