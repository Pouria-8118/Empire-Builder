package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.building.BuildingType;
import civgame.model.unit.MilitaryUnitCapProvider;
import civgame.model.building.TownHall;
import civgame.model.event.GameEventBus;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.resource.ResourceStorage;
import civgame.model.unit.Unit;
import civgame.model.animal.WildAnimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameSession {

    private final HexMap hexMap;
    private final TownHall townHall;
    private final ResourceStorage resourceStorage;
    private final List<Unit> units;
    private final Map<HexCoordinate, Building> buildings;
    private final Set<HexCoordinate> claimedBorderTiles;
    private final Set<HexCoordinate> settlementTiles;
    private final TownHallUpgradeService townHallUpgradeService;
    private final TownHallLevelService townHallLevelService;
    private final TownHallProductionQueue townHallProductionQueue;
    private final GameEventBus eventBus;
    private final AdjacencyBonusService adjacencyBonusService;
    private final HappinessService happinessService;
    private final List<WildAnimal> wildAnimals;
    private int unitCap;
    private int currentTurnNumber;
    private boolean inStarvation;

    public GameSession(HexMap hexMap, TownHall townHall, int initialUnitCap) {
        this.hexMap = hexMap;
        this.townHall = townHall;
        this.resourceStorage = new ResourceStorage();
        this.units = new ArrayList<Unit>();
        this.buildings = new HashMap<HexCoordinate, Building>();
        this.claimedBorderTiles = new HashSet<HexCoordinate>();
        this.settlementTiles = new HashSet<HexCoordinate>();
        this.townHallUpgradeService = new TownHallUpgradeService();
        this.townHallLevelService = new TownHallLevelService();
        this.townHallProductionQueue = new TownHallProductionQueue();
        this.eventBus = new GameEventBus();
        this.adjacencyBonusService = new AdjacencyBonusService();
        this.happinessService = new HappinessService();
        this.wildAnimals = new ArrayList<WildAnimal>();
        this.unitCap = initialUnitCap;
        this.adjacencyBonusService.subscribeTo(this);
        this.currentTurnNumber = 1;
        this.inStarvation = false;
        claimInitialBorderAroundTownHall();
    }

    private void claimInitialBorderAroundTownHall() {
        claimedBorderTiles.add(townHall.getCoordinate());

        List<HexCoordinate> neighbors = townHall.getCoordinate().getNeighbors();
        for (int i = 0; i < neighbors.size(); i++) {
            if (hexMap.containsCoordinate(neighbors.get(i))) {
                claimedBorderTiles.add(neighbors.get(i));
            }
        }
    }

    public HexMap getHexMap() {
        return hexMap;
    }

    public TownHall getTownHall() {
        return townHall;
    }

    public ResourceStorage getResourceStorage() {
        return resourceStorage;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public int getUnitCap() {
        return unitCap;
    }

    public boolean hasReachedUnitCap() {
        return units.size() >= unitCap;
    }

    public boolean addUnit(Unit unit) {
        if (hasReachedUnitCap()) {
            return false;
        }
        units.add(unit);
        return true;
    }

    public void removeUnit(Unit unit) {
        units.remove(unit);
    }

    public void increaseUnitCap(int extraCapacity) {
        unitCap = unitCap + extraCapacity;
    }

    public void addBuilding(Building building) {
        buildings.put(building.getCoordinate(), building);
    }

    public Building getBuildingAt(HexCoordinate coordinate) {
        return buildings.get(coordinate);
    }

    public void removeBuildingAt(HexCoordinate coordinate) {
        buildings.remove(coordinate);
    }

    public List<Building> getAllBuildings() {
        return new ArrayList<Building>(buildings.values());
    }

    public boolean isWithinBorder(HexCoordinate coordinate) {
        return claimedBorderTiles.contains(coordinate);
    }

    public void claimBorderTile(HexCoordinate coordinate) {
        claimedBorderTiles.add(coordinate);
    }

    public Set<HexCoordinate> getClaimedBorderTiles() {
        return claimedBorderTiles;
    }

    public void addSettlementTile(HexCoordinate coordinate) {
        settlementTiles.add(coordinate);
    }

    public boolean isSettlementTile(HexCoordinate coordinate) {
        return settlementTiles.contains(coordinate);
    }

    public Set<HexCoordinate> getSettlementTiles() {
        return settlementTiles;
    }

    public TownHallUpgradeService getTownHallUpgradeService() {
        return townHallUpgradeService;
    }

    public TownHallLevelService getTownHallLevelService() {
        return townHallLevelService;
    }

    public TownHallProductionQueue getTownHallProductionQueue() {
        return townHallProductionQueue;
    }

    public GameEventBus getEventBus() {
        return eventBus;
    }

    public AdjacencyBonusService getAdjacencyBonusService() {
        return adjacencyBonusService;
    }

    public HappinessService getHappinessService() {
        return happinessService;
    }

    public boolean hasActiveStable() {
        List<Building> allBuildings = getAllBuildings();
        for (int i = 0; i < allBuildings.size(); i++) {
            Building building = allBuildings.get(i);
            if (!building.isDestroyed() && building.getBuildingType() == BuildingType.STABLE) {
                return true;
            }
        }
        return false;
    }

    public int countActiveMilitaryUnits() {
        int count = 0;
        for (int i = 0; i < units.size(); i++) {
            if (MilitaryUnitCapProvider.isMilitaryUnitType(units.get(i).getUnitType())) {
                count = count + 1;
            }
        }
        return count;
    }

    public boolean hasReachedMilitaryUnitCap() {
        int cap = MilitaryUnitCapProvider.getCap(townHall.getLevel());
        return countActiveMilitaryUnits() >= cap;
    }

    public void addWildAnimal(WildAnimal wildAnimal) {
        wildAnimals.add(wildAnimal);
    }

    public void removeWildAnimal(WildAnimal wildAnimal) {
        wildAnimals.remove(wildAnimal);
    }

    public List<WildAnimal> getWildAnimals() {
        return wildAnimals;
    }
    
    public int getCurrentTurnNumber() {
        return currentTurnNumber;
    }

    public void incrementTurnNumber() {
        currentTurnNumber = currentTurnNumber + 1;
    }

    public boolean isInStarvation() {
        return inStarvation;
    }

    public void setInStarvation(boolean inStarvation) {
        this.inStarvation = inStarvation;
    }
}