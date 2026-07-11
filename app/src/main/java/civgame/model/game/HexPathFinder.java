package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TileVisibility;
import civgame.util.TerrainMovementCostProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class HexPathFinder {

    // Finds the cheapest path (by movement cost) between two hexes using Dijkstra's algorithm.
    // When allowUnseenTiles is false, the search will not travel through hexes that
    // are still fully hidden by fog of war (only Explorer units are allowed to do this).
    public List<HexCoordinate> findPath(HexMap hexMap, HexCoordinate start, HexCoordinate destination, boolean allowUnseenTiles) {
        if (!hexMap.containsCoordinate(start) || !hexMap.containsCoordinate(destination)) {
            return null;
        }
        if (start.equals(destination)) {
            return new ArrayList<HexCoordinate>();
        }

        final Map<HexCoordinate, Integer> costSoFar = new HashMap<HexCoordinate, Integer>();
        Map<HexCoordinate, HexCoordinate> cameFrom = new HashMap<HexCoordinate, HexCoordinate>();

        PriorityQueue<HexCoordinate> frontier = new PriorityQueue<HexCoordinate>(new Comparator<HexCoordinate>() {
            @Override
            public int compare(HexCoordinate first, HexCoordinate second) {
                return costSoFar.get(first) - costSoFar.get(second);
            }
        });

        costSoFar.put(start, 0);
        frontier.add(start);

        while (!frontier.isEmpty()) {
            HexCoordinate current = frontier.poll();

            if (current.equals(destination)) {
                break;
            }

            expandNeighborsForPath(hexMap, current, costSoFar, cameFrom, frontier, allowUnseenTiles);
        }

        if (!cameFrom.containsKey(destination)) {
            return null;
        }

        return buildPathFromCameFromMap(cameFrom, start, destination);
    }

    private void expandNeighborsForPath(HexMap hexMap, HexCoordinate current, Map<HexCoordinate, Integer> costSoFar,
                                         Map<HexCoordinate, HexCoordinate> cameFrom, PriorityQueue<HexCoordinate> frontier,
                                         boolean allowUnseenTiles) {
        List<HexCoordinate> neighbors = current.getNeighbors();

        for (int i = 0; i < neighbors.size(); i++) {
            HexCoordinate neighbor = neighbors.get(i);

            if (!hexMap.containsCoordinate(neighbor)) {
                continue;
            }

            HexTile neighborTile = hexMap.getTile(neighbor);

            if (!allowUnseenTiles && neighborTile.getVisibility() == TileVisibility.UNSEEN) {
                continue;
            }

            int stepCost = TerrainMovementCostProvider.getMovementCost(neighborTile.getTerrainType());
            int newCost = costSoFar.get(current) + stepCost;

            if (!costSoFar.containsKey(neighbor) || newCost < costSoFar.get(neighbor)) {
                costSoFar.put(neighbor, newCost);
                cameFrom.put(neighbor, current);
                frontier.add(neighbor);
            }
        }
    }

    private List<HexCoordinate> buildPathFromCameFromMap(Map<HexCoordinate, HexCoordinate> cameFrom, HexCoordinate start, HexCoordinate destination) {
        LinkedList<HexCoordinate> path = new LinkedList<HexCoordinate>();
        HexCoordinate current = destination;

        while (!current.equals(start)) {
            path.addFirst(current);
            current = cameFrom.get(current);
        }

        return path;
    }

    public MovementPlan truncateToAffordablePath(HexMap hexMap, List<HexCoordinate> fullPath, int availableActionPoints) {
        List<HexCoordinate> affordablePath = new ArrayList<HexCoordinate>();
        int totalCost = 0;

        for (int i = 0; i < fullPath.size(); i++) {
            HexCoordinate coordinate = fullPath.get(i);
            HexTile tile = hexMap.getTile(coordinate);
            int stepCost = TerrainMovementCostProvider.getMovementCost(tile.getTerrainType());

            if (totalCost + stepCost > availableActionPoints) {
                break;
            }

            totalCost = totalCost + stepCost;
            affordablePath.add(coordinate);
        }

        return new MovementPlan(affordablePath, totalCost);
    }

    public Set<HexCoordinate> findReachableTiles(HexMap hexMap, HexCoordinate start, int actionPointBudget, boolean allowUnseenTiles) {
        Set<HexCoordinate> reachableTiles = new HashSet<HexCoordinate>();
        Map<HexCoordinate, Integer> costSoFar = new HashMap<HexCoordinate, Integer>();

        costSoFar.put(start, 0);
        LinkedList<HexCoordinate> frontier = new LinkedList<HexCoordinate>();
        frontier.add(start);

        while (!frontier.isEmpty()) {
            HexCoordinate current = frontier.poll();
            List<HexCoordinate> neighbors = current.getNeighbors();

            for (int i = 0; i < neighbors.size(); i++) {
                HexCoordinate neighbor = neighbors.get(i);
                if (!hexMap.containsCoordinate(neighbor)) {
                    continue;
                }

                HexTile neighborTile = hexMap.getTile(neighbor);

                if (!allowUnseenTiles && neighborTile.getVisibility() == TileVisibility.UNSEEN) {
                    continue;
                }

                int stepCost = TerrainMovementCostProvider.getMovementCost(neighborTile.getTerrainType());
                int newCost = costSoFar.get(current) + stepCost;

                if (newCost > actionPointBudget) {
                    continue;
                }
                if (costSoFar.containsKey(neighbor) && costSoFar.get(neighbor) <= newCost) {
                    continue;
                }

                costSoFar.put(neighbor, newCost);
                reachableTiles.add(neighbor);
                frontier.add(neighbor);
            }
        }

        return reachableTiles;
    }
}