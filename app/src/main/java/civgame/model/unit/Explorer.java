package civgame.model.unit;

import civgame.model.map.HexCoordinate;

public class Explorer extends Unit {

    private boolean autoExploreEnabled;

    public Explorer(String id, HexCoordinate coordinate, int maxActionPoints, int visionRadius) {
        super(id, coordinate, maxActionPoints, visionRadius);
        this.autoExploreEnabled = false;
    }

    @Override
    public UnitType getUnitType() {
        return UnitType.EXPLORER;
    }

    public boolean isAutoExploreEnabled() {
        return autoExploreEnabled;
    }

    public void setAutoExploreEnabled(boolean autoExploreEnabled) {
        this.autoExploreEnabled = autoExploreEnabled;
    }
}