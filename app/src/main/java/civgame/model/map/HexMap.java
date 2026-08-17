package civgame.model.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HexMap implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final int mapRadius;
    private final Map<HexCoordinate, HexTile> tiles;
    private final Map<HexEdge, HexEdgeFeatures> edgeFeatures;

    public HexMap(int mapRadius) {
        this.mapRadius = mapRadius;
        this.tiles = new HashMap<HexCoordinate, HexTile>();
        this.edgeFeatures = new HashMap<HexEdge, HexEdgeFeatures>();
        buildBaseTiles();
    }

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

    public HexEdgeFeatures getEdgeFeatures(HexCoordinate coordinateA, HexCoordinate coordinateB) {
        return edgeFeatures.get(new HexEdge(coordinateA, coordinateB));
    }

    public HexEdgeFeatures getOrCreateEdgeFeatures(HexCoordinate coordinateA, HexCoordinate coordinateB) {
        HexEdge edge = new HexEdge(coordinateA, coordinateB);
        HexEdgeFeatures features = edgeFeatures.get(edge);

        if (features == null) {
            features = new HexEdgeFeatures();
            edgeFeatures.put(edge, features);
        }

        return features;
    }

    public boolean hasRiver(HexCoordinate coordinateA, HexCoordinate coordinateB) {
        HexEdgeFeatures features = getEdgeFeatures(coordinateA, coordinateB);
        return features != null && features.isRiverPresent();
    }

    public Set<HexEdge> getRiverEdges() {
        Set<HexEdge> riverEdges = new HashSet<HexEdge>();

        for (Map.Entry<HexEdge, HexEdgeFeatures> entry : edgeFeatures.entrySet()) {
            if (entry.getValue().isRiverPresent()) {
                riverEdges.add(entry.getKey());
            }
        }

        return riverEdges;
    }

    public boolean hasActiveWall(HexCoordinate coordinateA, HexCoordinate coordinateB) {
        HexEdgeFeatures features = getEdgeFeatures(coordinateA, coordinateB);
        return features != null && features.hasActiveWall();
    }

    public Map<HexEdge, Wall> getAllActiveWalls() {
        Map<HexEdge, Wall> walls = new HashMap<HexEdge, Wall>();

        for (Map.Entry<HexEdge, HexEdgeFeatures> entry : edgeFeatures.entrySet()) {
            if (entry.getValue().hasActiveWall()) {
                walls.put(entry.getKey(), entry.getValue().getWall());
            }
        }

        return walls;
    }

    public void removeWall(HexEdge edge) {
        HexEdgeFeatures features = edgeFeatures.get(edge);
        if (features != null) {
            features.setWall(null);
        }
    }
}