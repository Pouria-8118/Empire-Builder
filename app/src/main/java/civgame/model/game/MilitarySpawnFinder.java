package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.unit.UnitType;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class MilitarySpawnFinder {

    private static final int MAX_TILES_TO_CHECK = 40;

    public HexCoordinate findSpawnCoordinate(GameSession gameSession, UnitType unitType) {
        HexCoordinate townHallCoordinate = gameSession.getTownHall().getCoordinate();

        Set<HexCoordinate> visited = new HashSet<HexCoordinate>();
        Queue<HexCoordinate> frontier = new LinkedList<HexCoordinate>();

        visited.add(townHallCoordinate);
        frontier.add(townHallCoordinate);

        int tilesChecked = 0;

        while (!frontier.isEmpty() && tilesChecked < MAX_TILES_TO_CHECK) {
            HexCoordinate current = frontier.poll();

            if (MilitaryStackingRules.canAcceptUnit(gameSession, current, unitType)) {
                return current;
            }

            List<HexCoordinate> neighbors = current.getNeighbors();
            for (int i = 0; i < neighbors.size(); i++) {
                HexCoordinate neighbor = neighbors.get(i);
                if (visited.contains(neighbor) || !gameSession.getHexMap().containsCoordinate(neighbor)) {
                    continue;
                }
                visited.add(neighbor);
                tilesChecked = tilesChecked + 1;
                frontier.add(neighbor);
            }
        }

        return townHallCoordinate;
    }
}