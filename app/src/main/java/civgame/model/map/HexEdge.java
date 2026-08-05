package civgame.model.map;

public class HexEdge {

    private final HexCoordinate first;
    private final HexCoordinate second;

    public HexEdge(HexCoordinate coordinateA, HexCoordinate coordinateB) {
        if (isBefore(coordinateA, coordinateB)) {
            this.first = coordinateA;
            this.second = coordinateB;
        } else {
            this.first = coordinateB;
            this.second = coordinateA;
        }
    }

    private boolean isBefore(HexCoordinate coordinateA, HexCoordinate coordinateB) {
        if (coordinateA.getQ() != coordinateB.getQ()) {
            return coordinateA.getQ() < coordinateB.getQ();
        }
        return coordinateA.getR() < coordinateB.getR();
    }

    public HexCoordinate getFirst() {
        return first;
    }

    public HexCoordinate getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }

        HexEdge otherEdge = (HexEdge) otherObject;
        return first.equals(otherEdge.first) && second.equals(otherEdge.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }
}