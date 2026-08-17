package civgame.model.map;

import java.util.ArrayList;
import java.util.List;

public class HexCoordinate implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private static final int[][] NEIGHBOR_DIRECTIONS = {
            {1, 0},
            {1, -1},
            {0, -1},
            {-1, 0},
            {-1, 1},
            {0, 1}
    };

    private final int q;
    private final int r;

    public HexCoordinate(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    public List<HexCoordinate> getNeighbors() {
        List<HexCoordinate> neighbors = new ArrayList<HexCoordinate>();

        for (int i = 0; i < NEIGHBOR_DIRECTIONS.length; i++) {
            int neighborQ = this.q + NEIGHBOR_DIRECTIONS[i][0];
            int neighborR = this.r + NEIGHBOR_DIRECTIONS[i][1];
            neighbors.add(new HexCoordinate(neighborQ, neighborR));
        }

        return neighbors;
    }

    public int distanceTo(HexCoordinate other) {
        int deltaQ = Math.abs(this.q - other.q);
        int deltaR = Math.abs(this.r - other.r);
        int deltaS = Math.abs((-this.q - this.r) - (-other.q - other.r));

        return Math.max(deltaQ, Math.max(deltaR, deltaS));
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }

        HexCoordinate otherCoordinate = (HexCoordinate) otherObject;
        return this.q == otherCoordinate.q && this.r == otherCoordinate.r;
    }

    @Override
    public int hashCode() {
        int result = q;
        result = 31 * result + r;
        return result;
    }

    @Override
    public String toString() {
        return "(" + q + ", " + r + ")";
    }
}