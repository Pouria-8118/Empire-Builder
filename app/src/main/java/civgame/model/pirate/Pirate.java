package civgame.model.pirate;

import civgame.model.combat.Attackable;
import civgame.model.map.HexCoordinate;

public class Pirate implements Attackable, java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private HexCoordinate coordinate;
    private int currentHitPoints;
    private final int maxHitPoints;
    private final int maxActionPoints;
    private int currentActionPoints;

    public Pirate(String id, HexCoordinate coordinate, int maxHitPoints, int maxActionPoints) {
        this.id = id;
        this.coordinate = coordinate;
        this.maxHitPoints = maxHitPoints;
        this.currentHitPoints = maxHitPoints;
        this.maxActionPoints = maxActionPoints;
        this.currentActionPoints = maxActionPoints;
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