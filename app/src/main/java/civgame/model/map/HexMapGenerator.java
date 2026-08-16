package civgame.model.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import civgame.model.tribe.Tribe;
import civgame.model.tribe.TribeType;
import civgame.util.Constants;
import civgame.util.UnitIdGenerator;

public class HexMapGenerator {

    private static final int FOREST_PATCH_MIN_SIZE = 4;
    private static final int FOREST_PATCH_MAX_SIZE = 7;
    private static final int EXTRA_FOREST_PATCH_COUNT = 3;

    private static final int MOUNTAIN_RANGE_MIN_LENGTH = 5;
    private static final int MOUNTAIN_RANGE_MAX_LENGTH = 9;
    private static final int MOUNTAIN_RANGE_COUNT = 2;
    private static final int MOUNTAIN_MIN_DISTANCE_FROM_TOWN_HALL = 4;

    private static final int IMPASSABLE_RANGE_MIN_LENGTH = 4;
    private static final int IMPASSABLE_RANGE_MAX_LENGTH = 7;
    private static final int IMPASSABLE_RANGE_COUNT = 1;
    private static final int IMPASSABLE_RANGE_MIN_DISTANCE_FROM_TOWN_HALL = 5;

    private static final int SEA_PATCH_MIN_SIZE = 5;
    private static final int SEA_PATCH_MAX_SIZE = 10;
    private static final int SEA_PATCH_COUNT = 1;
    private static final int SEA_MIN_DISTANCE_FROM_TOWN_HALL = 5;
    private static final double FISH_RESOURCE_CHANCE = 0.4;

    private static final int MAX_BARRIER_GENERATION_ATTEMPTS = 5;
    private static final double MIN_REACHABLE_OPEN_TILE_FRACTION = 0.7;

    private static final int RIVER_MIN_LENGTH = 6;
    private static final int RIVER_MAX_LENGTH = 10;
    private static final int RIVER_MIN_DISTANCE_FROM_TOWN_HALL = 3;

    private static final int TRADING_POST_MIN_DISTANCE_FROM_TOWN_HALL = 4;

    private static final int TRIBE_MIN_DISTANCE_FROM_TOWN_HALL = 6;
    private static final int TRIBE_MIN_DISTANCE_BETWEEN_CAMPS = 4;

    private static final double IRON_DEPOSIT_CHANCE = 0.3;
    private static final double GRASSLAND_RESOURCE_CHANCE = 0.5;
    private static final double PLAIN_RESOURCE_CHANCE = 0.4;
    private static final double GRASSLAND_TERRAIN_CHANCE = 0.5;

    private final Random random;

    public HexMapGenerator() {
        this.random = new Random();
    }

    public HexMapGenerator(long seed) {
        this.random = new Random(seed);
    }

    public void generate(HexMap hexMap, HexCoordinate townHallCoordinate) {
        Set<HexCoordinate> forestTiles = generateForestPatches(hexMap, townHallCoordinate);
        Set<HexCoordinate> mountainTiles = generateMountainRanges(hexMap, townHallCoordinate);

        Set<HexCoordinate> impassableRangeTiles = new HashSet<HexCoordinate>();
        Set<HexCoordinate> seaTiles = new HashSet<HexCoordinate>();
        generateConnectedBarrierFeatures(hexMap, townHallCoordinate, impassableRangeTiles, seaTiles);

        applyForestTerrain(hexMap, forestTiles);
        applyMountainTerrain(hexMap, mountainTiles);
        applyVolcanoConversion(hexMap, mountainTiles);
        applyImpassableRangeTerrain(hexMap, impassableRangeTiles);
        applySeaTerrain(hexMap, seaTiles);
        applyRemainingTerrain(hexMap, forestTiles, mountainTiles, impassableRangeTiles, seaTiles);
        generateRiver(hexMap, townHallCoordinate);

        HexTile clearedTownHallTile = new HexTile(townHallCoordinate, TerrainType.PLAIN, TileResourceType.NONE);
        hexMap.setTile(townHallCoordinate, clearedTownHallTile);
    }

