package civgame.model.unit;

import civgame.model.combat.Combatant;
import civgame.model.map.HexCoordinate;

public abstract class MilitaryUnit extends Unit implements Combatant {

    private final int attackPower;
    private final int attackRange;

    public MilitaryUnit(String id, HexCoordinate coordinate, int maxActionPoints, int visionRadius, int maxHitPoints, int attackPower, int attackRange) {
        super(id, coordinate, maxActionPoints, visionRadius);
        setMaxHitPoints(maxHitPoints);
        this.attackPower = attackPower;
        this.attackRange = attackRange;
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