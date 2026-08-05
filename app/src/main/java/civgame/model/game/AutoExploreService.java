package civgame.model.game;

import civgame.model.building.TownHallUpgradeType;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TileVisibility;
import civgame.model.unit.Explorer;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class AutoExploreService {

    private final HexPathFinder pathFinder;

    public AutoExploreService() {
        this.pathFinder = new HexPathFinder();
    }

    public void runAutoExploreForAllUnits(GameSession gameSession) {
        List<Unit> units = new ArrayList<Unit>(gameSession.getUnits());

        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (unit.getUnitType() != UnitType.EXPLORER) {
                continue;
            }

            Explorer explorer = (Explorer) unit;
            if (explorer.isAutoExploreEnabled()) {
                performAutoExploreStep(gameSession, explorer);
            }
        }
    }

    private void performAutoExploreStep(GameSession gameSession, Explorer explorer) {
        if (explorer.getCurrentActionPoints() <= 0) {
            return;
        }

        HexCoordinate targetCoordinate = findNearestUnseenFrontier(gameSession, explorer);
        if (targetCoordinate == null) {
            explorer.setAutoExploreEnabled(false);
            return;
        }

        boolean navalTechUnlocked = gameSession.getTownHallUpgradeService().isUnlocked(TownHallUpgradeType.NAVAL_TECHNIQUES);
        List<HexCoordinate> fullPath = pathFinder.findPath(gameSession.getHexMap(), explorer.getCoordinate(), targetCoordinate, true, navalTechUnlocked);
        if (fullPath == null || fullPath.isEmpty()) {
            return;
        }

        MovementPlan movementPlan = pathFinder.truncateToAffordablePath(gameSession.getHexMap(), explorer.getCoordinate(), fullPath, explorer.getCurrentActionPoints());
        if (movementPlan.getPath().isEmpty()) {
            return;
        }

        HexCoordinate destination = movementPlan.getPath().get(movementPlan.getPath().size() - 1);
        explorer.consumeActionPoints(movementPlan.getTotalCost());
        explorer.setCoordinate(destination);
    }

    private HexCoordinate findNearestUnseenFrontier(GameSession gameSession, Explorer explorer) {
        HexMap hexMap = gameSession.getHexMap();
        Queue<HexCoordinate> frontierQueue = new LinkedList<HexCoordinate>();
        Set<HexCoordinate> visited = new HashSet<HexCoordinate>();

        frontierQueue.add(explorer.getCoordinate());
        visited.add(explorer.getCoordinate());

        while (!frontierQueue.isEmpty()) {
            HexCoordinate current = frontierQueue.poll();
            HexTile currentTile = hexMap.getTile(current);

            boolean isUnseen = currentTile != null && currentTile.getVisibility() == TileVisibility.UNSEEN;
            if (isUnseen && !current.equals(explorer.getCoordinate())) {
                return current;
            }

            List<HexCoordinate> neighbors = current.getNeighbors();
            for (int i = 0; i < neighbors.size(); i++) {
                HexCoordinate neighbor = neighbors.get(i);
                if (!hexMap.containsCoordinate(neighbor) || visited.contains(neighbor)) {
                    continue;
                }
                visited.add(neighbor);
                frontierQueue.add(neighbor);
            }
        }

        return null;
    }
}