    private void generateRiver(HexMap hexMap, HexCoordinate townHallCoordinate) {
        HexCoordinate startCoordinate = pickRandomCoordinateFarFromTownHall(hexMap, townHallCoordinate, RIVER_MIN_DISTANCE_FROM_TOWN_HALL);
        if (startCoordinate == null) {
            return;
        }

        int riverLength = randomBetween(RIVER_MIN_LENGTH, RIVER_MAX_LENGTH);
        HexCoordinate current = startCoordinate;
        Set<HexCoordinate> visited = new HashSet<HexCoordinate>();
        visited.add(current);

        for (int i = 1; i < riverLength; i++) {
            List<HexCoordinate> neighbors = current.getNeighbors();
            Collections.shuffle(neighbors, random);

            HexCoordinate nextCoordinate = null;
            for (int j = 0; j < neighbors.size(); j++) {
                HexCoordinate neighbor = neighbors.get(j);
                if (hexMap.containsCoordinate(neighbor) && !visited.contains(neighbor) && !neighbor.equals(townHallCoordinate)) {
                    nextCoordinate = neighbor;
                    break;
                }
            }

            if (nextCoordinate == null) {
                break;
            }

            hexMap.getOrCreateEdgeFeatures(current, nextCoordinate).setRiverPresent(true);
            visited.add(nextCoordinate);
            current = nextCoordinate;
        }
    }
    
    public HexCoordinate pickTradingPostCoordinate(HexMap hexMap, HexCoordinate townHallCoordinate) {
        List<HexCoordinate> candidates = new ArrayList<HexCoordinate>();

        for (HexCoordinate coordinate : hexMap.getAllTiles().keySet()) {
            if (townHallCoordinate.distanceTo(coordinate) < TRADING_POST_MIN_DISTANCE_FROM_TOWN_HALL) {
                continue;
            }

            HexTile tile = hexMap.getTile(coordinate);
            if (tile.getTerrainType() == TerrainType.SEA || tile.getTerrainType() == TerrainType.MOUNTAIN_RANGE) {
                continue;
            }

            candidates.add(coordinate);
        }

        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get(random.nextInt(candidates.size()));
    }
    public List<Tribe> placeTribes(HexMap hexMap, HexCoordinate townHallCoordinate) {
        List<Tribe> tribes = new ArrayList<Tribe>();
        List<HexCoordinate> usedCoordinates = new ArrayList<HexCoordinate>();

        addTribeIfPossible(tribes, usedCoordinates, hexMap, townHallCoordinate, TribeType.FARMER, "Farmer Tribe");
        addTribeIfPossible(tribes, usedCoordinates, hexMap, townHallCoordinate, TribeType.WARRIOR, "Warrior Tribe");
        addTribeIfPossible(tribes, usedCoordinates, hexMap, townHallCoordinate, TribeType.TRADER, "Trader Tribe");
        addTribeIfPossible(tribes, usedCoordinates, hexMap, townHallCoordinate, TribeType.MOUNTAIN, "Mountain Tribe");
        addTribeIfPossible(tribes, usedCoordinates, hexMap, townHallCoordinate, TribeType.COASTAL, "Coastal Tribe");

        return tribes;
    }

    private void addTribeIfPossible(List<Tribe> tribes, List<HexCoordinate> usedCoordinates, HexMap hexMap, HexCoordinate townHallCoordinate, TribeType tribeType, String tribeName) {
        HexCoordinate campCoordinate = pickTribeCampCoordinate(hexMap, townHallCoordinate, usedCoordinates, tribeType);
        if (campCoordinate == null) {
            return;
        }

        usedCoordinates.add(campCoordinate);
        String id = UnitIdGenerator.generateNextId("TRIBE");
        tribes.add(new Tribe(id, tribeName, tribeType, campCoordinate));
    }

