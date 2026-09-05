package civgame.network.message.lobby;

import civgame.network.message.MessageType;
import civgame.network.message.Response;
import civgame.server.lobby.PlayerSlot;

import java.util.ArrayList;
import java.util.List;

public class JoinLobbyResponse extends Response {

    private String myClientId;
    private String lobbyId;
    private String hostClientId;
    private List<PlayerSlot.PlayerSlotInfo> currentPlayers;
    private String selectedMapName;
    private List<String> availableMaps;

    public JoinLobbyResponse() {
        super();
        setType(MessageType.JOIN_LOBBY_RESPONSE);
        this.currentPlayers = new ArrayList<>();
        this.availableMaps = new ArrayList<>();
    }

    public JoinLobbyResponse(String requestId,
                             boolean success,
                             String myClientId,
                             String lobbyId,
                             String hostClientId,
                             List<PlayerSlot.PlayerSlotInfo> currentPlayers,
                             String selectedMapName,
                             List<String> availableMaps) {
        super(MessageType.JOIN_LOBBY_RESPONSE, requestId, success);
        this.myClientId = myClientId;
        this.lobbyId = lobbyId;
        this.hostClientId = hostClientId;
        this.currentPlayers = currentPlayers != null ? currentPlayers : new ArrayList<>();
        this.selectedMapName = selectedMapName;
        this.availableMaps = availableMaps != null ? availableMaps : new ArrayList<>();
    }

    public JoinLobbyResponse(String requestId, String errorMessage) {
        super(MessageType.JOIN_LOBBY_RESPONSE, requestId, errorMessage);
        this.currentPlayers = new ArrayList<>();
        this.availableMaps = new ArrayList<>();
    }

    public String getMyClientId() {
        return myClientId;
    }

    public void setMyClientId(String myClientId) {
        this.myClientId = myClientId;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getHostClientId() {
        return hostClientId;
    }

    public void setHostClientId(String hostClientId) {
        this.hostClientId = hostClientId;
    }

    public List<PlayerSlot.PlayerSlotInfo> getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(List<PlayerSlot.PlayerSlotInfo> currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public String getSelectedMapName() {
        return selectedMapName;
    }

    public void setSelectedMapName(String selectedMapName) {
        this.selectedMapName = selectedMapName;
    }

    public List<String> getAvailableMaps() {
        return availableMaps;
    }

    public void setAvailableMaps(List<String> availableMaps) {
        this.availableMaps = availableMaps;
    }
}