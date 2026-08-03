package civgame.model.building;

import civgame.model.map.HexCoordinate;
import civgame.util.Constants;

public class TownHall {

    private final HexCoordinate coordinate;
    private TownHallLevel level;
    private int currentHitPoints;
    private int maxHitPoints;

    public TownHall(HexCoordinate coordinate) {
        this.coordinate = coordinate;
        this.level = TownHallLevel.BASE_CAMP;
        this.maxHitPoints = Constants.TOWN_HALL_BASE_HIT_POINTS;
        this.currentHitPoints = maxHitPoints;
    }

    public HexCoordinate getCoordinate() {
        return coordinate;
    }

    public TownHallLevel getLevel() {
        return level;
    }

    public void setLevel(TownHallLevel level) {
        this.level = level;
    }

    public boolean isAtLeastLevel(TownHallLevel requiredLevel) {
        return level.ordinal() >= requiredLevel.ordinal();
    }

    public int getCurrentHitPoints() {
        return currentHitPoints;
    }

    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    public void setMaxHitPoints(int maxHitPoints) {
        this.maxHitPoints = maxHitPoints;
    }

    public void heal(int amount) {
        currentHitPoints = currentHitPoints + amount;
        if (currentHitPoints > maxHitPoints) {
            currentHitPoints = maxHitPoints;
        }
    }

    // TODO: implement generic combat damage handling here once TownHall
    // implements the Attackable interface alongside Building and military Unit subclasses.
}