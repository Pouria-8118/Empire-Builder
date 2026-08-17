package civgame.model.map;

public class TradingPost implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final HexCoordinate coordinate;

    public TradingPost(HexCoordinate coordinate) {
        this.coordinate = coordinate;
    }

    public HexCoordinate getCoordinate() {
        return coordinate;
    }
}