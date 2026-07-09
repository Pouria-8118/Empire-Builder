package civgame.model.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class HexMapGenerator {

    private static final int FOREST_PATCH_MIN_SIZE = 4;
    private static final int FOREST_PATCH_MAX_SIZE = 7;
    private static final int EXTRA_FOREST_PATCH_COUNT = 3;

    private static final int MOUNTAIN_RANGE_MIN_LENGTH = 5;
    private static final int MOUNTAIN_RANGE_MAX_LENGTH = 9;
    private static final int MOUNTAIN_RANGE_COUNT = 2;
    private static final int MOUNTAIN_MIN_DISTANCE_FROM_TOWN_HALL = 4;

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

        applyForestTerrain(hexMap, forestTiles);
        applyMountainTerrain(hexMap, mountainTiles);
        applyRemainingTerrain(hexMap, forestTiles, mountainTiles);

        HexTile clearedTownHallTile = new HexTile(townHallCoordinate, TerrainType.PLAIN, TileResourceType.NONE);
        hexMap.setTile(townHallCoordinate, clearedTownHallTile);
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

    private void applyRemainingTerrain(HexMap hexMap, Set<HexCoordinate> forestTiles, Set<HexCoordinate> mountainTiles) {
        for (HexCoordinate coordinate : hexMap.getAllTiles().keySet()) {
            if (forestTiles.contains(coordinate) || mountainTiles.contains(coordinate)) {
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
        List<HexCoordinate> farCoordinates = new ArrayList<HexCoordinate>();

        for (HexCoordinate coordinate : hexMap.getAllTiles().keySet()) {
            if (townHallCoordinate.distanceTo(coordinate) >= MOUNTAIN_MIN_DISTANCE_FROM_TOWN_HALL) {
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

    // TODO : if a finite per-tile resource capacity system is added later,
    // factor it into generation here (currently depletion is just a boolean flag on HexTile)
}