package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.building.TownHall;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.resource.ResourceStorage;
import civgame.model.unit.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSession {

    private final HexMap hexMap;
    private final TownHall townHall;
    private final ResourceStorage resourceStorage;
    private final List<Unit> units;
    private final Map<HexCoordinate, Building> buildings;
    private int unitCap;

    public GameSession(HexMap hexMap, TownHall townHall, int initialUnitCap) {
        this.hexMap = hexMap;
        this.townHall = townHall;
        this.resourceStorage = new ResourceStorage();
        this.units = new ArrayList<Unit>();
        this.buildings = new HashMap<HexCoordinate, Building>();
        this.unitCap = initialUnitCap;
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

    // TODO : add a currentTurnNumber field and increment it in the turn engine
}