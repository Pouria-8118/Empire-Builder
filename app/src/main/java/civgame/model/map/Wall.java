package civgame.model.map;

import civgame.model.combat.Attackable;
import civgame.util.Constants;

public class Wall implements Attackable, java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int currentHitPoints;
    private int maxHitPoints;
    private int missedUpkeepTurns;

    public Wall() {
        this.maxHitPoints = Constants.WALL_MAX_HIT_POINTS;
        this.currentHitPoints = maxHitPoints;
        this.missedUpkeepTurns = 0;
    }

    @Override
    public int getCurrentHitPoints() {
        return currentHitPoints;
    }

    @Override
    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    @Override
    public boolean isDestroyedByCombat() {
        return currentHitPoints <= 0;
    }

    @Override
    public void applyCombatDamage(int amount) {
        currentHitPoints = currentHitPoints - amount;
        if (currentHitPoints < 0) {
            currentHitPoints = 0;
        }
    }

    public boolean isActive() {
        return currentHitPoints > 0;
    }

    public int getDefensiveDiceBonus() {
        return Constants.WALL_DEFENSE_DICE_BONUS;
    }

    public int getMissedUpkeepTurns() {
        return missedUpkeepTurns;
    }

    public void registerMissedUpkeep() {
        missedUpkeepTurns = missedUpkeepTurns + 1;
    }

    public void resetMissedUpkeep() {
        missedUpkeepTurns = 0;
    }
}