package civgame.network.message.game;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;

import java.util.ArrayList;
import java.util.List;

public class FogRevealBroadcast extends Broadcast {

    private String revealerClientId;
    private List<RevealedTileDTO> revealedTiles;

    public FogRevealBroadcast() {
        super();
        setType(MessageType.FOG_REVEAL_BROADCAST);
        this.revealedTiles = new ArrayList<>();
    }

    public FogRevealBroadcast(String broadcasterId) {
        super(MessageType.FOG_REVEAL_BROADCAST, broadcasterId);
        this.revealedTiles = new ArrayList<>();
    }

    public String getRevealerClientId() {
        return revealerClientId;
    }

    public void setRevealerClientId(String revealerClientId) {
        this.revealerClientId = revealerClientId;
    }

    public List<RevealedTileDTO> getRevealedTiles() {
        return revealedTiles;
    }

    public void setRevealedTiles(List<RevealedTileDTO> revealedTiles) {
        this.revealedTiles = revealedTiles;
    }

    public static class RevealedTileDTO {
        private int q;
        private int r;
        private String terrainType;
        private String resourceType;
        private String visibility;

        public RevealedTileDTO() {
        }

        public RevealedTileDTO(int q, int r, String terrainType, String resourceType, String visibility) {
            this.q = q;
            this.r = r;
            this.terrainType = terrainType;
            this.resourceType = resourceType;
            this.visibility = visibility;
        }

        public int getQ() {
            return q;
        }

        public void setQ(int q) {
            this.q = q;
        }

        public int getR() {
            return r;
        }

        public void setR(int r) {
            this.r = r;
        }

        public String getTerrainType() {
            return terrainType;
        }

        public void setTerrainType(String terrainType) {
            this.terrainType = terrainType;
        }

        public String getResourceType() {
            return resourceType;
        }

        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }

        public String getVisibility() {
            return visibility;
        }

        public void setVisibility(String visibility) {
            this.visibility = visibility;
        }
    }
}