package civgame.model.unit;

import civgame.model.map.HexCoordinate;

public abstract class Unit {

    private final String id;
    private HexCoordinate coordinate;
    private final int maxActionPoints;
    private int currentActionPoints;
    private final int visionRadius;

    public Unit(String id, HexCoordinate coordinate, int maxActionPoints, int visionRadius) {
        this.id = id;
        this.coordinate = coordinate;
        this.maxActionPoints = maxActionPoints;
        this.currentActionPoints = maxActionPoints;
        this.visionRadius = visionRadius;
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
}