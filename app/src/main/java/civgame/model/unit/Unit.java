package civgame.model.unit;

import civgame.model.combat.Attackable;
import civgame.model.map.HexCoordinate;
import civgame.util.Constants;

public abstract class Unit implements Attackable, java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private HexCoordinate coordinate;
    private final int maxActionPoints;
    private int currentActionPoints;
    private final int visionRadius;
    private boolean floating;
    private int currentHitPoints;
    private int maxHitPoints;

    public Unit(String id, HexCoordinate coordinate, int maxActionPoints, int visionRadius) {
        this.id = id;
        this.coordinate = coordinate;
        this.maxActionPoints = maxActionPoints;
        this.currentActionPoints = maxActionPoints;
        this.visionRadius = visionRadius;
        this.floating = false;
        this.maxHitPoints = Constants.CIVILIAN_UNIT_MAX_HIT_POINTS;
        this.currentHitPoints = maxHitPoints;
    }

    public abstract UnitType getUnitType();

    public String getId() {
        return id;
    }

    public HexCoordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(HexCoordinate coordinate) {
        this.coordinate = coordinate;
    }

    public int getMaxActionPoints() {
        return maxActionPoints;
    }

    public int getCurrentActionPoints() {
        return currentActionPoints;
    }

    public int getVisionRadius() {
        return visionRadius;
    }

    public boolean hasEnoughActionPoints(int amount) {
        return currentActionPoints >= amount;
    }

    public boolean consumeActionPoints(int amount) {
        if (!hasEnoughActionPoints(amount)) {
            return false;
        }
        currentActionPoints = currentActionPoints - amount;
        return true;
    }

    public void zeroRemainingActionPoints() {
        currentActionPoints = 0;
    }

    public void refreshActionPoints() {
        currentActionPoints = maxActionPoints;
    }

    public boolean isIdleWithRemainingActionPoints() {
        return currentActionPoints > 0;
    }

    public void applyActionPointPenalty(int amount) {
        currentActionPoints = currentActionPoints - amount;
        if (currentActionPoints < 0) {
            currentActionPoints = 0;
        }
    }

    public boolean isFloating() {
        return floating;
    }

    public void setFloating(boolean floating) {
        this.floating = floating;
    }

    protected void setMaxHitPoints(int maxHitPoints) {
        this.maxHitPoints = maxHitPoints;
        this.currentHitPoints = maxHitPoints;
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