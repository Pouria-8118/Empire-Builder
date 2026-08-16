package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TileVisibility;
import civgame.util.EdgeMovementCostProvider;
import civgame.util.TerrainMovementCostProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class HexPathFinder {

    public List<HexCoordinate> findPath(HexMap hexMap, HexCoordinate start, HexCoordinate destination, boolean allowUnseenTiles, boolean navalTechUnlocked, SeasonType season) {
        Map<HexCoordinate, Integer> costSoFar = new HashMap<HexCoordinate, Integer>();
        Map<HexCoordinate, HexCoordinate> cameFrom = new HashMap<HexCoordinate, HexCoordinate>();

        costSoFar.put(start, 0);
        PriorityQueue<HexCoordinate> frontier = new PriorityQueue<HexCoordinate>((a, b) -> costSoFar.get(a) - costSoFar.get(b));
        frontier.add(start);

        while (!frontier.isEmpty()) {
            HexCoordinate current = frontier.poll();

            if (current.equals(destination)) {
                break;
            }

            expandNeighborsForPath(hexMap, current, costSoFar, cameFrom, frontier, allowUnseenTiles, navalTechUnlocked, season);
        }

        if (!cameFrom.containsKey(destination) && !start.equals(destination)) {
            return null;
        }

        return reconstructPath(cameFrom, start, destination);
    }

    private List<HexCoordinate> reconstructPath(Map<HexCoordinate, HexCoordinate> cameFrom, HexCoordinate start, HexCoordinate destination) {
        List<HexCoordinate> path = new ArrayList<HexCoordinate>();
        HexCoordinate current = destination;

        while (!current.equals(start)) {
            path.add(0, current);
            current = cameFrom.get(current);
            if (current == null) {
                return null;
            }
        }

        return path;
    }

    private void expandNeighborsForPath(HexMap hexMap, HexCoordinate current, Map<HexCoordinate, Integer> costSoFar,
                                         Map<HexCoordinate, HexCoordinate> cameFrom, PriorityQueue<HexCoordinate> frontier,
                                         boolean allowUnseenTiles, boolean navalTechUnlocked, SeasonType season) {
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
            if (!TerrainMovementCostProvider.isPassable(neighborTile.getTerrainType(), navalTechUnlocked)) {
                continue;
            }

            int baseCost = TerrainMovementCostProvider.getMovementCost(neighborTile.getTerrainType(), season);
            int stepCost = EdgeMovementCostProvider.getStepCost(hexMap, current, neighbor, baseCost);
            int newCost = costSoFar.get(current) + stepCost;

            if (!costSoFar.containsKey(neighbor) || newCost < costSoFar.get(neighbor)) {
                costSoFar.put(neighbor, newCost);
                cameFrom.put(neighbor, current);
                frontier.add(neighbor);
            }
        }
    }

    public Set<HexCoordinate> findReachableTiles(HexMap hexMap, HexCoordinate start, int actionPointBudget, boolean allowUnseenTiles, boolean navalTechUnlocked, SeasonType season) {
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
                if (!TerrainMovementCostProvider.isPassable(neighborTile.getTerrainType(), navalTechUnlocked)) {
                    continue;
                }

                int baseCost = TerrainMovementCostProvider.getMovementCost(neighborTile.getTerrainType(), season);
                int stepCost = EdgeMovementCostProvider.getStepCost(hexMap, current, neighbor, baseCost);
                int newCost = costSoFar.get(current) + stepCost;

                if (newCost > actionPointBudget) {
                    continue;
                }

                if (!costSoFar.containsKey(neighbor) || newCost < costSoFar.get(neighbor)) {
                    costSoFar.put(neighbor, newCost);
                    reachableTiles.add(neighbor);
                    frontier.add(neighbor);
                }
            }
        }

        return reachableTiles;
    }

    public MovementPlan truncateToAffordablePath(HexMap hexMap, HexCoordinate startCoordinate, List<HexCoordinate> fullPath, int availableActionPoints, SeasonType season) {
        List<HexCoordinate> affordablePath = new ArrayList<HexCoordinate>();
        int totalCost = 0;
        HexCoordinate previousCoordinate = startCoordinate;

        for (int i = 0; i < fullPath.size(); i++) {
            HexCoordinate coordinate = fullPath.get(i);
            HexTile tile = hexMap.getTile(coordinate);
            int baseCost = TerrainMovementCostProvider.getMovementCost(tile.getTerrainType(), season);
            int stepCost = EdgeMovementCostProvider.getStepCost(hexMap, previousCoordinate, coordinate, baseCost);

            if (totalCost + stepCost > availableActionPoints) {
                break;
            }

            totalCost = totalCost + stepCost;
            affordablePath.add(coordinate);
            previousCoordinate = coordinate;
        }

        return new MovementPlan(affordablePath, totalCost);
    }
}