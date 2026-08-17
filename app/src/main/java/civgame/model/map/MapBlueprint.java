package civgame.model.map;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MapBlueprint implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int mapRadius;
    private final Map<HexCoordinate, HexTile> tiles;
    private final Map<HexEdge, HexEdgeFeatures> edgeFeatures;
    private HexCoordinate townHallCoordinate;

    public MapBlueprint(int mapRadius) {
        this.mapRadius = mapRadius;
        this.tiles = new HashMap<HexCoordinate, HexTile>();
        this.edgeFeatures = new HashMap<HexEdge, HexEdgeFeatures>();
        this.townHallCoordinate = null;

        for (int q = -mapRadius; q <= mapRadius; q++) {
            int rStart = Math.max(-mapRadius, -q - mapRadius);
            int rEnd = Math.min(mapRadius, -q + mapRadius);
            for (int r = rStart; r <= rEnd; r++) {
                HexCoordinate coordinate = new HexCoordinate(q, r);
                tiles.put(coordinate, new HexTile(coordinate, TerrainType.PLAIN, TileResourceType.NONE));
            }
        }
    }

    public int getMapRadius() {
        return mapRadius;
    }

    public Map<HexCoordinate, HexTile> getTiles() {
        return tiles;
    }

    public void setTile(HexCoordinate coordinate, HexTile tile) {
        tiles.put(coordinate, tile);
    }

    public HexTile getTile(HexCoordinate coordinate) {
        return tiles.get(coordinate);
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

    public Map<HexEdge, HexEdgeFeatures> getEdgeFeatures() {
        return edgeFeatures;
    }

    public HexCoordinate getTownHallCoordinate() {
        return townHallCoordinate;
    }

    public void setTownHallCoordinate(HexCoordinate townHallCoordinate) {
        this.townHallCoordinate = townHallCoordinate;
    }

    public HexMap toHexMap() {
        HexMap hexMap = new HexMap(mapRadius);

        for (Map.Entry<HexCoordinate, HexTile> entry : tiles.entrySet()) {
            hexMap.setTile(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<HexEdge, HexEdgeFeatures> entry : edgeFeatures.entrySet()) {
            HexEdgeFeatures targetFeatures = hexMap.getOrCreateEdgeFeatures(entry.getKey().getFirst(), entry.getKey().getSecond());
            targetFeatures.setRiverPresent(entry.getValue().isRiverPresent());
            if (entry.getValue().hasActiveWall()) {
                targetFeatures.setWall(entry.getValue().getWall());
            }
        }

        return hexMap;
    }
}