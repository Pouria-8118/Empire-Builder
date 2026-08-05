package civgame.model.map;

public class HexEdgeFeatures {

    private boolean riverPresent;

    // TODO: add a wallPresent flag here once the Wall model is introduced,
    // since a wall is also built on the edge between two hexes.

    public boolean isRiverPresent() {
        return riverPresent;
    }

    public void setRiverPresent(boolean riverPresent) {
        this.riverPresent = riverPresent;
    }
}