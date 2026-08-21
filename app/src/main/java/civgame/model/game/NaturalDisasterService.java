package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.building.BuildingType;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.map.TileVisibility;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;
import civgame.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NaturalDisasterService {

    private final Random random;
    private final BearBehaviorService bearBehaviorService;
    private final DisasterPathGenerator pathGenerator;

    public NaturalDisasterService(BearBehaviorService bearBehaviorService) {
        this.random = new Random();
        this.bearBehaviorService = bearBehaviorService;
        this.pathGenerator = new DisasterPathGenerator(random);
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
        if (bearBehaviorService.isEligible(gameSession)) {
            availableTypes.add(NaturalDisasterType.BEAR_ATTACK);
        }
        if (pickCoastalCoordinate(gameSession) != null) {
            availableTypes.add(NaturalDisasterType.TSUNAMI);
            availableTypes.add(NaturalDisasterType.SEA_STORM);
        }
        if (pickVolcanoCoordinate(gameSession) != null) {
            availableTypes.add(NaturalDisasterType.VOLCANIC_ERUPTION);
        }
        if (pickPlainOrGrasslandCoordinate(gameSession) != null) {
            availableTypes.add(NaturalDisasterType.TORNADO);
        }
        if (pickMountainStartCoordinate(gameSession) != null) {
            availableTypes.add(NaturalDisasterType.AVALANCHE);
        }

        NaturalDisasterType chosenType = availableTypes.get(random.nextInt(availableTypes.size()));
        if (chosenType == NaturalDisasterType.EARTHQUAKE) {
            triggerEarthquake(gameSession);
        } else if (chosenType == NaturalDisasterType.FLOOD) {
            triggerFlood(gameSession);
        } else if (chosenType == NaturalDisasterType.BEAR_ATTACK) {
            bearBehaviorService.triggerBearAttack(gameSession);
        } else if (chosenType == NaturalDisasterType.TSUNAMI) {
            triggerTsunami(gameSession);
        } else if (chosenType == NaturalDisasterType.SEA_STORM) {
            triggerSeaStorm(gameSession);
        } else if (chosenType == NaturalDisasterType.VOLCANIC_ERUPTION) {
            triggerVolcanicEruption(gameSession);
        } else if (chosenType == NaturalDisasterType.TORNADO) {
            triggerTornado(gameSession);
        } else {
            triggerAvalanche(gameSession);
        }
    }

    private void triggerTsunami(GameSession gameSession) {
        HexCoordinate center = pickCoastalCoordinate(gameSession);
        if (center == null) {
            return;
        }

        List<HexCoordinate> affected = new ArrayList<HexCoordinate>();
        affected.add(center);

        List<HexCoordinate> neighbors = center.getNeighbors();
        for (int i = 0; i < neighbors.size() && affected.size() < 1 + Constants.TSUNAMI_RADIUS_EXTRA_HEXES; i++) {
            HexCoordinate neighbor = neighbors.get(i);
            if (gameSession.getHexMap().containsCoordinate(neighbor) && isCoastal(gameSession, neighbor)) {
                affected.add(neighbor);
            }
        }

        for (int i = 0; i < affected.size(); i++) {
            HexCoordinate coordinate = affected.get(i);
            damageUnitsAt(gameSession, coordinate, Constants.TSUNAMI_UNIT_DAMAGE, false);

            Building building = gameSession.getBuildingAt(coordinate);
            if (building != null) {
                building.destroy();
                gameSession.removeBuildingAt(coordinate);
            }
        }

        announceDisaster(gameSession, NaturalDisasterType.TSUNAMI, center);
    }

    private void triggerSeaStorm(GameSession gameSession) {
        HexCoordinate center = pickCoastalCoordinate(gameSession);
        if (center == null) {
            return;
        }
        HexCoordinate seaCenter = findAdjacentSea(gameSession, center);
        if (seaCenter == null) {
            return;
        }

        List<HexCoordinate> affected = collectHexesInRadius(gameSession, seaCenter, Constants.SEA_STORM_RADIUS);
        for (int i = 0; i < affected.size(); i++) {
            HexCoordinate coordinate = affected.get(i);
            TerrainType terrain = gameSession.getHexMap().getTile(coordinate).getTerrainType();

            if (terrain == TerrainType.SEA) {
                damageShipsAt(gameSession, coordinate);
                continue;
            }

            Building building = gameSession.getBuildingAt(coordinate);
            if (building != null && building.getBuildingType() == BuildingType.DOCK) {
                building.applyCombatDamage(Constants.SEA_STORM_DOCK_DAMAGE);
                if (building.isDestroyedByCombat()) {
                    gameSession.removeBuildingAt(coordinate);
                }
            }

            if (isCoastal(gameSession, coordinate)) {
                damageUnitsAt(gameSession, coordinate, Constants.SEA_STORM_COASTAL_UNIT_DAMAGE, false);
            }
        }

        announceDisaster(gameSession, NaturalDisasterType.SEA_STORM, seaCenter);
    }

    private void damageShipsAt(GameSession gameSession, HexCoordinate coordinate) {
        List<Unit> units = new ArrayList<Unit>(gameSession.getUnits());
        List<Unit> killed = new ArrayList<Unit>();

        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            boolean isShipOrFloating = unit.getUnitType() == UnitType.GALLEY || unit.getUnitType() == UnitType.FISHING_BOAT || unit.isFloating();
            if (!unit.getCoordinate().equals(coordinate) || !isShipOrFloating) {
                continue;
            }

            unit.applyCombatDamage(Constants.SEA_STORM_SHIP_DAMAGE);
            unit.zeroRemainingActionPoints();
            if (unit.isDestroyedByCombat()) {
                killed.add(unit);
            }
        }

        for (int i = 0; i < killed.size(); i++) {
            gameSession.removeUnit(killed.get(i));
        }
    }

    private void triggerVolcanicEruption(GameSession gameSession) {
        HexCoordinate volcanoCoordinate = pickVolcanoCoordinate(gameSession);
        if (volcanoCoordinate == null) {
            return;
        }

        damageUnitsAt(gameSession, volcanoCoordinate, Integer.MAX_VALUE, false);

        List<HexCoordinate> lavaPath = pathGenerator.growPath(gameSession, volcanoCoordinate, Constants.LAVA_PATH_LENGTH, true, true);
        int blockUntilTurn = gameSession.getCurrentTurnNumber() + Constants.LAVA_PATH_BLOCK_TURNS;

        for (int i = 1; i < lavaPath.size(); i++) {
            HexCoordinate coordinate = lavaPath.get(i);
            damageUnitsAt(gameSession, coordinate, Constants.VOLCANO_UNIT_DAMAGE, false);
            destroyStructuresAt(gameSession, coordinate);
            gameSession.blockHexTemporarily(coordinate, blockUntilTurn);
        }

        List<HexCoordinate> neighbors = collectHexesInRadius(gameSession, volcanoCoordinate, 1);
        for (int i = 0; i < neighbors.size(); i++) {
            HexCoordinate coordinate = neighbors.get(i);
            if (coordinate.equals(volcanoCoordinate) || lavaPath.contains(coordinate)) {
                continue;
            }
            damageUnitsAt(gameSession, coordinate, Constants.VOLCANO_NEIGHBOR_UNIT_DAMAGE, false);
        }

        announceDisaster(gameSession, NaturalDisasterType.VOLCANIC_ERUPTION, volcanoCoordinate);
    }

    private void triggerTornado(GameSession gameSession) {
        HexCoordinate start = pickPlainOrGrasslandCoordinate(gameSession);
        if (start == null) {
            return;
        }

        List<HexCoordinate> path = pathGenerator.growPath(gameSession, start, Constants.TORNADO_PATH_LENGTH, true, true);

        for (int i = 0; i < path.size(); i++) {
            HexCoordinate coordinate = path.get(i);
            throwUnitsAt(gameSession, coordinate);
            damageBuildingAndRoadAt(gameSession, coordinate, Constants.TORNADO_BUILDING_DAMAGE, true);
        }

        announceDisaster(gameSession, NaturalDisasterType.TORNADO, start);
    }

    private void triggerAvalanche(GameSession gameSession) {
        HexCoordinate start = pickMountainStartCoordinate(gameSession);
        if (start == null) {
            return;
        }

        List<HexCoordinate> path = pathGenerator.growPath(gameSession, start, Constants.AVALANCHE_PATH_LENGTH, true, false);
        int blockUntilTurn = gameSession.getCurrentTurnNumber() + Constants.AVALANCHE_BLOCK_TURNS + 1;

        for (int i = 0; i < path.size(); i++) {
            HexCoordinate coordinate = path.get(i);
            damageUnitsAt(gameSession, coordinate, Constants.AVALANCHE_UNIT_DAMAGE, false);
            damageBuildingAndRoadAt(gameSession, coordinate, Constants.AVALANCHE_STRUCTURE_DAMAGE, false);
            gameSession.blockHexTemporarily(coordinate, blockUntilTurn);
        }

        announceDisaster(gameSession, NaturalDisasterType.AVALANCHE, start);
    }

    private void throwUnitsAt(GameSession gameSession, HexCoordinate coordinate) {
        List<Unit> units = new ArrayList<Unit>(gameSession.getUnits());
        List<Unit> killed = new ArrayList<Unit>();

        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (!unit.getCoordinate().equals(coordinate)) {
                continue;
            }

            unit.applyCombatDamage(Constants.TORNADO_UNIT_DAMAGE);
            if (unit.isDestroyedByCombat()) {
                killed.add(unit);
                continue;
            }

            HexCoordinate throwTarget = findPassableNeighbor(gameSession, coordinate);
            if (throwTarget != null) {
                unit.setCoordinate(throwTarget);
            }
        }

        for (int i = 0; i < killed.size(); i++) {
            gameSession.removeUnit(killed.get(i));
        }
    }

    private HexCoordinate findPassableNeighbor(GameSession gameSession, HexCoordinate coordinate) {
        List<HexCoordinate> neighbors = coordinate.getNeighbors();
        for (int i = 0; i < neighbors.size(); i++) {
            HexCoordinate neighbor = neighbors.get(i);
            if (!gameSession.getHexMap().containsCoordinate(neighbor)) {
                continue;
            }
            TerrainType terrain = gameSession.getHexMap().getTile(neighbor).getTerrainType();
            if (terrain != TerrainType.SEA && terrain != TerrainType.MOUNTAIN_RANGE) {
                return neighbor;
            }
        }
        return null;
    }

    private void damageBuildingAndRoadAt(GameSession gameSession, HexCoordinate coordinate, int damage, boolean destroyRoadOnly) {
        HexTile tile = gameSession.getHexMap().getTile(coordinate);
        if (tile != null && tile.isRoadPresent()) {
            tile.setRoadPresent(false);
        }

        Building building = gameSession.getBuildingAt(coordinate);
        if (building == null) {
            return;
        }

        building.applyCombatDamage(damage);
        building.clearAllStationedWorkers();
        if (building.isDestroyedByCombat()) {
            gameSession.removeBuildingAt(coordinate);
        }
    }

    private void destroyStructuresAt(GameSession gameSession, HexCoordinate coordinate) {
        HexTile tile = gameSession.getHexMap().getTile(coordinate);
        if (tile != null && tile.isRoadPresent()) {
            tile.setRoadPresent(false);
        }

        Building building = gameSession.getBuildingAt(coordinate);
        if (building != null) {
            building.destroy();
            gameSession.removeBuildingAt(coordinate);
        }
    }

    private HexCoordinate pickCoastalCoordinate(GameSession gameSession) {
        List<HexCoordinate> candidates = new ArrayList<HexCoordinate>();
        for (HexCoordinate coordinate : gameSession.getHexMap().getAllTiles().keySet()) {
            TerrainType terrain = gameSession.getHexMap().getTile(coordinate).getTerrainType();
            if (terrain != TerrainType.SEA && isCoastal(gameSession, coordinate)) {
                candidates.add(coordinate);
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    private HexCoordinate findAdjacentSea(GameSession gameSession, HexCoordinate coastalCoordinate) {
        List<HexCoordinate> neighbors = coastalCoordinate.getNeighbors();
        for (int i = 0; i < neighbors.size(); i++) {
            HexCoordinate neighbor = neighbors.get(i);
            if (gameSession.getHexMap().containsCoordinate(neighbor) && gameSession.getHexMap().getTile(neighbor).getTerrainType() == TerrainType.SEA) {
                return neighbor;
            }
        }
        return null;
    }

    private boolean isCoastal(GameSession gameSession, HexCoordinate coordinate) {
        List<HexCoordinate> neighbors = coordinate.getNeighbors();
        for (int i = 0; i < neighbors.size(); i++) {
            HexCoordinate neighbor = neighbors.get(i);
            if (gameSession.getHexMap().containsCoordinate(neighbor) && gameSession.getHexMap().getTile(neighbor).getTerrainType() == TerrainType.SEA) {
                return true;
            }
        }
        return false;
    }

    private HexCoordinate pickVolcanoCoordinate(GameSession gameSession) {
        List<HexCoordinate> candidates = new ArrayList<HexCoordinate>();
        for (HexCoordinate coordinate : gameSession.getHexMap().getAllTiles().keySet()) {
            if (gameSession.getHexMap().getTile(coordinate).getTerrainType() == TerrainType.VOLCANO) {
                candidates.add(coordinate);
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    private HexCoordinate pickPlainOrGrasslandCoordinate(GameSession gameSession) {
        List<HexCoordinate> candidates = new ArrayList<HexCoordinate>();
        for (HexCoordinate coordinate : gameSession.getHexMap().getAllTiles().keySet()) {
            TerrainType terrain = gameSession.getHexMap().getTile(coordinate).getTerrainType();
            if (terrain == TerrainType.PLAIN || terrain == TerrainType.GRASSLAND) {
                candidates.add(coordinate);
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    private HexCoordinate pickMountainStartCoordinate(GameSession gameSession) {
        List<HexCoordinate> candidates = new ArrayList<HexCoordinate>();
        for (HexCoordinate coordinate : gameSession.getHexMap().getAllTiles().keySet()) {
            TerrainType terrain = gameSession.getHexMap().getTile(coordinate).getTerrainType();
            if (terrain == TerrainType.MOUNTAIN || terrain == TerrainType.MOUNTAIN_RANGE) {
                candidates.add(coordinate);
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(random.nextInt(candidates.size()));
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
            if (neighborTerrain != TerrainType.SEA && neighborTerrain != TerrainType.MOUNTAIN && neighborTerrain != TerrainType.MOUNTAIN_RANGE && neighborTerrain != TerrainType.VOLCANO) {
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
        building.setProductionStoppedUntilTurn(gameSession.getCurrentTurnNumber() + 1);

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

        String label = getDisasterLabel(type);
        String visibilityNote = visible ? "" : " (out of sight)";
        gameSession.addTribeNotification(label + " struck near (" + center.getQ() + ", " + center.getR() + ")" + visibilityNote);
    }

    private String getDisasterLabel(NaturalDisasterType type) {
        if (type == NaturalDisasterType.EARTHQUAKE) {
            return "Earthquake";
        }
        if (type == NaturalDisasterType.FLOOD) {
            return "Flood";
        }
        if (type == NaturalDisasterType.TSUNAMI) {
            return "Tsunami";
        }
        if (type == NaturalDisasterType.VOLCANIC_ERUPTION) {
            return "Volcanic Eruption";
        }
        if (type == NaturalDisasterType.TORNADO) {
            return "Tornado";
        }
        if (type == NaturalDisasterType.AVALANCHE) {
            return "Avalanche";
        }
        if (type == NaturalDisasterType.SEA_STORM) {
            return "Sea Storm";
        }
        if (type == NaturalDisasterType.BEAR_ATTACK) {
            return "Bear Attack";
        }
        return "Disaster";
    }
}