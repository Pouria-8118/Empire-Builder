package civgame.model.unit;

import civgame.model.map.HexCoordinate;

public class Explorer extends Unit {

    public Explorer(String id, HexCoordinate coordinate, int maxActionPoints, int visionRadius) {
        super(id, coordinate, maxActionPoints, visionRadius);
    }

    @Override
    public UnitType getUnitType() {
        return UnitType.EXPLORER;
    }

    // TODO (bonus): add an auto-explore toggle and target-tile tracking here
}