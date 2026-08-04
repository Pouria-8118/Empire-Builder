package civgame.model.building;

import civgame.model.combat.Attackable;
import civgame.model.map.HexCoordinate;
import civgame.util.Constants;

public class TownHall implements Attackable {

    private final HexCoordinate coordinate;
    private TownHallLevel level;
    private int currentHitPoints;
    private int maxHitPoints;
    private int defense;

    public TownHall(HexCoordinate coordinate) {
        this.coordinate = coordinate;
        this.level = TownHallLevel.BASE_CAMP;
        this.maxHitPoints = Constants.TOWN_HALL_BASE_HIT_POINTS;
        this.currentHitPoints = maxHitPoints;
        this.defense = Constants.TOWN_HALL_BASE_DEFENSE;
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

    @Override
    public int getCurrentHitPoints() {
        return currentHitPoints;
    }

    @Override
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

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    // TODO: decide and implement what happens at the game level
    // when the Town Hall itself reaches 0 HP through combat.
}