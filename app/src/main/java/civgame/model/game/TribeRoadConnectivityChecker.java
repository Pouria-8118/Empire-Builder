package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexTile;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class TribeRoadConnectivityChecker {

    public boolean isConnected(GameSession gameSession, HexCoordinate tribeCampCoordinate) {
        Set<HexCoordinate> targetSet = new HashSet<HexCoordinate>(tribeCampCoordinate.getNeighbors());

        Set<HexCoordinate> visited = new HashSet<HexCoordinate>();
        Queue<HexCoordinate> frontier = new LinkedList<HexCoordinate>();

        List<Building> allBuildings = gameSession.getAllBuildings();
        for (int i = 0; i < allBuildings.size(); i++) {
            Building building = allBuildings.get(i);
            if (building.isDestroyed()) {
                continue;
            }
            HexCoordinate coordinate = building.getCoordinate();
            if (visited.add(coordinate)) {
                frontier.add(coordinate);
            }
        }

        while (!frontier.isEmpty()) {
            HexCoordinate current = frontier.poll();

            if (targetSet.contains(current)) {
                return true;
            }

            List<HexCoordinate> neighbors = current.getNeighbors();
            for (int i = 0; i < neighbors.size(); i++) {
                HexCoordinate neighbor = neighbors.get(i);
                if (visited.contains(neighbor) || !gameSession.getHexMap().containsCoordinate(neighbor)) {
                    continue;
                }

                HexTile neighborTile = gameSession.getHexMap().getTile(neighbor);
                boolean canStepHere = neighborTile.isRoadPresent() || targetSet.contains(neighbor);

                if (canStepHere) {
                    visited.add(neighbor);
                    frontier.add(neighbor);
                }
            }
        }

        return false;
    }
}