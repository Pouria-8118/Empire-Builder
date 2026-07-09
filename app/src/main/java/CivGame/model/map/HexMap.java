package civgame.model.map;

import java.util.HashMap;
import java.util.Map;

public class HexMap {

    private final int mapRadius;
    private final Map<HexCoordinate, HexTile> tiles;

    public HexMap(int mapRadius) {
        this.mapRadius = mapRadius;
        this.tiles = new HashMap<HexCoordinate, HexTile>();
        buildPlaceholderTiles();
    }

    // TODO : replace this placeholder generation with the real procedural
    // map generator (mountain ranges, dense forests, forest within 2 hexes of Town Hall)
    private void buildPlaceholderTiles() {
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

    // TODO : add a method to query which tiles are currently visible
    // to the player, based on fog of war state
}