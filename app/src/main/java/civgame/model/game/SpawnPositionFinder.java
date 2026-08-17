package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.unit.Unit;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class SpawnPositionFinder implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private static final int MAX_TILES_TO_CHECK = 40;

    public HexCoordinate findFreeNeighbor(GameSession gameSession, HexCoordinate center) {
        Set<HexCoordinate> visited = new HashSet<HexCoordinate>();
        Queue<HexCoordinate> frontier = new LinkedList<HexCoordinate>();

        visited.add(center);
        frontier.add(center);

        int tilesChecked = 0;

        while (!frontier.isEmpty() && tilesChecked < MAX_TILES_TO_CHECK) {
            HexCoordinate current = frontier.poll();
            List<HexCoordinate> neighbors = current.getNeighbors();

            for (int i = 0; i < neighbors.size(); i++) {
                HexCoordinate neighbor = neighbors.get(i);

                if (visited.contains(neighbor)) {
                    continue;
                }
                visited.add(neighbor);

                if (!gameSession.getHexMap().containsCoordinate(neighbor)) {
                    continue;
                }

                tilesChecked = tilesChecked + 1;

                boolean isTownHall = neighbor.equals(gameSession.getTownHall().getCoordinate());
                if (!isTownHall && isCoordinateFreeOfUnits(gameSession, neighbor)) {
                    return neighbor;
                }

                frontier.add(neighbor);
            }
        }

        return center;
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