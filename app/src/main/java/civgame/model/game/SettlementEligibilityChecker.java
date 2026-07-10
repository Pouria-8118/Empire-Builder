package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.map.TileResourceType;
import civgame.model.map.TileVisibility;

import java.util.Map;

public class SettlementEligibilityChecker {

    public HexCoordinate findEligibleTile(GameSession gameSession) {
        Map<HexCoordinate, HexTile> allTiles = gameSession.getHexMap().getAllTiles();

        for (Map.Entry<HexCoordinate, HexTile> entry : allTiles.entrySet()) {
            if (isEligible(gameSession, entry.getKey(), entry.getValue())) {
                return entry.getKey();
            }
        }

        return null;
    }

    private boolean isEligible(GameSession gameSession, HexCoordinate coordinate, HexTile tile) {
        if (coordinate.equals(gameSession.getTownHall().getCoordinate())) {
            return false;
        }
        if (!gameSession.isWithinBorder(coordinate)) {
            return false;
        }
        if (tile.getVisibility() == TileVisibility.UNSEEN) {
            return false;
        }
        if (gameSession.isSettlementTile(coordinate)) {
            return false;
        }
        if (gameSession.getBuildingAt(coordinate) != null) {
            return false;
        }

        return isTerrainFreeOfSpecialResource(tile);
    }

    private boolean isTerrainFreeOfSpecialResource(HexTile tile) {
        if (tile.getTerrainType() == TerrainType.FOREST) {
            return false;
        }
        if (tile.getTerrainType() == TerrainType.MOUNTAIN) {
            return !tile.isIronDepositPresent();
        }
        return tile.getResourceType() == TileResourceType.NONE;
    }
}