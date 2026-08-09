package civgame.model.unit;

import civgame.model.combat.Combatant;
import civgame.model.map.HexCoordinate;

public abstract class MilitaryUnit extends Unit implements Combatant {

    private int currentHitPoints;
    private final int maxHitPoints;
    private final int attackPower;
    private final int attackRange;

    public MilitaryUnit(String id, HexCoordinate coordinate, int maxActionPoints, int visionRadius, int maxHitPoints, int attackPower, int attackRange) {
        super(id, coordinate, maxActionPoints, visionRadius);
        this.maxHitPoints = maxHitPoints;
        this.currentHitPoints = maxHitPoints;
        this.attackPower = attackPower;
        this.attackRange = attackRange;
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

    @Override
    public int getAttackPower() {
        return attackPower;
    }

    @Override
    public int getAttackRange() {
        return attackRange;
    }
}