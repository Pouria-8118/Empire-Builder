package civgame.network.message.lobby;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;

public class MapSelectedBroadcast extends Broadcast {
    
    private String mapName;
    private String selectedByClientId;
    private String selectedByUsername;
    
    public MapSelectedBroadcast() {
        super();
        setType(MessageType.MAP_SELECTED_BROADCAST);
    }
    
    public MapSelectedBroadcast(String broadcasterId,
                                String mapName,
                                String selectedByClientId,
                                String selectedByUsername) {
        super(MessageType.MAP_SELECTED_BROADCAST, broadcasterId);
        this.mapName = mapName;
        this.selectedByClientId = selectedByClientId;
        this.selectedByUsername = selectedByUsername;
    }
    
    public String getMapName() {
        return mapName;
    }
    
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
    
    public String getSelectedByClientId() {
        return selectedByClientId;
    }
    
    public void setSelectedByClientId(String selectedByClientId) {
        this.selectedByClientId = selectedByClientId;
    }
    
    public String getSelectedByUsername() {
        return selectedByUsername;
    }
    
    public void setSelectedByUsername(String selectedByUsername) {
        this.selectedByUsername = selectedByUsername;
    }
}