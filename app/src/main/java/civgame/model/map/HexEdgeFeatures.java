package civgame.model.map;

public class HexEdgeFeatures implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

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