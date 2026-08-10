package civgame.model.animal;

import civgame.model.combat.Attackable;
import civgame.model.map.HexCoordinate;

public class WildAnimal implements Attackable {

    private final String id;
    private HexCoordinate coordinate;
    private int currentHitPoints;
    private final int maxHitPoints;
    private final int attackPower;

    public WildAnimal(String id, HexCoordinate coordinate, int maxHitPoints, int attackPower) {
        this.id = id;
        this.coordinate = coordinate;
        this.maxHitPoints = maxHitPoints;
        this.currentHitPoints = maxHitPoints;
        this.attackPower = attackPower;
    }

    public String getId() {
        return id;
    }

    public HexCoordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(HexCoordinate coordinate) {
        this.coordinate = coordinate;
    }

    public int getAttackPower() {
        return attackPower;
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