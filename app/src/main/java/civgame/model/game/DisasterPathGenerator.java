package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.map.TerrainType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DisasterPathGenerator {

    private final Random random;

    public DisasterPathGenerator(Random random) {
        this.random = random;
    }

    public List<HexCoordinate> growPath(GameSession gameSession, HexCoordinate start, int length, boolean avoidSea, boolean avoidMountain) {
        List<HexCoordinate> path = new ArrayList<HexCoordinate>();
        HexCoordinate current = start;
        path.add(current);

        for (int i = 1; i < length; i++) {
            List<HexCoordinate> neighbors = current.getNeighbors();
            Collections.shuffle(neighbors, random);
            HexCoordinate next = null;

            for (int j = 0; j < neighbors.size(); j++) {
                HexCoordinate candidate = neighbors.get(j);
                if (!gameSession.getHexMap().containsCoordinate(candidate) || path.contains(candidate)) {
                    continue;
                }

                TerrainType terrain = gameSession.getHexMap().getTile(candidate).getTerrainType();
                if (avoidSea && terrain == TerrainType.SEA) {
                    continue;
                }
                if (avoidMountain && (terrain == TerrainType.MOUNTAIN || terrain == TerrainType.MOUNTAIN_RANGE || terrain == TerrainType.VOLCANO)) {
                    continue;
                }

                next = candidate;
                break;
            }

            if (next == null) {
                break;
            }

            path.add(next);
            current = next;
        }

        return path;
    }
}