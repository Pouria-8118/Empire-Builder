package civgame.model.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CombatResolutionService {

    private final Random random;

    public CombatResolutionService() {
        this.random = new Random();
    }

    public CombatResult resolveCombat(int attackerDiceCount, int defenderDiceCount, int defenderDieBonus) {
        List<Integer> attackerDice = rollDice(attackerDiceCount);
        List<Integer> defenderDice = rollDice(defenderDiceCount);
        applyDieBonus(defenderDice, defenderDieBonus);

        Collections.sort(attackerDice, Collections.reverseOrder());
        Collections.sort(defenderDice, Collections.reverseOrder());

        int pairCount = Math.min(attackerDice.size(), defenderDice.size());
        int attackerHits = 0;
        int defenderHits = 0;

        for (int i = 0; i < pairCount; i++) {
            int attackerValue = attackerDice.get(i);
            int defenderValue = defenderDice.get(i);

            if (attackerValue > defenderValue) {
                defenderHits = defenderHits + 1;
            } else {
                attackerHits = attackerHits + 1;
            }
        }

        return new CombatResult(attackerDice, defenderDice, attackerHits, defenderHits);
    }

    private List<Integer> rollDice(int diceCount) {
        List<Integer> values = new ArrayList<Integer>();
        for (int i = 0; i < diceCount; i++) {
            values.add(1 + random.nextInt(6));
        }
        return values;
    }
    
    private void applyDieBonus(List<Integer> dice, int bonus) {
        if (bonus <= 0) {
            return;
        }
        for (int i = 0; i < dice.size(); i++) {
            int boostedValue = dice.get(i) + bonus;
            if (boostedValue > 6) {
                boostedValue = 6;
            }
            dice.set(i, boostedValue);
        }
    }
}