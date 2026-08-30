package civgame.model.map.prebuilt;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.map.TileResourceType;

import java.util.ArrayList;
import java.util.List;

public class PrebuiltMap2 {
    
    private static final int MAP_RADIUS = 18;
    private static final String MAP_NAME = "Contested Valleys";
    private static final String MAP_DESCRIPTION = "A larger map with 6 spawn points separated by mountain ranges and rivers. Perfect for 3-6 players.";
    
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
                3,
                6
        );
    }
    
    private List<HexCoordinate> calculateSpawnLocations() {
        List<HexCoordinate> spawns = new ArrayList<>();
        
        spawns.add(new HexCoordinate(0, -12));
        spawns.add(new HexCoordinate(10, -6));
        spawns.add(new HexCoordinate(10, 6));
        spawns.add(new HexCoordinate(0, 12));
        spawns.add(new HexCoordinate(-10, 6));
        spawns.add(new HexCoordinate(-10, -6));
        
        return spawns;
    }
    
    private void generateBaseTerrain(HexMap hexMap) {
        for (HexCoordinate coordinate : hexMap.getAllTiles().keySet()) {
            int distanceFromCenter = coordinate.distanceTo(new HexCoordinate(0, 0));
            
            if (distanceFromCenter >= 16) {
                HexTile tile = new HexTile(coordinate, TerrainType.MOUNTAIN_RANGE, TileResourceType.NONE);
                hexMap.setTile(coordinate, tile);
            } else if (distanceFromCenter >= 14) {
                HexTile tile = new HexTile(coordinate, TerrainType.SEA, TileResourceType.NONE);
                hexMap.setTile(coordinate, tile);
            } else {
                HexTile tile = new HexTile(coordinate, TerrainType.GRASSLAND, TileResourceType.NONE);
                hexMap.setTile(coordinate, tile);
            }
        }
    }
    
    private void generateResources(HexMap hexMap) {
        generateResourceCluster(hexMap, new HexCoordinate(6, -9), TileResourceType.WOOD, 8);
        generateResourceCluster(hexMap, new HexCoordinate(12, -3), TileResourceType.WOOD, 8);
        generateResourceCluster(hexMap, new HexCoordinate(12, 3), TileResourceType.WOOD, 8);
        generateResourceCluster(hexMap, new HexCoordinate(6, 9), TileResourceType.WOOD, 8);
        generateResourceCluster(hexMap, new HexCoordinate(-6, 9), TileResourceType.WOOD, 8);
        generateResourceCluster(hexMap, new HexCoordinate(-12, 3), TileResourceType.WOOD, 8);
        generateResourceCluster(hexMap, new HexCoordinate(-12, -3), TileResourceType.WOOD, 8);
        generateResourceCluster(hexMap, new HexCoordinate(-6, -9), TileResourceType.WOOD, 8);
        
        generateResourceCluster(hexMap, new HexCoordinate(4, -7), TileResourceType.STONE, 5);
        generateResourceCluster(hexMap, new HexCoordinate(8, 0), TileResourceType.STONE, 5);
        generateResourceCluster(hexMap, new HexCoordinate(4, 7), TileResourceType.STONE, 5);
        generateResourceCluster(hexMap, new HexCoordinate(-4, 7), TileResourceType.STONE, 5);
        generateResourceCluster(hexMap, new HexCoordinate(-8, 0), TileResourceType.STONE, 5);
        generateResourceCluster(hexMap, new HexCoordinate(-4, -7), TileResourceType.STONE, 5);
        
        generateResourceCluster(hexMap, new HexCoordinate(2, -4), TileResourceType.IRON, 3);
        generateResourceCluster(hexMap, new HexCoordinate(4, 0), TileResourceType.IRON, 3);
        generateResourceCluster(hexMap, new HexCoordinate(2, 4), TileResourceType.IRON, 3);
        generateResourceCluster(hexMap, new HexCoordinate(-2, 4), TileResourceType.IRON, 3);
        generateResourceCluster(hexMap, new HexCoordinate(-4, 0), TileResourceType.IRON, 3);
        generateResourceCluster(hexMap, new HexCoordinate(-2, -4), TileResourceType.IRON, 3);
        
        generateResourceCluster(hexMap, new HexCoordinate(3, -6), TileResourceType.WHEAT, 6);
        generateResourceCluster(hexMap, new HexCoordinate(6, 0), TileResourceType.WHEAT, 6);
        generateResourceCluster(hexMap, new HexCoordinate(3, 6), TileResourceType.WHEAT, 6);
        generateResourceCluster(hexMap, new HexCoordinate(-3, 6), TileResourceType.WHEAT, 6);
        generateResourceCluster(hexMap, new HexCoordinate(-6, 0), TileResourceType.WHEAT, 6);
        generateResourceCluster(hexMap, new HexCoordinate(-3, -6), TileResourceType.WHEAT, 6);
    }
    
    private void generateResourceCluster(HexMap hexMap, HexCoordinate center, TileResourceType resourceType, int count) {
        List<HexCoordinate> coordinates = new ArrayList<>();
        coordinates.add(center);
        coordinates.addAll(center.getNeighbors());
        
        for (int i = 0; i < coordinates.size(); i++) {
            HexCoordinate neighbor = coordinates.get(i);
            coordinates.addAll(neighbor.getNeighbors());
        }
        
        int placed = 0;
        for (int i = 0; i < coordinates.size() && placed < count; i++) {
            HexCoordinate coordinate = coordinates.get(i);
            if (!hexMap.containsCoordinate(coordinate)) {
                continue;
            }
            
            HexTile currentTile = hexMap.getTile(coordinate);
            if (currentTile.getTerrainType() == TerrainType.MOUNTAIN_RANGE || 
                currentTile.getTerrainType() == TerrainType.SEA) {
                continue;
            }
            
            if (currentTile.getResourceType() != TileResourceType.NONE) {
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
        generateMountainBarrier(hexMap, new HexCoordinate(0, -6), new HexCoordinate(0, 6));
        generateMountainBarrier(hexMap, new HexCoordinate(6, -3), new HexCoordinate(-6, 3));
        generateMountainBarrier(hexMap, new HexCoordinate(-6, -3), new HexCoordinate(6, 3));
        
        generateSeaArea(hexMap, new HexCoordinate(0, 0), 3);
    }
    
    private void generateMountainBarrier(HexMap hexMap, HexCoordinate start, HexCoordinate end) {
        HexCoordinate current = start;
        
        while (!current.equals(end)) {
            if (hexMap.containsCoordinate(current)) {
                HexTile tile = new HexTile(current, TerrainType.MOUNTAIN, TileResourceType.NONE);
                hexMap.setTile(current, tile);
            }
            
            List<HexCoordinate> neighbors = current.getNeighbors();
            HexCoordinate bestNext = null;
            int bestDistance = Integer.MAX_VALUE;
            
            for (int i = 0; i < neighbors.size(); i++) {
                HexCoordinate neighbor = neighbors.get(i);
                if (!hexMap.containsCoordinate(neighbor)) {
                    continue;
                }
                
                int distance = neighbor.distanceTo(end);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestNext = neighbor;
                }
            }
            
            if (bestNext == null || bestNext.equals(current)) {
                break;
            }
            
            current = bestNext;
        }
    }
    
    private void generateSeaArea(HexMap hexMap, HexCoordinate center, int radius) {
        for (HexCoordinate coordinate : hexMap.getAllTiles().keySet()) {
            if (coordinate.distanceTo(center) <= radius) {
                HexTile currentTile = hexMap.getTile(coordinate);
                if (currentTile.getTerrainType() != TerrainType.MOUNTAIN_RANGE) {
                    TileResourceType resource = TileResourceType.NONE;
                    if (coordinate.distanceTo(center) == radius) {
                        resource = TileResourceType.FISH;
                    }
                    HexTile tile = new HexTile(coordinate, TerrainType.SEA, resource);
                    hexMap.setTile(coordinate, tile);
                }
            }
        }
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