package civgame.model.animal;

import civgame.model.combat.Attackable;
import civgame.model.map.HexCoordinate;

public class WildAnimal implements Attackable {

    private final String id;
    private HexCoordinate coordinate;
    private final HexCoordinate denCoordinate;
    private int currentHitPoints;
    private final int maxHitPoints;
    private final int attackPower;
    private final int maxActionPoints;
    private int currentActionPoints;
    private final int attackRange;

    public WildAnimal(String id, HexCoordinate coordinate, HexCoordinate denCoordinate, int maxHitPoints, int attackPower, int maxActionPoints, int attackRange) {
        this.id = id;
        this.coordinate = coordinate;
        this.denCoordinate = denCoordinate;
        this.maxHitPoints = maxHitPoints;
        this.currentHitPoints = maxHitPoints;
        this.attackPower = attackPower;
        this.maxActionPoints = maxActionPoints;
        this.currentActionPoints = maxActionPoints;
        this.attackRange = attackRange;
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

    public HexCoordinate getDenCoordinate() {
        return denCoordinate;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public int getCurrentActionPoints() {
        return currentActionPoints;
    }

    public boolean hasEnoughActionPoints(int amount) {
        return currentActionPoints >= amount;
    }

    public void consumeActionPoints(int amount) {
        currentActionPoints = currentActionPoints - amount;
        if (currentActionPoints < 0) {
            currentActionPoints = 0;
        }
    }

    public void refreshActionPoints() {
        currentActionPoints = maxActionPoints;
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