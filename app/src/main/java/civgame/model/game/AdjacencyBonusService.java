package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.building.BuildingType;
import civgame.model.event.BuildingDestroyedEvent;
import civgame.model.event.BuildingPlacedEvent;
import civgame.model.event.GameEvent;
import civgame.model.event.GameEventListener;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdjacencyBonusService implements GameEventListener {

    private final Map<Building, Integer> buildingBonusCache;
    private int farmSynergyBonus;
    private GameSession gameSession;

    public AdjacencyBonusService() {
        this.buildingBonusCache = new HashMap<Building, Integer>();
        this.farmSynergyBonus = 0;
    }

    public void subscribeTo(GameSession gameSession) {
        this.gameSession = gameSession;
        gameSession.getEventBus().subscribe(this);
    }

    @Override
    public void onGameEvent(GameEvent event) {
        if (event instanceof BuildingPlacedEvent || event instanceof BuildingDestroyedEvent) {
            recalculateAll();
        }
    }

    public int getBuildingBonus(Building building) {
        Integer bonus = buildingBonusCache.get(building);
        if (bonus == null) {
            return 0;
        }
        return bonus;
    }

    public int getFarmSynergyBonus() {
        return farmSynergyBonus;
    }

    public void recalculateAll() {
        if (gameSession == null) {
            return;
        }

        buildingBonusCache.clear();

        List<Building> allBuildings = gameSession.getAllBuildings();
        for (int i = 0; i < allBuildings.size(); i++) {
            Building building = allBuildings.get(i);
            if (building.isDestroyed()) {
                continue;
            }

            if (building.getBuildingType() == BuildingType.LUMBER_MILL) {
                buildingBonusCache.put(building, calculateCoastalLumberBonus(building));
            } else if (building.getBuildingType() == BuildingType.STONE_MINE || building.getBuildingType() == BuildingType.IRON_MINE) {
                buildingBonusCache.put(building, calculateDeepMineBonus(building));
            }
        }

        farmSynergyBonus = calculateFarmSynergyBonus(allBuildings);
    }

    private int calculateCoastalLumberBonus(Building building) {
        List<HexCoordinate> neighbors = building.getCoordinate().getNeighbors();

        for (int i = 0; i < neighbors.size(); i++) {
            HexTile neighborTile = gameSession.getHexMap().getTile(neighbors.get(i));
            if (neighborTile != null && neighborTile.getTerrainType() == TerrainType.SEA) {
                return Constants.COASTAL_LUMBER_MILL_BONUS;
            }
        }

        return 0;
    }

    private int calculateDeepMineBonus(Building building) {
        List<HexCoordinate> neighbors = building.getCoordinate().getNeighbors();
        int mountainNeighborCount = 0;

        for (int i = 0; i < neighbors.size(); i++) {
            HexTile neighborTile = gameSession.getHexMap().getTile(neighbors.get(i));
            if (neighborTile != null && neighborTile.getTerrainType() == TerrainType.MOUNTAIN) {
                mountainNeighborCount = mountainNeighborCount + 1;
            }
        }

        if (mountainNeighborCount >= Constants.DEEP_MINE_MIN_MOUNTAIN_NEIGHBORS) {
            return Constants.DEEP_MINE_BONUS;
        }

        return 0;
    }

    private int calculateFarmSynergyBonus(List<Building> allBuildings) {
        int adjacentFarmPairCount = 0;

        for (int i = 0; i < allBuildings.size(); i++) {
            Building buildingA = allBuildings.get(i);
            if (buildingA.isDestroyed() || buildingA.getBuildingType() != BuildingType.FARM) {
                continue;
            }

            for (int j = i + 1; j < allBuildings.size(); j++) {
                Building buildingB = allBuildings.get(j);
                if (buildingB.isDestroyed() || buildingB.getBuildingType() != BuildingType.FARM) {
                    continue;
                }

                if (buildingA.getCoordinate().distanceTo(buildingB.getCoordinate()) == 1) {
                    adjacentFarmPairCount = adjacentFarmPairCount + 1;
                }
            }
        }

        return adjacentFarmPairCount * Constants.FARM_SYNERGY_BONUS_PER_PAIR;
    }
}