    private HexCoordinate pickTribeCampCoordinate(HexMap hexMap, HexCoordinate townHallCoordinate, List<HexCoordinate> usedCoordinates, TribeType tribeType) {
        List<HexCoordinate> candidates = new ArrayList<HexCoordinate>();
        List<HexCoordinate> preferredCandidates = new ArrayList<HexCoordinate>();

        for (HexCoordinate coordinate : hexMap.getAllTiles().keySet()) {
            if (townHallCoordinate.distanceTo(coordinate) < TRIBE_MIN_DISTANCE_FROM_TOWN_HALL) {
                continue;
            }
            if (isTooCloseToUsedCoordinates(coordinate, usedCoordinates)) {
                continue;
            }

            HexTile tile = hexMap.getTile(coordinate);
            if (tile.getTerrainType() == TerrainType.SEA || tile.getTerrainType() == TerrainType.MOUNTAIN_RANGE) {
                continue;
            }

            candidates.add(coordinate);
            if (matchesTribeTerrainPreference(hexMap, coordinate, tribeType)) {
                preferredCandidates.add(coordinate);
            }
        }

        List<HexCoordinate> pool = preferredCandidates.isEmpty() ? candidates : preferredCandidates;
        if (pool.isEmpty()) {
            return null;
        }

        return pool.get(random.nextInt(pool.size()));
    }

