package civgame.model.game;

import civgame.model.building.TownHall;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TileVisibility;
import civgame.model.unit.Unit;
import civgame.util.Constants;

import java.util.List;

public class FogOfWarCalculator {

    public void updateVisibility(GameSession gameSession) {
        HexMap hexMap = gameSession.getHexMap();
        downgradeVisibleTilesToExplored(hexMap);
        revealAroundTownHall(hexMap, gameSession.getTownHall());
        revealAroundUnits(hexMap, gameSession.getUnits());
    }

    private void downgradeVisibleTilesToExplored(HexMap hexMap) {
        for (HexTile tile : hexMap.getAllTiles().values()) {
            if (tile.getVisibility() == TileVisibility.VISIBLE) {
                tile.setVisibility(TileVisibility.EXPLORED);
            }
        }
    }

    private void revealAroundTownHall(HexMap hexMap, TownHall townHall) {
        List<HexTile> tilesInRange = hexMap.getTilesInRange(townHall.getCoordinate(), Constants.TOWN_HALL_VISION_RADIUS);
        markTilesVisible(tilesInRange);
    }

    private void revealAroundUnits(HexMap hexMap, List<Unit> units) {
        for (Unit unit : units) {
            List<HexTile> tilesInRange = hexMap.getTilesInRange(unit.getCoordinate(), unit.getVisionRadius());
            markTilesVisible(tilesInRange);
        }
    }

    private void markTilesVisible(List<HexTile> tiles) {
        for (HexTile tile : tiles) {
            tile.setVisibility(TileVisibility.VISIBLE);
        }
    }

    // TODO : call updateVisibility() again after every unit movement, not just at game start
}