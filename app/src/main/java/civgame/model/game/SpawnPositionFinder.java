package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.unit.Unit;

import java.util.List;

public class SpawnPositionFinder {

    public HexCoordinate findFreeNeighbor(GameSession gameSession, HexCoordinate center) {
        List<HexCoordinate> neighbors = center.getNeighbors();

        for (int i = 0; i < neighbors.size(); i++) {
            HexCoordinate neighbor = neighbors.get(i);
            if (!gameSession.getHexMap().containsCoordinate(neighbor)) {
                continue;
            }
            if (isCoordinateFreeOfUnits(gameSession, neighbor)) {
                return neighbor;
            }
        }

        return neighbors.get(0);
    }

    private boolean isCoordinateFreeOfUnits(GameSession gameSession, HexCoordinate coordinate) {
        List<Unit> units = gameSession.getUnits();

        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getCoordinate().equals(coordinate)) {
                return false;
            }
        }

        return true;
    }
}