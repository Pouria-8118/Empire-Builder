package civgame.model.game;

import civgame.model.map.HexCoordinate;

public class ActiveDisasterEffect {

    private final NaturalDisasterType type;
    private final HexCoordinate centerCoordinate;
    private int remainingTicks;

    public ActiveDisasterEffect(NaturalDisasterType type, HexCoordinate centerCoordinate, int durationTicks) {
        this.type = type;
        this.centerCoordinate = centerCoordinate;
        this.remainingTicks = durationTicks;
    }

    public NaturalDisasterType getType() {
        return type;
    }

    public HexCoordinate getCenterCoordinate() {
        return centerCoordinate;
    }

    public int getRemainingTicks() {
        return remainingTicks;
    }

    public void decrementTicks() {
        remainingTicks = remainingTicks - 1;
    }

    public boolean isFinished() {
        return remainingTicks <= 0;
    }
}