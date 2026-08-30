package civgame.model.map.prebuilt;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.map.TileResourceType;

import java.util.ArrayList;
import java.util.List;

public class PrebuiltMap1 {
    
    private static final int MAP_RADIUS = 15;
    private static final String MAP_NAME = "Balanced Plains";
    private static final String MAP_DESCRIPTION = "A medium-sized map with 4 evenly spaced spawn points and fair resource distribution. Ideal for 2-4 players.";
    
    public PrebuiltMap build() {
        HexMap hexMap = new HexMap(MAP_RADIUS);
        
        generateBaseTerrain(hexMap);
        generateResources(hexMap);
        generateStrategicFeatures(hexMap);
        
        List<HexCoordinate> spawnLocations = calculateSpawnLocations();
        clearSpawnAreas(hexMap, spawnLocations);
        
        return new PrebuiltMap(
                MAP_NAME,
                MAP_DESCRIPTION,
                hexMap,
                spawnLocations,
                2,
                4
        );
    }
    
    private List<HexCoordinate> calculateSpawnLocations() {
        List<HexCoordinate> spawns = new ArrayList<>();
        
        spawns.add(new HexCoordinate(0, -10));
        spawns.add(new HexCoordinate(10, 0));
        spawns.add(new HexCoordinate(0, 10));
        spawns.add(new HexCoordinate(-10, 0));
        
        return spawns;
    }
    
    private void generateBaseTerrain(HexMap hexMap) {
        for (HexCoordinate coordinate : hexMap.getAllTiles().keySet()) {
            int distanceFromCenter = coordinate.distanceTo(new HexCoordinate(0, 0));
            
            if (distanceFromCenter >= 13) {
                HexTile tile = new HexTile(coordinate, TerrainType.MOUNTAIN_RANGE, TileResourceType.NONE);
                hexMap.setTile(coordinate, tile);
            } else if (distanceFromCenter >= 10) {
                HexTile tile = new HexTile(coordinate, TerrainType.MOUNTAIN, TileResourceType.STONE);
                hexMap.setTile(coordinate, tile);
            } else {
                HexTile tile = new HexTile(coordinate, TerrainType.PLAIN, TileResourceType.NONE);
                hexMap.setTile(coordinate, tile);
            }
        }
    }
    
    private void generateResources(HexMap hexMap) {
        generateResourceCluster(hexMap, new HexCoordinate(5, -5), TileResourceType.WOOD, 6);
        generateResourceCluster(hexMap, new HexCoordinate(5, 5), TileResourceType.WOOD, 6);
        generateResourceCluster(hexMap, new HexCoordinate(-5, 5), TileResourceType.WOOD, 6);
        generateResourceCluster(hexMap, new HexCoordinate(-5, -5), TileResourceType.WOOD, 6);
        
        generateResourceCluster(hexMap, new HexCoordinate(8, -8), TileResourceType.STONE, 4);
        generateResourceCluster(hexMap, new HexCoordinate(8, 8), TileResourceType.STONE, 4);
        generateResourceCluster(hexMap, new HexCoordinate(-8, 8), TileResourceType.STONE, 4);
        generateResourceCluster(hexMap, new HexCoordinate(-8, -8), TileResourceType.STONE, 4);
        
        generateResourceCluster(hexMap, new HexCoordinate(3, 0), TileResourceType.IRON, 2);
        generateResourceCluster(hexMap, new HexCoordinate(0, 3), TileResourceType.IRON, 2);
        generateResourceCluster(hexMap, new HexCoordinate(-3, 0), TileResourceType.IRON, 2);
        generateResourceCluster(hexMap, new HexCoordinate(0, -3), TileResourceType.IRON, 2);
        
        generateResourceCluster(hexMap, new HexCoordinate(2, -2), TileResourceType.WHEAT, 3);
        generateResourceCluster(hexMap, new HexCoordinate(2, 2), TileResourceType.WHEAT, 3);
        generateResourceCluster(hexMap, new HexCoordinate(-2, 2), TileResourceType.WHEAT, 3);
        generateResourceCluster(hexMap, new HexCoordinate(-2, -2), TileResourceType.WHEAT, 3);
    }
    
