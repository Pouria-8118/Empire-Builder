package civgame.model.tribe;

import civgame.model.combat.Attackable;
import civgame.model.unit.UnitType;

public class TribeGuardUnit implements Attackable {

    private final UnitType combatCategory;
    private int currentHitPoints;
    private final int maxHitPoints;

    public TribeGuardUnit(UnitType combatCategory, int maxHitPoints) {
        this.combatCategory = combatCategory;
        this.maxHitPoints = maxHitPoints;
        this.currentHitPoints = maxHitPoints;
    }

    public UnitType getCombatCategory() {
        return combatCategory;
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
}