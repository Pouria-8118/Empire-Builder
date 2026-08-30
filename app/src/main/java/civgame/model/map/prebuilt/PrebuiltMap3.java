package civgame.model.map.prebuilt;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.map.TileResourceType;

import java.util.ArrayList;
import java.util.List;

public class PrebuiltMap3 {
    
    private static final int MAP_RADIUS = 20;
    private static final String MAP_NAME = "Archipelago Wars";
    private static final String MAP_DESCRIPTION = "A massive map with islands separated by seas. 8 spawn points across multiple islands. For 4-8 players.";
    
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
                4,
                8
        );
    }
    
    private List<HexCoordinate> calculateSpawnLocations() {
        List<HexCoordinate> spawns = new ArrayList<>();
        
        spawns.add(new HexCoordinate(0, -14));
        spawns.add(new HexCoordinate(12, -7));
        spawns.add(new HexCoordinate(12, 7));
        spawns.add(new HexCoordinate(0, 14));
        spawns.add(new HexCoordinate(-12, 7));
        spawns.add(new HexCoordinate(-12, -7));
        spawns.add(new HexCoordinate(7, 0));
        spawns.add(new HexCoordinate(-7, 0));
        
        return spawns;
    }
    
    private void generateBaseTerrain(HexMap hexMap) {
        for (HexCoordinate coordinate : hexMap.getAllTiles().keySet()) {
            HexTile tile = new HexTile(coordinate, TerrainType.SEA, TileResourceType.NONE);
            hexMap.setTile(coordinate, tile);
        }
    }
    
    private void generateResources(HexMap hexMap) {
        generateIsland(hexMap, new HexCoordinate(0, -14), 8, 0);
        generateIsland(hexMap, new HexCoordinate(12, -7), 8, 1);
        generateIsland(hexMap, new HexCoordinate(12, 7), 8, 2);
        generateIsland(hexMap, new HexCoordinate(0, 14), 8, 3);
        generateIsland(hexMap, new HexCoordinate(-12, 7), 8, 4);
        generateIsland(hexMap, new HexCoordinate(-12, -7), 8, 5);
        generateIsland(hexMap, new HexCoordinate(7, 0), 6, 6);
        generateIsland(hexMap, new HexCoordinate(-7, 0), 6, 7);
    }
    
    private void generateIsland(HexMap hexMap, HexCoordinate center, int radius, int islandId) {
        for (HexCoordinate coordinate : hexMap.getAllTiles().keySet()) {
            int distance = coordinate.distanceTo(center);
            
            if (distance <= radius) {
                if (distance == radius) {
                    HexTile tile = new HexTile(coordinate, TerrainType.PLAIN, TileResourceType.NONE);
                    hexMap.setTile(coordinate, tile);
                } else if (distance <= 2) {
                    HexTile tile = new HexTile(coordinate, TerrainType.PLAIN, TileResourceType.NONE);
                    hexMap.setTile(coordinate, tile);
                } else {
                    HexTile tile = generateIslandTerrain(coordinate, islandId, distance);
                    hexMap.setTile(coordinate, tile);
                }
            }
        }
    }
    
    private HexTile generateIslandTerrain(HexCoordinate coordinate, int islandId, int distance) {
        int hashValue = Math.abs(coordinate.getQ() * 31 + coordinate.getR() * 17 + islandId);
        int terrainChoice = hashValue % 10;
        
        if (terrainChoice < 3) {
            return new HexTile(coordinate, TerrainType.FOREST, TileResourceType.WOOD);
        } else if (terrainChoice < 5) {
            return new HexTile(coordinate, TerrainType.MOUNTAIN, TileResourceType.STONE);
        } else if (terrainChoice < 6) {
            HexTile tile = new HexTile(coordinate, TerrainType.MOUNTAIN, TileResourceType.IRON);
            tile.setIronDepositPresent(true);
            return tile;
        } else if (terrainChoice < 8) {
            return new HexTile(coordinate, TerrainType.GRASSLAND, TileResourceType.WHEAT);
        } else {
            return new HexTile(coordinate, TerrainType.PLAIN, TileResourceType.CATTLE);
        }
    }
    
    private void generateStrategicFeatures(HexMap hexMap) {
        for (HexCoordinate coordinate : hexMap.getAllTiles().keySet()) {
            HexTile tile = hexMap.getTile(coordinate);
            if (tile.getTerrainType() == TerrainType.SEA) {
                int hashValue = Math.abs(coordinate.getQ() * 23 + coordinate.getR() * 13);
                if (hashValue % 8 == 0) {
                    HexTile fishTile = new HexTile(coordinate, TerrainType.SEA, TileResourceType.FISH);
                    hexMap.setTile(coordinate, fishTile);
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