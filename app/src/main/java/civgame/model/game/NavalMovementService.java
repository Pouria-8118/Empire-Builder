package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.TerrainType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class NavalMovementService {

    public Set<HexCoordinate> findReachableSeaTiles(HexMap hexMap, HexCoordinate start, int actionPointBudget) {
        Set<HexCoordinate> reachable = new HashSet<HexCoordinate>();
        Map<HexCoordinate, Integer> costSoFar = new HashMap<HexCoordinate, Integer>();
        costSoFar.put(start, 0);

        Queue<HexCoordinate> frontier = new LinkedList<HexCoordinate>();
        frontier.add(start);

        while (!frontier.isEmpty()) {
            HexCoordinate current = frontier.poll();
            List<HexCoordinate> neighbors = current.getNeighbors();

            for (int i = 0; i < neighbors.size(); i++) {
                HexCoordinate neighbor = neighbors.get(i);
                if (!hexMap.containsCoordinate(neighbor) || hexMap.getTile(neighbor).getTerrainType() != TerrainType.SEA) {
                    continue;
                }

                int newCost = costSoFar.get(current) + 1;
                if (newCost > actionPointBudget) {
                    continue;
                }
                if (!costSoFar.containsKey(neighbor) || newCost < costSoFar.get(neighbor)) {
                    costSoFar.put(neighbor, newCost);
                    reachable.add(neighbor);
                    frontier.add(neighbor);
                }
            }
        }

        return reachable;
    }

    public List<HexCoordinate> findSeaPath(HexMap hexMap, HexCoordinate start, HexCoordinate destination) {
        Map<HexCoordinate, HexCoordinate> cameFrom = new HashMap<HexCoordinate, HexCoordinate>();
        Set<HexCoordinate> visited = new HashSet<HexCoordinate>();
        Queue<HexCoordinate> frontier = new LinkedList<HexCoordinate>();

        visited.add(start);
        frontier.add(start);

        while (!frontier.isEmpty()) {
            HexCoordinate current = frontier.poll();
            if (current.equals(destination)) {
                break;
            }

            List<HexCoordinate> neighbors = current.getNeighbors();
            for (int i = 0; i < neighbors.size(); i++) {
                HexCoordinate neighbor = neighbors.get(i);
                if (visited.contains(neighbor) || !hexMap.containsCoordinate(neighbor)) {
                    continue;
                }
                if (hexMap.getTile(neighbor).getTerrainType() != TerrainType.SEA) {
                    continue;
                }

                visited.add(neighbor);
                cameFrom.put(neighbor, current);
                frontier.add(neighbor);
            }
        }

        if (!cameFrom.containsKey(destination)) {
            return null;
        }

        List<HexCoordinate> path = new ArrayList<HexCoordinate>();
        HexCoordinate current = destination;
        while (!current.equals(start)) {
            path.add(0, current);
            current = cameFrom.get(current);
        }

        return path;
    }

    public boolean isValidNavalDestination(HexMap hexMap, HexCoordinate destination) {
        return hexMap.containsCoordinate(destination) && hexMap.getTile(destination).getTerrainType() == TerrainType.SEA;
    }
}