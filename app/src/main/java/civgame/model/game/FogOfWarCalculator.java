package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.map.TileVisibility;
import civgame.model.tribe.Tribe;
import civgame.model.unit.Unit;
import civgame.util.Constants;
import civgame.util.HexLineTracer;

import java.util.List;

public class FogOfWarCalculator {

    public void updateVisibility(GameSession gameSession) {
        HexMap hexMap = gameSession.getHexMap();
        downgradeVisibleTilesToExplored(hexMap);

        revealAround(hexMap, gameSession.getTownHall().getCoordinate(), Constants.TOWN_HALL_VISION_RADIUS);

        List<Building> buildings = gameSession.getAllBuildings();
        for (int i = 0; i < buildings.size(); i++) {
            Building building = buildings.get(i);
            if (building.isDestroyed()) {
                continue;
            }
            revealAround(hexMap, building.getCoordinate(), Constants.BUILDING_VISION_RADIUS);
        }

        List<Unit> units = gameSession.getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            revealAround(hexMap, unit.getCoordinate(), unit.getVisionRadius());
        }

        updateTribeDiscovery(hexMap, gameSession.getTribes());
    }

    private void downgradeVisibleTilesToExplored(HexMap hexMap) {
        for (HexTile tile : hexMap.getAllTiles().values()) {
            if (tile.getVisibility() == TileVisibility.VISIBLE) {
                tile.setVisibility(TileVisibility.EXPLORED);
            }
        }
    }

    public void revealAround(HexMap hexMap, HexCoordinate viewerCoordinate, int visionRadius) {
        for (HexCoordinate coordinate : hexMap.getAllTiles().keySet()) {
            if (viewerCoordinate.distanceTo(coordinate) > visionRadius) {
                continue;
            }
            if (!hasLineOfSight(hexMap, viewerCoordinate, coordinate)) {
                continue;
            }
            hexMap.getTile(coordinate).setVisibility(TileVisibility.VISIBLE);
        }
    }

    private boolean hasLineOfSight(HexMap hexMap, HexCoordinate from, HexCoordinate to) {
        List<HexCoordinate> line = HexLineTracer.traceLine(from, to);

        for (int i = 1; i < line.size() - 1; i++) {
            HexCoordinate coordinate = line.get(i);
            if (!hexMap.containsCoordinate(coordinate)) {
                continue;
            }
            if (hexMap.getTile(coordinate).getTerrainType() == TerrainType.MOUNTAIN_RANGE) {
                return false;
            }
        }

        return true;
    }

    private void updateTribeDiscovery(HexMap hexMap, List<Tribe> tribes) {
        for (int i = 0; i < tribes.size(); i++) {
            Tribe tribe = tribes.get(i);
            if (tribe.isDiscovered()) {
                continue;
            }

            HexTile campTile = hexMap.getTile(tribe.getCampCoordinate());
            if (campTile != null && campTile.getVisibility() == TileVisibility.VISIBLE) {
                tribe.setDiscovered(true);
            }
        }
    }
}