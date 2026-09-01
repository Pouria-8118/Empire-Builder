package civgame.network.message.game;

import civgame.model.map.HexCoordinate;
import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStartBroadcast extends Broadcast {

    private String gameId;
    private String myClientId;
    private String myUsername;
    private int myPlayerIndex;
    private int mapRadius;
    private Map<String, TileDTO> tiles;
    private PlayerSnapshotDTO mySnapshot;
    private List<OpponentInfoDTO> opponents;
    private int initialTurnNumber;
    private String initialTurnClientId;

    public GameStartBroadcast() {
        super();
        setType(MessageType.GAME_START_BROADCAST);
        this.tiles = new HashMap<>();
        this.opponents = new ArrayList<>();
    }

    public GameStartBroadcast(String broadcasterId, String gameId) {
        super(MessageType.GAME_START_BROADCAST, broadcasterId);
        this.gameId = gameId;
        this.tiles = new HashMap<>();
        this.opponents = new ArrayList<>();
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getMyClientId() {
        return myClientId;
    }

    public void setMyClientId(String myClientId) {
        this.myClientId = myClientId;
    }

    public String getMyUsername() {
        return myUsername;
    }

    public void setMyUsername(String myUsername) {
        this.myUsername = myUsername;
    }

    public int getMyPlayerIndex() {
        return myPlayerIndex;
    }

    public void setMyPlayerIndex(int myPlayerIndex) {
        this.myPlayerIndex = myPlayerIndex;
    }

    public int getMapRadius() {
        return mapRadius;
    }

    public void setMapRadius(int mapRadius) {
        this.mapRadius = mapRadius;
    }

    public Map<String, TileDTO> getTiles() {
        return tiles;
    }

    public void setTiles(Map<String, TileDTO> tiles) {
        this.tiles = tiles;
    }

    public PlayerSnapshotDTO getMySnapshot() {
        return mySnapshot;
    }

    public void setMySnapshot(PlayerSnapshotDTO mySnapshot) {
        this.mySnapshot = mySnapshot;
    }

    public List<OpponentInfoDTO> getOpponents() {
        return opponents;
    }

    public void setOpponents(List<OpponentInfoDTO> opponents) {
        this.opponents = opponents;
    }

    public int getInitialTurnNumber() {
        return initialTurnNumber;
    }

    public void setInitialTurnNumber(int initialTurnNumber) {
        this.initialTurnNumber = initialTurnNumber;
    }

    public String getInitialTurnClientId() {
        return initialTurnClientId;
    }

    public void setInitialTurnClientId(String initialTurnClientId) {
        this.initialTurnClientId = initialTurnClientId;
    }

    public static class TileDTO {
        private int q;
        private int r;
        private String terrainType;
        private String resourceType;
        private String visibility;

        public TileDTO() {
        }

        public TileDTO(int q, int r, String terrainType, String resourceType, String visibility) {
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

        public HexCoordinate toCoordinate() {
            return new HexCoordinate(q, r);
        }
    }

    public static class PlayerSnapshotDTO {
        private int food;
        private int wood;
        private int stone;
        private int iron;
        private int foodCapacity;
        private int woodCapacity;
        private int stoneCapacity;
        private int ironCapacity;
        private String townHallLevel;
        private int townHallCurrentHp;
        private int townHallMaxHp;
        private int townHallDefense;
        private int townHallQ;
        private int townHallR;
        private List<UnitDTO> units;

        public PlayerSnapshotDTO() {
            this.units = new ArrayList<>();
        }

        public int getFood() {
            return food;
        }

        public void setFood(int food) {
            this.food = food;
        }

        public int getWood() {
            return wood;
        }

        public void setWood(int wood) {
            this.wood = wood;
        }

        public int getStone() {
            return stone;
        }

        public void setStone(int stone) {
            this.stone = stone;
        }

        public int getIron() {
            return iron;
        }

        public void setIron(int iron) {
            this.iron = iron;
        }

        public int getFoodCapacity() {
            return foodCapacity;
        }

        public void setFoodCapacity(int foodCapacity) {
            this.foodCapacity = foodCapacity;
        }

        public int getWoodCapacity() {
            return woodCapacity;
        }

        public void setWoodCapacity(int woodCapacity) {
            this.woodCapacity = woodCapacity;
        }

        public int getStoneCapacity() {
            return stoneCapacity;
        }

        public void setStoneCapacity(int stoneCapacity) {
            this.stoneCapacity = stoneCapacity;
        }

        public int getIronCapacity() {
            return ironCapacity;
        }

        public void setIronCapacity(int ironCapacity) {
            this.ironCapacity = ironCapacity;
        }

        public String getTownHallLevel() {
            return townHallLevel;
        }

        public void setTownHallLevel(String townHallLevel) {
            this.townHallLevel = townHallLevel;
        }

        public int getTownHallCurrentHp() {
            return townHallCurrentHp;
        }

        public void setTownHallCurrentHp(int townHallCurrentHp) {
            this.townHallCurrentHp = townHallCurrentHp;
        }

        public int getTownHallMaxHp() {
            return townHallMaxHp;
        }

        public void setTownHallMaxHp(int townHallMaxHp) {
            this.townHallMaxHp = townHallMaxHp;
        }

        public int getTownHallDefense() {
            return townHallDefense;
        }

        public void setTownHallDefense(int townHallDefense) {
            this.townHallDefense = townHallDefense;
        }

        public int getTownHallQ() {
            return townHallQ;
        }

        public void setTownHallQ(int townHallQ) {
            this.townHallQ = townHallQ;
        }

        public int getTownHallR() {
            return townHallR;
        }

        public void setTownHallR(int townHallR) {
            this.townHallR = townHallR;
        }

        public List<UnitDTO> getUnits() {
            return units;
        }

        public void setUnits(List<UnitDTO> units) {
            this.units = units;
        }
    }

    public static class UnitDTO {
        private String id;
        private String unitType;
        private int q;
        private int r;
        private int currentAp;
        private int maxAp;
        private int currentHp;
        private int maxHp;

        public UnitDTO() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUnitType() {
            return unitType;
        }

        public void setUnitType(String unitType) {
            this.unitType = unitType;
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

        public int getCurrentAp() {
            return currentAp;
        }

        public void setCurrentAp(int currentAp) {
            this.currentAp = currentAp;
        }

        public int getMaxAp() {
            return maxAp;
        }

        public void setMaxAp(int maxAp) {
            this.maxAp = maxAp;
        }

        public int getCurrentHp() {
            return currentHp;
        }

        public void setCurrentHp(int currentHp) {
            this.currentHp = currentHp;
        }

        public int getMaxHp() {
            return maxHp;
        }

        public void setMaxHp(int maxHp) {
            this.maxHp = maxHp;
        }
    }

    public static class OpponentInfoDTO {
        private String clientId;
        private String username;
        private int playerIndex;
        private String relation;

        public OpponentInfoDTO() {
        }

        public OpponentInfoDTO(String clientId, String username, int playerIndex, String relation) {
            this.clientId = clientId;
            this.username = username;
            this.playerIndex = playerIndex;
            this.relation = relation;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getPlayerIndex() {
            return playerIndex;
        }

        public void setPlayerIndex(int playerIndex) {
            this.playerIndex = playerIndex;
        }

        public String getRelation() {
            return relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }
    }
}