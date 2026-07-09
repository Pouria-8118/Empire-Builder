package civgame.model.unit;

import civgame.model.map.HexCoordinate;

public class Builder extends Unit {

    private int remainingCharges;

    public Builder(String id, HexCoordinate coordinate, int maxActionPoints, int visionRadius, int startingCharges) {
        super(id, coordinate, maxActionPoints, visionRadius);
        this.remainingCharges = startingCharges;
    }

    @Override
    public UnitType getUnitType() {
        return UnitType.BUILDER;
    }

    public int getRemainingCharges() {
        return remainingCharges;
    }

    public boolean isOutOfCharges() {
        return remainingCharges <= 0;
    }

    public void consumeCharge() {
        if (remainingCharges > 0) {
            remainingCharges = remainingCharges - 1;
        }
    }

    // TODO : call consumeCharge() whenever this builder finishes constructing a building
}