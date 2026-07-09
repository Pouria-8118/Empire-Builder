package civgame.model.game;

import civgame.model.map.HexCoordinate;

import java.util.List;

public class MovementPlan {

    private final List<HexCoordinate> path;
    private final int totalCost;

    public MovementPlan(List<HexCoordinate> path, int totalCost) {
        this.path = path;
        this.totalCost = totalCost;
    }

    public List<HexCoordinate> getPath() {
        return path;
    }

    public int getTotalCost() {
        return totalCost;
    }
}