    private boolean isTooCloseToUsedCoordinates(HexCoordinate coordinate, List<HexCoordinate> usedCoordinates) {
        for (int i = 0; i < usedCoordinates.size(); i++) {
            if (coordinate.distanceTo(usedCoordinates.get(i)) < TRIBE_MIN_DISTANCE_BETWEEN_CAMPS) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesTribeTerrainPreference(HexMap hexMap, HexCoordinate coordinate, TribeType tribeType) {
        HexTile tile = hexMap.getTile(coordinate);

        if (tribeType == TribeType.FARMER) {
            return tile.getTerrainType() == TerrainType.PLAIN || tile.getTerrainType() == TerrainType.GRASSLAND;
        }
        if (tribeType == TribeType.MOUNTAIN) {
            return hasNeighborOfTerrain(hexMap, coordinate, TerrainType.MOUNTAIN);
        }
        if (tribeType == TribeType.COASTAL) {
            return hasNeighborOfTerrain(hexMap, coordinate, TerrainType.SEA);
        }
        return true;
    }

    private boolean hasNeighborOfTerrain(HexMap hexMap, HexCoordinate coordinate, TerrainType terrainType) {
        List<HexCoordinate> neighbors = coordinate.getNeighbors();
        for (int i = 0; i < neighbors.size(); i++) {
            if (!hexMap.containsCoordinate(neighbors.get(i))) {
                continue;
            }
            if (hexMap.getTile(neighbors.get(i)).getTerrainType() == terrainType) {
                return true;
            }
        }
        return false;
    }

    private Set<HexCoordinate> generateForestPatches(HexMap hexMap, HexCoordinate townHallCoordinate) {
        Set<HexCoordinate> forestTiles = new HashSet<HexCoordinate>();

        List<HexTile> nearbyTiles = hexMap.getTilesInRange(townHallCoordinate, 2);
        List<HexCoordinate> candidateSeeds = new ArrayList<HexCoordinate>();
        for (int i = 0; i < nearbyTiles.size(); i++) {
            HexCoordinate candidate = nearbyTiles.get(i).getCoordinate();
            if (!candidate.equals(townHallCoordinate)) {
                candidateSeeds.add(candidate);
            }
        }

        if (!candidateSeeds.isEmpty()) {
            HexCoordinate guaranteedSeed = candidateSeeds.get(random.nextInt(candidateSeeds.size()));
            int guaranteedSize = randomBetween(FOREST_PATCH_MIN_SIZE, FOREST_PATCH_MAX_SIZE);
            forestTiles.addAll(growCluster(hexMap, guaranteedSeed, guaranteedSize, townHallCoordinate));
        }

        for (int i = 0; i < EXTRA_FOREST_PATCH_COUNT; i++) {
            HexCoordinate randomSeed = pickRandomCoordinate(hexMap);
            if (randomSeed == null || randomSeed.equals(townHallCoordinate)) {
                continue;
            }
            int patchSize = randomBetween(FOREST_PATCH_MIN_SIZE, FOREST_PATCH_MAX_SIZE);
            forestTiles.addAll(growCluster(hexMap, randomSeed, patchSize, townHallCoordinate));
        }

        return forestTiles;
    }

    private Set<HexCoordinate> generateMountainRanges(HexMap hexMap, HexCoordinate townHallCoordinate) {
        Set<HexCoordinate> mountainTiles = new HashSet<HexCoordinate>();

        for (int i = 0; i < MOUNTAIN_RANGE_COUNT; i++) {
            HexCoordinate startCoordinate = pickRandomCoordinateFarFromTownHall(hexMap, townHallCoordinate);
            if (startCoordinate == null) {
                continue;
            }
            int rangeLength = randomBetween(MOUNTAIN_RANGE_MIN_LENGTH, MOUNTAIN_RANGE_MAX_LENGTH);
            mountainTiles.addAll(growMountainRange(hexMap, startCoordinate, rangeLength));
        }

        return mountainTiles;
    }

    private void generateConnectedBarrierFeatures(HexMap hexMap, HexCoordinate townHallCoordinate, Set<HexCoordinate> impassableRangeTiles, Set<HexCoordinate> seaTiles) {
        for (int attempt = 0; attempt < MAX_BARRIER_GENERATION_ATTEMPTS; attempt++) {
            Set<HexCoordinate> candidateImpassableTiles = generateImpassableRangeTiles(hexMap, townHallCoordinate);
            Set<HexCoordinate> candidateSeaTiles = generateSeaTiles(hexMap, townHallCoordinate);

            Set<HexCoordinate> combinedBarrier = new HashSet<HexCoordinate>();
            combinedBarrier.addAll(candidateImpassableTiles);
            combinedBarrier.addAll(candidateSeaTiles);

            if (isTownHallAreaStillOpen(hexMap, townHallCoordinate, combinedBarrier)) {
                impassableRangeTiles.addAll(candidateImpassableTiles);
                seaTiles.addAll(candidateSeaTiles);
                return;
            }
        }
    }

    private Set<HexCoordinate> generateImpassableRangeTiles(HexMap hexMap, HexCoordinate townHallCoordinate) {
        Set<HexCoordinate> impassableTiles = new HashSet<HexCoordinate>();

        for (int i = 0; i < IMPASSABLE_RANGE_COUNT; i++) {
            HexCoordinate startCoordinate = pickRandomCoordinateFarFromTownHall(hexMap, townHallCoordinate, IMPASSABLE_RANGE_MIN_DISTANCE_FROM_TOWN_HALL);
            if (startCoordinate == null) {
                continue;
            }
            int rangeLength = randomBetween(IMPASSABLE_RANGE_MIN_LENGTH, IMPASSABLE_RANGE_MAX_LENGTH);
            impassableTiles.addAll(growMountainRange(hexMap, startCoordinate, rangeLength));
        }

        return impassableTiles;
    }

    private Set<HexCoordinate> generateSeaTiles(HexMap hexMap, HexCoordinate townHallCoordinate) {
        Set<HexCoordinate> seaTiles = new HashSet<HexCoordinate>();

        for (int i = 0; i < SEA_PATCH_COUNT; i++) {
            HexCoordinate startCoordinate = pickRandomCoordinateFarFromTownHall(hexMap, townHallCoordinate, SEA_MIN_DISTANCE_FROM_TOWN_HALL);
            if (startCoordinate == null) {
                continue;
            }
            int patchSize = randomBetween(SEA_PATCH_MIN_SIZE, SEA_PATCH_MAX_SIZE);
            seaTiles.addAll(growCluster(hexMap, startCoordinate, patchSize, townHallCoordinate));
        }

        return seaTiles;
    }

    private boolean isTownHallAreaStillOpen(HexMap hexMap, HexCoordinate townHallCoordinate, Set<HexCoordinate> barrierTiles) {
        Set<HexCoordinate> reachable = new HashSet<HexCoordinate>();
        Queue<HexCoordinate> frontier = new LinkedList<HexCoordinate>();

        reachable.add(townHallCoordinate);
        frontier.add(townHallCoordinate);

        while (!frontier.isEmpty()) {
            HexCoordinate current = frontier.poll();
            List<HexCoordinate> neighbors = current.getNeighbors();

            for (int i = 0; i < neighbors.size(); i++) {
                HexCoordinate neighbor = neighbors.get(i);
                if (!hexMap.containsCoordinate(neighbor)) {
                    continue;
                }
                if (barrierTiles.contains(neighbor)) {
                    continue;
                }
                if (reachable.contains(neighbor)) {
                    continue;
                }
                reachable.add(neighbor);
                frontier.add(neighbor);
            }
        }

        int totalOpenTiles = hexMap.getAllTiles().size() - barrierTiles.size();
        if (totalOpenTiles <= 0) {
            return false;
        }

        double reachableFraction = (double) reachable.size() / totalOpenTiles;
        return reachableFraction >= MIN_REACHABLE_OPEN_TILE_FRACTION;
    }

    private void applyForestTerrain(HexMap hexMap, Set<HexCoordinate> forestTiles) {
        for (HexCoordinate coordinate : forestTiles) {
            HexTile forestTile = new HexTile(coordinate, TerrainType.FOREST, TileResourceType.WOOD);
            hexMap.setTile(coordinate, forestTile);
        }
    }

    private void applyMountainTerrain(HexMap hexMap, Set<HexCoordinate> mountainTiles) {
        for (HexCoordinate coordinate : mountainTiles) {
            HexTile mountainTile = new HexTile(coordinate, TerrainType.MOUNTAIN, TileResourceType.STONE);
            if (random.nextDouble() < IRON_DEPOSIT_CHANCE) {
                mountainTile.setIronDepositPresent(true);
            }
            hexMap.setTile(coordinate, mountainTile);
        }
    }

    private void applyVolcanoConversion(HexMap hexMap, Set<HexCoordinate> mountainTiles) {
        for (HexCoordinate coordinate : mountainTiles) {
            if (random.nextDouble() < Constants.VOLCANO_SPAWN_CHANCE) {
                HexTile volcanoTile = new HexTile(coordinate, TerrainType.VOLCANO, TileResourceType.NONE);
                hexMap.setTile(coordinate, volcanoTile);
            }
        }
    }

    private void applyImpassableRangeTerrain(HexMap hexMap, Set<HexCoordinate> impassableRangeTiles) {
        for (HexCoordinate coordinate : impassableRangeTiles) {
            HexTile rangeTile = new HexTile(coordinate, TerrainType.MOUNTAIN_RANGE, TileResourceType.NONE);
            hexMap.setTile(coordinate, rangeTile);
        }
    }

    private void applySeaTerrain(HexMap hexMap, Set<HexCoordinate> seaTiles) {
        for (HexCoordinate coordinate : seaTiles) {
            TileResourceType resourceType = TileResourceType.NONE;
            if (random.nextDouble() < FISH_RESOURCE_CHANCE) {
                resourceType = TileResourceType.FISH;
            }
            HexTile seaTile = new HexTile(coordinate, TerrainType.SEA, resourceType);
            hexMap.setTile(coordinate, seaTile);
        }
    }

    private void applyRemainingTerrain(HexMap hexMap, Set<HexCoordinate> forestTiles, Set<HexCoordinate> mountainTiles, Set<HexCoordinate> impassableRangeTiles, Set<HexCoordinate> seaTiles) {
        for (HexCoordinate coordinate : hexMap.getAllTiles().keySet()) {
            if (forestTiles.contains(coordinate) || mountainTiles.contains(coordinate)) {
                continue;
            }
            if (impassableRangeTiles.contains(coordinate) || seaTiles.contains(coordinate)) {
                continue;
            }

            HexTile newTile;
            if (random.nextDouble() < GRASSLAND_TERRAIN_CHANCE) {
                newTile = createGrasslandTile(coordinate);
            } else {
                newTile = createPlainTile(coordinate);
            }

            hexMap.setTile(coordinate, newTile);
        }
    }

    private HexTile createGrasslandTile(HexCoordinate coordinate) {
        if (random.nextDouble() >= GRASSLAND_RESOURCE_CHANCE) {
            return new HexTile(coordinate, TerrainType.GRASSLAND, TileResourceType.NONE);
        }

        if (random.nextBoolean()) {
            return new HexTile(coordinate, TerrainType.GRASSLAND, TileResourceType.WHEAT);
        }
        return new HexTile(coordinate, TerrainType.GRASSLAND, TileResourceType.RICE);
    }

    private HexTile createPlainTile(HexCoordinate coordinate) {
        if (random.nextDouble() >= PLAIN_RESOURCE_CHANCE) {
            return new HexTile(coordinate, TerrainType.PLAIN, TileResourceType.NONE);
        }

        if (random.nextBoolean()) {
            return new HexTile(coordinate, TerrainType.PLAIN, TileResourceType.CATTLE);
        }
        return new HexTile(coordinate, TerrainType.PLAIN, TileResourceType.SHEEP);
    }

    private Set<HexCoordinate> growCluster(HexMap hexMap, HexCoordinate start, int targetSize, HexCoordinate excludedCoordinate) {
        Set<HexCoordinate> cluster = new HashSet<HexCoordinate>();
        Queue<HexCoordinate> frontier = new LinkedList<HexCoordinate>();

        cluster.add(start);
        frontier.add(start);

        while (!frontier.isEmpty() && cluster.size() < targetSize) {
            HexCoordinate current = frontier.poll();
            List<HexCoordinate> neighbors = current.getNeighbors();
            Collections.shuffle(neighbors, random);

            for (int i = 0; i < neighbors.size(); i++) {
                HexCoordinate neighbor = neighbors.get(i);

                if (cluster.size() >= targetSize) {
                    break;
                }
                if (!hexMap.containsCoordinate(neighbor)) {
                    continue;
                }
                if (neighbor.equals(excludedCoordinate)) {
                    continue;
                }
                if (cluster.contains(neighbor)) {
                    continue;
                }

                cluster.add(neighbor);
                frontier.add(neighbor);
            }
        }

        return cluster;
    }

    private Set<HexCoordinate> growMountainRange(HexMap hexMap, HexCoordinate start, int length) {
        Set<HexCoordinate> range = new HashSet<HexCoordinate>();
        HexCoordinate current = start;
        range.add(current);

        for (int i = 1; i < length; i++) {
            List<HexCoordinate> neighbors = current.getNeighbors();
            Collections.shuffle(neighbors, random);

            HexCoordinate nextCoordinate = null;
            for (int j = 0; j < neighbors.size(); j++) {
                HexCoordinate neighbor = neighbors.get(j);
                if (hexMap.containsCoordinate(neighbor) && !range.contains(neighbor)) {
                    nextCoordinate = neighbor;
                    break;
                }
            }

            if (nextCoordinate == null) {
                break;
            }

            range.add(nextCoordinate);
            current = nextCoordinate;
        }

        return range;
    }

    private HexCoordinate pickRandomCoordinate(HexMap hexMap) {
        List<HexCoordinate> allCoordinates = new ArrayList<HexCoordinate>(hexMap.getAllTiles().keySet());
        if (allCoordinates.isEmpty()) {
            return null;
        }
        return allCoordinates.get(random.nextInt(allCoordinates.size()));
    }

    private HexCoordinate pickRandomCoordinateFarFromTownHall(HexMap hexMap, HexCoordinate townHallCoordinate) {
        return pickRandomCoordinateFarFromTownHall(hexMap, townHallCoordinate, MOUNTAIN_MIN_DISTANCE_FROM_TOWN_HALL);
    }

    private HexCoordinate pickRandomCoordinateFarFromTownHall(HexMap hexMap, HexCoordinate townHallCoordinate, int minDistance) {
        List<HexCoordinate> farCoordinates = new ArrayList<HexCoordinate>();

        for (HexCoordinate coordinate : hexMap.getAllTiles().keySet()) {
            if (townHallCoordinate.distanceTo(coordinate) >= minDistance) {
                farCoordinates.add(coordinate);
            }
        }

        if (farCoordinates.isEmpty()) {
            return null;
        }

        return farCoordinates.get(random.nextInt(farCoordinates.size()));
    }

    private int randomBetween(int minInclusive, int maxInclusive) {
        return minInclusive + random.nextInt(maxInclusive - minInclusive + 1);
    }

}