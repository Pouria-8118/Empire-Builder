package civgame.model.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HexMap {

    private final int mapRadius;
    private final Map<HexCoordinate, HexTile> tiles;

    public HexMap(int mapRadius) {
        this.mapRadius = mapRadius;
        this.tiles = new HashMap<HexCoordinate, HexTile>();
        buildBaseTiles();
    }

    // Builds only the grid structure (which coordinates exist). Actual terrain
    // and resource assignment is handled separately by HexMapGenerator.
    private void buildBaseTiles() {
        for (int q = -mapRadius; q <= mapRadius; q++) {
            int rStart = Math.max(-mapRadius, -q - mapRadius);
            int rEnd = Math.min(mapRadius, -q + mapRadius);

            for (int r = rStart; r <= rEnd; r++) {
                HexCoordinate coordinate = new HexCoordinate(q, r);
                HexTile tile = new HexTile(coordinate, TerrainType.PLAIN, TileResourceType.NONE);
                tiles.put(coordinate, tile);
            }
        }
    }

    public HexTile getTile(HexCoordinate coordinate) {
        return tiles.get(coordinate);
    }

    public boolean containsCoordinate(HexCoordinate coordinate) {
        return tiles.containsKey(coordinate);
    }

    public void setTile(HexCoordinate coordinate, HexTile tile) {
        tiles.put(coordinate, tile);
    }

    public Map<HexCoordinate, HexTile> getAllTiles() {
        return tiles;
    }

    public int getMapRadius() {
        return mapRadius;
    }

    public List<HexTile> getTilesInRange(HexCoordinate center, int range) {
        List<HexTile> tilesInRange = new ArrayList<HexTile>();

        for (Map.Entry<HexCoordinate, HexTile> entry : tiles.entrySet()) {
            HexCoordinate coordinate = entry.getKey();
            if (center.distanceTo(coordinate) <= range) {
                tilesInRange.add(entry.getValue());
            }
        }

        return tilesInRange;
    }

    // TODO : add a method that recalculates VISIBLE/EXPLORED/UNSEEN
    // state for every tile based on the vision radius of all player units and buildings
}