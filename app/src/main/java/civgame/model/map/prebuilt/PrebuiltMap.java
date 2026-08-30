package civgame.model.map.prebuilt;

import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;

import java.util.ArrayList;
import java.util.List;

public class PrebuiltMap {
    
    private final String name;
    private final String description;
    private final HexMap hexMap;
    private final List<HexCoordinate> spawnLocations;
    private final int maxPlayers;
    private final int minPlayers;
    
    public PrebuiltMap(String name, 
                       String description, 
                       HexMap hexMap, 
                       List<HexCoordinate> spawnLocations,
                       int minPlayers,
                       int maxPlayers) {
        this.name = name;
        this.description = description;
        this.hexMap = hexMap;
        this.spawnLocations = new ArrayList<>(spawnLocations);
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public HexMap getHexMap() {
        return hexMap;
    }
    
    public List<HexCoordinate> getSpawnLocations() {
        return new ArrayList<>(spawnLocations);
    }
    
    public HexCoordinate getSpawnLocation(int playerIndex) {
        if (playerIndex < 0 || playerIndex >= spawnLocations.size()) {
            return null;
        }
        return spawnLocations.get(playerIndex);
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public int getMinPlayers() {
        return minPlayers;
    }
    
    public boolean supportsPlayerCount(int playerCount) {
        return playerCount >= minPlayers && playerCount <= maxPlayers;
    }
    
    public int getSpawnLocationCount() {
        return spawnLocations.size();
    }
}