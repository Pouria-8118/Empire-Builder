package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.building.BuildingType;
import civgame.model.unit.MilitaryUnitCapProvider;
import civgame.model.building.TownHall;
import civgame.model.event.GameEventBus;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TradingPost;
import civgame.model.pirate.Pirate;
import civgame.model.resource.ResourceStorage;
import civgame.model.tribe.Tribe;
import civgame.model.unit.Unit;
import civgame.util.Constants;
import civgame.model.animal.WildAnimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameSession implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

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
    private transient GameEventBus eventBus;
    private final AdjacencyBonusService adjacencyBonusService;
    private final HappinessService happinessService;
    private final List<WildAnimal> wildAnimals;
    private final List<TradingPost> tradingPosts;
    private final List<Tribe> tribes;
    private final List<String> tribeNotifications;
    private final SeasonService seasonService;
    private final List<ActiveDisasterEffect> activeDisasterEffects;
    private final Map<HexCoordinate, Integer> temporarilyBlockedHexes;
    private final List<Pirate> pirates;
    private boolean bazaarTradedThisTurn;
    private boolean tradingPostTradedThisTurn;
    private boolean pendingDockDiscount;
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
        this.tradingPosts = new ArrayList<TradingPost>();
        this.tribes = new ArrayList<Tribe>();
        this.tribeNotifications = new ArrayList<String>();
        this.seasonService = new SeasonService();
        this.activeDisasterEffects = new ArrayList<ActiveDisasterEffect>();
        this.temporarilyBlockedHexes = new HashMap<HexCoordinate, Integer>();
        this.pirates = new ArrayList<Pirate>();
        this.bazaarTradedThisTurn = false;
        this.pendingDockDiscount = false;
        this.tradingPostTradedThisTurn = false;
        this.unitCap = initialUnitCap;
        this.adjacencyBonusService.subscribeTo(this);
        this.currentTurnNumber = 1;
        this.inStarvation = false;
        claimInitialBorderAroundTownHall();
        claimSecondRingAroundTownHall();
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

    private void claimSecondRingAroundTownHall() {
        List<HexTile> tilesInRange = hexMap.getTilesInRange(townHall.getCoordinate(), Constants.INITIAL_BORDER_RADIUS);
        for (int i = 0; i < tilesInRange.size(); i++) {
            claimedBorderTiles.add(tilesInRange.get(i).getCoordinate());
        }
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

    public void reconnectAfterLoad() {
        this.eventBus = new GameEventBus();
        this.adjacencyBonusService.subscribeTo(this);
        this.adjacencyBonusService.recalculateAll();
        this.tribeNotifications.clear();
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

    public WildAnimal getWildAnimalAt(HexCoordinate coordinate) {
        for (int i = 0; i < wildAnimals.size(); i++) {
            if (wildAnimals.get(i).getCoordinate().equals(coordinate)) {
                return wildAnimals.get(i);
            }
        }
        return null;
    }

    public boolean hasActiveBazaar() {
        List<Building> allBuildings = getAllBuildings();
        for (int i = 0; i < allBuildings.size(); i++) {
            Building building = allBuildings.get(i);
            if (!building.isDestroyed() && building.getBuildingType() == BuildingType.BAZAAR) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBazaarTradedThisTurn() {
        return bazaarTradedThisTurn;
    }

    public void markBazaarTradedThisTurn() {
        this.bazaarTradedThisTurn = true;
    }

    public void resetBazaarTradeFlag() {
        this.bazaarTradedThisTurn = false;
    }

    public void addTradingPost(TradingPost tradingPost) {
        tradingPosts.add(tradingPost);
    }

    public TradingPost getTradingPostAt(HexCoordinate coordinate) {
        for (int i = 0; i < tradingPosts.size(); i++) {
            if (tradingPosts.get(i).getCoordinate().equals(coordinate)) {
                return tradingPosts.get(i);
            }
        }
        return null;
    }

    public List<TradingPost> getTradingPosts() {
        return tradingPosts;
    }

    public boolean hasTradingPostTradedThisTurn() {
        return tradingPostTradedThisTurn;
    }

    public void markTradingPostTradedThisTurn() {
        this.tradingPostTradedThisTurn = true;
    }

    public void resetTradingPostTradeFlag() {
        this.tradingPostTradedThisTurn = false;
    }

    public void addTribe(Tribe tribe) {
        tribes.add(tribe);
    }

    public List<Tribe> getTribes() {
        return tribes;
    }

    public SeasonType getCurrentSeason() {
        return seasonService.getCurrentSeason(getCurrentTurnNumber());
    }

    public void addActiveDisasterEffect(ActiveDisasterEffect effect) {
        activeDisasterEffects.add(effect);
    }

    public List<ActiveDisasterEffect> getActiveDisasterEffects() {
        return activeDisasterEffects;
    }

    public void addPirate(Pirate pirate) {
        pirates.add(pirate);
    }

    public void removePirate(Pirate pirate) {
        pirates.remove(pirate);
    }

    public List<Pirate> getPirates() {
        return pirates;
    }

    public void blockHexTemporarily(HexCoordinate coordinate, int untilTurn) {
        temporarilyBlockedHexes.put(coordinate, untilTurn);
    }

    public boolean isTemporarilyBlocked(HexCoordinate coordinate) {
        Integer untilTurn = temporarilyBlockedHexes.get(coordinate);
        return untilTurn != null && getCurrentTurnNumber() < untilTurn;
    }

    public void tickActiveDisasterEffects() {
        List<ActiveDisasterEffect> finished = new ArrayList<ActiveDisasterEffect>();
        for (int i = 0; i < activeDisasterEffects.size(); i++) {
            ActiveDisasterEffect effect = activeDisasterEffects.get(i);
            effect.decrementTicks();
            if (effect.isFinished()) {
                finished.add(effect);
            }
        }
        activeDisasterEffects.removeAll(finished);
    }

    public void addTribeNotification(String message) {
        tribeNotifications.add(message);
    }

    public void clearTribeNotifications() {
        tribeNotifications.clear();
    }

    public List<String> getTribeNotifications() {
        return tribeNotifications;
    }

    public boolean hasAlliedCoastalTribe() {
        for (int i = 0; i < tribes.size(); i++) {
            Tribe tribe = tribes.get(i);
            if (tribe.isAllied() && tribe.getTribeType() == civgame.model.tribe.TribeType.COASTAL) {
                return true;
            }
        }
        return false;
    }

    public Tribe getTribeAt(HexCoordinate coordinate) {
        for (int i = 0; i < tribes.size(); i++) {
            if (tribes.get(i).getCampCoordinate().equals(coordinate)) {
                return tribes.get(i);
            }
        }
        return null;
    }

    public boolean hasPendingDockDiscount() {
        return pendingDockDiscount;
    }

    public void setPendingDockDiscount(boolean pendingDockDiscount) {
        this.pendingDockDiscount = pendingDockDiscount;
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