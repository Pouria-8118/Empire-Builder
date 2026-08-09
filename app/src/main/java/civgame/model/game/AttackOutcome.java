package civgame.model.game;

import civgame.model.unit.Unit;

import java.util.List;

public class AttackOutcome {

    private final CombatResult combatResult;
    private final boolean autoCaptured;
    private final List<Unit> attackerLosses;
    private final List<Unit> defenderLosses;

    public AttackOutcome(CombatResult combatResult, boolean autoCaptured, List<Unit> attackerLosses, List<Unit> defenderLosses) {
        this.combatResult = combatResult;
        this.autoCaptured = autoCaptured;
        this.attackerLosses = attackerLosses;
        this.defenderLosses = defenderLosses;
    }

    public CombatResult getCombatResult() {
        return combatResult;
    }

    public boolean isAutoCaptured() {
        return autoCaptured;
    }

    public List<Unit> getAttackerLosses() {
        return attackerLosses;
    }

    public List<Unit> getDefenderLosses() {
        return defenderLosses;
    }
}