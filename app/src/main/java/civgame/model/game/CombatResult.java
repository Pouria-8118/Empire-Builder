package civgame.model.game;

import java.util.List;

public class CombatResult {

    private final List<Integer> attackerDice;
    private final List<Integer> defenderDice;
    private final int attackerHits;
    private final int defenderHits;

    public CombatResult(List<Integer> attackerDice, List<Integer> defenderDice, int attackerHits, int defenderHits) {
        this.attackerDice = attackerDice;
        this.defenderDice = defenderDice;
        this.attackerHits = attackerHits;
        this.defenderHits = defenderHits;
    }

    public List<Integer> getAttackerDice() {
        return attackerDice;
    }

    public List<Integer> getDefenderDice() {
        return defenderDice;
    }

    public int getAttackerHits() {
        return attackerHits;
    }

    public int getDefenderHits() {
        return defenderHits;
    }
}