package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.building.BuildingType;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.map.TileVisibility;
import civgame.model.unit.Unit;
import civgame.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NaturalDisasterService {

    private final Random random;

    public NaturalDisasterService() {
        this.random = new Random();
    }

    public void rollForDisaster(GameSession gameSession) {
        if (random.nextDouble() >= Constants.NATURAL_DISASTER_CHANCE_PER_TURN) {
            return;
        }

        List<NaturalDisasterType> availableTypes = new ArrayList<NaturalDisasterType>();
        availableTypes.add(NaturalDisasterType.EARTHQUAKE);
        if (gameSession.getCurrentSeason() == SeasonType.AUTUMN && pickFloodCenter(gameSession) != null) {
            availableTypes.add(NaturalDisasterType.FLOOD);
        }

        NaturalDisasterType chosenType = availableTypes.get(random.nextInt(availableTypes.size()));
        if (chosenType == NaturalDisasterType.EARTHQUAKE) {
            triggerEarthquake(gameSession);
        } else {
            triggerFlood(gameSession);
        }
    }

    private void triggerEarthquake(GameSession gameSession) {
        List<HexCoordinate> landCoordinates = collectLandCoordinates(gameSession);
        if (landCoordinates.isEmpty()) {
            return;
        }

        HexCoordinate center = landCoordinates.get(random.nextInt(landCoordinates.size()));
        List<HexCoordinate> affectedArea = collectHexesInRadius(gameSession, center, Constants.EARTHQUAKE_RADIUS);

        for (int i = 0; i < affectedArea.size(); i++) {
            HexCoordinate coordinate = affectedArea.get(i);
            damageUnitsAt(gameSession, coordinate, Constants.EARTHQUAKE_UNIT_DAMAGE, false);

            if (coordinate.equals(gameSession.getTownHall().getCoordinate())) {
                applyTownHallEarthquakeDamage(gameSession);
            }
        }

        announceDisaster(gameSession, NaturalDisasterType.EARTHQUAKE, center);
    }

    private void applyTownHallEarthquakeDamage(GameSession gameSession) {
        int currentHp = gameSession.getTownHall().getCurrentHitPoints();
        int damage = Constants.EARTHQUAKE_TOWN_HALL_DAMAGE;
        if (currentHp - damage < 1) {
            damage = currentHp - 1;
        }
        if (damage > 0) {
            gameSession.getTownHall().applyCombatDamage(damage);
        }
    }

    private void triggerFlood(GameSession gameSession) {
        HexCoordinate center = pickFloodCenter(gameSession);
        if (center == null) {
            return;
        }

        List<HexCoordinate> affectedArea = new ArrayList<HexCoordinate>();
        affectedArea.add(center);

        List<HexCoordinate> neighbors = center.getNeighbors();
        for (int i = 0; i < neighbors.size(); i++) {
            HexCoordinate neighbor = neighbors.get(i);
            if (!gameSession.getHexMap().containsCoordinate(neighbor)) {
                continue;
            }
            TerrainType neighborTerrain = gameSession.getHexMap().getTile(neighbor).getTerrainType();
            if (neighborTerrain != TerrainType.SEA && neighborTerrain != TerrainType.MOUNTAIN && neighborTerrain != TerrainType.MOUNTAIN_RANGE) {
                affectedArea.add(neighbor);
            }
        }

        for (int i = 0; i < affectedArea.size(); i++) {
            HexCoordinate coordinate = affectedArea.get(i);
            damageUnitsAt(gameSession, coordinate, Constants.FLOOD_UNIT_DAMAGE, true);
            damageStructuresAt(gameSession, coordinate);
        }

        announceDisaster(gameSession, NaturalDisasterType.FLOOD, center);
    }

    private HexCoordinate pickFloodCenter(GameSession gameSession) {
        List<HexCoordinate> candidates = new ArrayList<HexCoordinate>();

        for (HexCoordinate coordinate : gameSession.getHexMap().getAllTiles().keySet()) {
            HexTile tile = gameSession.getHexMap().getTile(coordinate);
            if (!isFloodableTerrain(tile.getTerrainType())) {
                continue;
            }
            if (isNearRiverOrSea(gameSession, coordinate)) {
                candidates.add(coordinate);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get(random.nextInt(candidates.size()));
    }

    private boolean isFloodableTerrain(TerrainType terrainType) {
        return terrainType == TerrainType.PLAIN || terrainType == TerrainType.GRASSLAND || terrainType == TerrainType.FOREST;
    }

    private boolean isNearRiverOrSea(GameSession gameSession, HexCoordinate coordinate) {
        List<HexCoordinate> neighbors = coordinate.getNeighbors();
        for (int i = 0; i < neighbors.size(); i++) {
            HexCoordinate neighbor = neighbors.get(i);
            if (!gameSession.getHexMap().containsCoordinate(neighbor)) {
                continue;
            }
            if (gameSession.getHexMap().hasRiver(coordinate, neighbor)) {
                return true;
            }
            if (gameSession.getHexMap().getTile(neighbor).getTerrainType() == TerrainType.SEA) {
                return true;
            }
        }
        return false;
    }

    private void damageUnitsAt(GameSession gameSession, HexCoordinate coordinate, int damage, boolean zeroActionPoints) {
        List<Unit> units = new ArrayList<Unit>(gameSession.getUnits());
        List<Unit> killed = new ArrayList<Unit>();

        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (!unit.getCoordinate().equals(coordinate)) {
                continue;
            }

            unit.applyCombatDamage(damage);
            if (zeroActionPoints) {
                unit.zeroRemainingActionPoints();
            }
            if (unit.isDestroyedByCombat()) {
                killed.add(unit);
            }
        }

        for (int i = 0; i < killed.size(); i++) {
            gameSession.removeUnit(killed.get(i));
        }
    }

    private void damageStructuresAt(GameSession gameSession, HexCoordinate coordinate) {
        HexTile tile = gameSession.getHexMap().getTile(coordinate);
        if (tile == null) {
            return;
        }

        if (tile.isRoadPresent()) {
            tile.setRoadPresent(false);
        }

        Building building = gameSession.getBuildingAt(coordinate);
        if (building == null) {
            return;
        }

        if (building.getBuildingType() == BuildingType.FARM) {
            building.destroy();
            gameSession.removeBuildingAt(coordinate);
            return;
        }

        building.applyCombatDamage(Constants.FLOOD_BUILDING_DAMAGE);
        building.clearAllStationedWorkers();

        if (building.isDestroyedByCombat()) {
            gameSession.removeBuildingAt(coordinate);
        }
    }

    private List<HexCoordinate> collectLandCoordinates(GameSession gameSession) {
        List<HexCoordinate> coordinates = new ArrayList<HexCoordinate>();
        for (HexCoordinate coordinate : gameSession.getHexMap().getAllTiles().keySet()) {
            if (gameSession.getHexMap().getTile(coordinate).getTerrainType() != TerrainType.SEA) {
                coordinates.add(coordinate);
            }
        }
        return coordinates;
    }

    private List<HexCoordinate> collectHexesInRadius(GameSession gameSession, HexCoordinate center, int radius) {
        List<HexCoordinate> result = new ArrayList<HexCoordinate>();
        for (HexCoordinate coordinate : gameSession.getHexMap().getAllTiles().keySet()) {
            if (center.distanceTo(coordinate) <= radius) {
                result.add(coordinate);
            }
        }
        return result;
    }

    private void announceDisaster(GameSession gameSession, NaturalDisasterType type, HexCoordinate center) {
        HexTile centerTile = gameSession.getHexMap().getTile(center);
        boolean visible = centerTile != null && centerTile.getVisibility() == TileVisibility.VISIBLE;

        if (visible) {
            gameSession.addActiveDisasterEffect(new ActiveDisasterEffect(type, center, Constants.DISASTER_EFFECT_DURATION_TICKS));
        }

        String label = type == NaturalDisasterType.EARTHQUAKE ? "Earthquake" : "Flood";
        String visibilityNote = visible ? "" : " (out of sight)";
        gameSession.addTribeNotification(label + " struck near (" + center.getQ() + ", " + center.getR() + ")" + visibilityNote);
    }
}