    private void generateResourceCluster(HexMap hexMap, HexCoordinate center, TileResourceType resourceType, int count) {
        List<HexCoordinate> coordinates = new ArrayList<>();
        coordinates.add(center);
        coordinates.addAll(center.getNeighbors());
        
        int placed = 0;
        for (int i = 0; i < coordinates.size() && placed < count; i++) {
            HexCoordinate coordinate = coordinates.get(i);
            if (!hexMap.containsCoordinate(coordinate)) {
                continue;
            }
            
            HexTile currentTile = hexMap.getTile(coordinate);
            if (currentTile.getTerrainType() == TerrainType.MOUNTAIN_RANGE) {
                continue;
            }
            
            TerrainType terrain = determineTerrainForResource(resourceType);
            HexTile tile = new HexTile(coordinate, terrain, resourceType);
            hexMap.setTile(coordinate, tile);
            placed++;
        }
    }
    
    private TerrainType determineTerrainForResource(TileResourceType resourceType) {
        if (resourceType == TileResourceType.WOOD) {
            return TerrainType.FOREST;
        }
        if (resourceType == TileResourceType.STONE || resourceType == TileResourceType.IRON) {
            return TerrainType.MOUNTAIN;
        }
        if (resourceType == TileResourceType.WHEAT || resourceType == TileResourceType.RICE) {
            return TerrainType.GRASSLAND;
        }
        return TerrainType.PLAIN;
    }
    
    private void generateStrategicFeatures(HexMap hexMap) {
        HexTile seaTile1 = new HexTile(new HexCoordinate(11, -2), TerrainType.SEA, TileResourceType.FISH);
        HexTile seaTile2 = new HexTile(new HexCoordinate(11, -1), TerrainType.SEA, TileResourceType.FISH);
        HexTile seaTile3 = new HexTile(new HexCoordinate(11, 0), TerrainType.SEA, TileResourceType.FISH);
        hexMap.setTile(seaTile1.getCoordinate(), seaTile1);
        hexMap.setTile(seaTile2.getCoordinate(), seaTile2);
        hexMap.setTile(seaTile3.getCoordinate(), seaTile3);
        
        HexTile seaTile4 = new HexTile(new HexCoordinate(-11, 2), TerrainType.SEA, TileResourceType.FISH);
        HexTile seaTile5 = new HexTile(new HexCoordinate(-11, 1), TerrainType.SEA, TileResourceType.FISH);
        HexTile seaTile6 = new HexTile(new HexCoordinate(-11, 0), TerrainType.SEA, TileResourceType.FISH);
        hexMap.setTile(seaTile4.getCoordinate(), seaTile4);
        hexMap.setTile(seaTile5.getCoordinate(), seaTile5);
        hexMap.setTile(seaTile6.getCoordinate(), seaTile6);
    }
    
    private void clearSpawnAreas(HexMap hexMap, List<HexCoordinate> spawnLocations) {
        for (int i = 0; i < spawnLocations.size(); i++) {
            HexCoordinate spawn = spawnLocations.get(i);
            
            HexTile spawnTile = new HexTile(spawn, TerrainType.PLAIN, TileResourceType.NONE);
            hexMap.setTile(spawn, spawnTile);
            
            List<HexCoordinate> neighbors = spawn.getNeighbors();
            for (int j = 0; j < neighbors.size(); j++) {
                HexCoordinate neighbor = neighbors.get(j);
                if (hexMap.containsCoordinate(neighbor)) {
                    HexTile neighborTile = new HexTile(neighbor, TerrainType.PLAIN, TileResourceType.NONE);
                    hexMap.setTile(neighbor, neighborTile);
                }
            }
        }
    }
}