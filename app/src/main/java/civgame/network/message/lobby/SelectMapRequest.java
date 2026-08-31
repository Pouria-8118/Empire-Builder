package civgame.network.message.lobby;

import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class SelectMapRequest extends Request {
    
    private String mapName;
    
    public SelectMapRequest() {
        super();
        setType(MessageType.SELECT_MAP_REQUEST);
    }
    
    public SelectMapRequest(String senderId, String mapName) {
        super(MessageType.SELECT_MAP_REQUEST, senderId);
        this.mapName = mapName;
    }
    
    public String getMapName() {
        return mapName;
    }
    
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}