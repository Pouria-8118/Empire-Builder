package civgame.model.unit;

import civgame.model.combat.Attackable;
import civgame.model.map.HexCoordinate;
import civgame.util.Constants;
import java.time.LocalDateTime;

public abstract class Unit implements Attackable, java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final LocalDateTime createdAt;
    private HexCoordinate coordinate;
    private final int maxActionPoints;
    private int currentActionPoints;
    private int extraActionPoints;
    private final int visionRadius;
    private boolean floating;
    private int currentHitPoints;
    private int maxHitPoints;
    private boolean combatBoosted;

    public Unit(String id, HexCoordinate coordinate, int maxActionPoints, int visionRadius) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
        this.coordinate = coordinate;
        this.maxActionPoints = maxActionPoints;
        this.currentActionPoints = maxActionPoints;
        this.extraActionPoints = 0;
        this.visionRadius = visionRadius;
        this.floating = false;
        this.maxHitPoints = Constants.CIVILIAN_UNIT_MAX_HIT_POINTS;
        this.currentHitPoints = maxHitPoints;
        this.combatBoosted = false;
    }

    public abstract UnitType getUnitType();

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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
        return currentActionPoints + extraActionPoints;
    }

    public int getBaseActionPoints() {
        return currentActionPoints;
    }

    public int getExtraActionPoints() {
        return extraActionPoints;
    }

    public void addExtraActionPoints(int bonus) {
        this.extraActionPoints = this.extraActionPoints + bonus;
    }

    public int getVisionRadius() {
        return visionRadius;
    }

    public boolean hasEnoughActionPoints(int amount) {
        return (currentActionPoints + extraActionPoints) >= amount;
    }

    public boolean consumeActionPoints(int amount) {
        if (!hasEnoughActionPoints(amount)) {
            return false;
        }
        int remaining = amount;
        if (extraActionPoints > 0) {
            int consumeFromExtra = Math.min(extraActionPoints, remaining);
            extraActionPoints = extraActionPoints - consumeFromExtra;
            remaining = remaining - consumeFromExtra;
        }
        currentActionPoints = currentActionPoints - remaining;
        return true;
    }

    public void zeroRemainingActionPoints() {
        currentActionPoints = 0;
        extraActionPoints = 0;
    }

    public void refreshActionPoints() {
        currentActionPoints = maxActionPoints;
        extraActionPoints = 0;
    }

    public boolean isIdleWithRemainingActionPoints() {
        return (currentActionPoints + extraActionPoints) > 0;
    }

    public void applyActionPointPenalty(int amount) {
        int remaining = amount;
        if (extraActionPoints > 0) {
            int consumeFromExtra = Math.min(extraActionPoints, remaining);
            extraActionPoints = extraActionPoints - consumeFromExtra;
            remaining = remaining - consumeFromExtra;
        }
        currentActionPoints = currentActionPoints - remaining;
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

    public boolean isCombatBoosted() {
        return combatBoosted;
    }

    public void setCombatBoosted(boolean combatBoosted) {
        this.combatBoosted = combatBoosted;
    }

    public void clearCombatBoost() {
        this.combatBoosted = false;
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
        currentHitPoints = currentActionPoints - amount;
        if (currentHitPoints < 0) {
            currentHitPoints = 0;
        }
    }
}