package civgame.model.unit;

import civgame.model.map.HexCoordinate;

public class BorderExpander extends Unit {

    public BorderExpander(String id, HexCoordinate coordinate, int maxActionPoints, int visionRadius) {
        super(id, coordinate, maxActionPoints, visionRadius);
    }

    @Override
    public UnitType getUnitType() {
        return UnitType.BORDER_EXPANDER;
    }

}