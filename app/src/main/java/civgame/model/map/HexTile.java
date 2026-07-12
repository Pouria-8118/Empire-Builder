package civgame.model.map;

import civgame.util.Constants;

public class HexTile {

    private final HexCoordinate coordinate;
    private final TerrainType terrainType;
    private final TileResourceType resourceType;
    private boolean resourceDepleted;
    private boolean ironDepositPresent;
    private TileVisibility visibility;
    private int remainingResourceCapacity;

    public HexTile(HexCoordinate coordinate, TerrainType terrainType, TileResourceType resourceType) {
        this.coordinate = coordinate;
        this.terrainType = terrainType;
        this.resourceType = resourceType;
        this.resourceDepleted = false;
        this.ironDepositPresent = false;
        this.visibility = TileVisibility.UNSEEN;
        this.remainingResourceCapacity = computeInitialCapacity(resourceType);
    }

    private int computeInitialCapacity(TileResourceType resourceType) {
        if (resourceType == TileResourceType.NONE) {
            return 0;
        }
        return Constants.RESOURCE_TILE_CAPACITY;
    }

    public HexCoordinate getCoordinate() {
        return coordinate;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public TileResourceType getResourceType() {
        return resourceType;
    }

    public boolean hasResource() {
        return resourceType != TileResourceType.NONE && !resourceDepleted;
    }

    public boolean isResourceDepleted() {
        return resourceDepleted;
    }

    public void depleteResource() {
        this.resourceDepleted = true;
    }

    public int getRemainingResourceCapacity() {
        return remainingResourceCapacity;
    }
    
    public void consumeResourceCapacity(int amount) {
        if (resourceDepleted) {
            return;
        }

        remainingResourceCapacity = remainingResourceCapacity - amount;

        if (remainingResourceCapacity <= 0) {
            remainingResourceCapacity = 0;
            depleteResource();
        }
    }

    public boolean isIronDepositPresent() {
        return ironDepositPresent;
    }

    public void setIronDepositPresent(boolean ironDepositPresent) {
        this.ironDepositPresent = ironDepositPresent;
    }

    public TileVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(TileVisibility visibility) {
        this.visibility = visibility;
    }
}