package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;

import java.util.List;

public class DockResourceFinder {

    private DockResourceFinder() {
    }

    public static HexTile findAdjacentFishTile(GameSession gameSession, HexCoordinate dockCoordinate) {
        List<HexCoordinate> neighbors = dockCoordinate.getNeighbors();

        for (int i = 0; i < neighbors.size(); i++) {
            HexCoordinate neighborCoordinate = neighbors.get(i);
            if (!gameSession.getHexMap().containsCoordinate(neighborCoordinate)) {
                continue;
            }

            HexTile neighborTile = gameSession.getHexMap().getTile(neighborCoordinate);
            if (neighborTile.getTerrainType() == TerrainType.SEA && neighborTile.hasResource()) {
                return neighborTile;
            }
        }

        return null;
    }
}