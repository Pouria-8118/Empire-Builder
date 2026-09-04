package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.unit.UnitType;

public class EnemyUnitMarker implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final String unitId;
    private final String ownerClientId;
    private final String ownerUsername;
    private final UnitType unitType;
    private HexCoordinate coordinate;
    private int currentHitPoints;
    private int maxHitPoints;
    private boolean floating;

    public EnemyUnitMarker(String unitId,
                            String ownerClientId,
                            String ownerUsername,
                            UnitType unitType,
                            HexCoordinate coordinate,
                            int currentHitPoints,
                            int maxHitPoints,
                            boolean floating) {
        this.unitId = unitId;
        this.ownerClientId = ownerClientId;
        this.ownerUsername = ownerUsername;
        this.unitType = unitType;
        this.coordinate = coordinate;
        this.currentHitPoints = currentHitPoints;
        this.maxHitPoints = maxHitPoints;
        this.floating = floating;
    }

    public String getUnitId() {
        return unitId;
    }

    public String getOwnerClientId() {
        return ownerClientId;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public HexCoordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(HexCoordinate coordinate) {
        this.coordinate = coordinate;
    }

    public int getCurrentHitPoints() {
        return currentHitPoints;
    }

    public void setCurrentHitPoints(int currentHitPoints) {
        this.currentHitPoints = currentHitPoints;
    }

    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    public void setMaxHitPoints(int maxHitPoints) {
        this.maxHitPoints = maxHitPoints;
    }

    public boolean isFloating() {
        return floating;
    }

    public void setFloating(boolean floating) {
        this.floating = floating;
    }

    public boolean isMilitaryUnit() {
        return unitType == UnitType.SWORDSMAN
                || unitType == UnitType.ARCHER
                || unitType == UnitType.CAVALRY
                || unitType == UnitType.CATAPULT
                || unitType == UnitType.GALLEY;
    }
}