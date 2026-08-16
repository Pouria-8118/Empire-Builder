package civgame.model.map;

public class TradingPost {

    private final HexCoordinate coordinate;

    public TradingPost(HexCoordinate coordinate) {
        this.coordinate = coordinate;
    }

    public HexCoordinate getCoordinate() {
        return coordinate;
    }
}