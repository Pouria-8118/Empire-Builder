package civgame.model.map;

public class HexEdgeFeatures {

    private boolean riverPresent;
    private Wall wall;

    public boolean isRiverPresent() {
        return riverPresent;
    }

    public void setRiverPresent(boolean riverPresent) {
        this.riverPresent = riverPresent;
    }

    public boolean hasActiveWall() {
        return wall != null && wall.isActive();
    }

    public Wall getWall() {
        return wall;
    }

    public void setWall(Wall wall) {
        this.wall = wall;
    }
}