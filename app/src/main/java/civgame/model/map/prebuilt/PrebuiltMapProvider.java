package civgame.model.map.prebuilt;

import java.util.ArrayList;
import java.util.List;

public class PrebuiltMapProvider {
    
    private final List<PrebuiltMap> availableMaps;
    
    public PrebuiltMapProvider() {
        this.availableMaps = new ArrayList<>();
        initializeMaps();
    }
    
    private void initializeMaps() {
        PrebuiltMap1 map1Builder = new PrebuiltMap1();
        availableMaps.add(map1Builder.build());
        
        PrebuiltMap2 map2Builder = new PrebuiltMap2();
        availableMaps.add(map2Builder.build());
        
        PrebuiltMap3 map3Builder = new PrebuiltMap3();
        availableMaps.add(map3Builder.build());
    }
    
    public List<PrebuiltMap> getAvailableMaps() {
        return new ArrayList<>(availableMaps);
    }
    
    public PrebuiltMap getMapByIndex(int index) {
        if (index < 0 || index >= availableMaps.size()) {
            return null;
        }
        return availableMaps.get(index);
    }
    
    public PrebuiltMap getMapByName(String name) {
        for (int i = 0; i < availableMaps.size(); i++) {
            PrebuiltMap map = availableMaps.get(i);
            if (map.getName().equals(name)) {
                return map;
            }
        }
        return null;
    }
    
    public List<PrebuiltMap> getMapsForPlayerCount(int playerCount) {
        List<PrebuiltMap> compatibleMaps = new ArrayList<>();
        
        for (int i = 0; i < availableMaps.size(); i++) {
            PrebuiltMap map = availableMaps.get(i);
            if (map.supportsPlayerCount(playerCount)) {
                compatibleMaps.add(map);
            }
        }
        
        return compatibleMaps;
    }
    
    public int getMapCount() {
        return availableMaps.size();
    }
    
    public List<String> getMapNames() {
        List<String> names = new ArrayList<>();
        
        for (int i = 0; i < availableMaps.size(); i++) {
            names.add(availableMaps.get(i).getName());
        }
        
        return names;
    }
}