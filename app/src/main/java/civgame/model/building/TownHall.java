package civgame.model.building;

import civgame.model.map.HexCoordinate;

public class TownHall {

    private final HexCoordinate coordinate;

    public TownHall(HexCoordinate coordinate) {
        this.coordinate = coordinate;
    }

    public HexCoordinate getCoordinate() {
        return coordinate;
    }

}