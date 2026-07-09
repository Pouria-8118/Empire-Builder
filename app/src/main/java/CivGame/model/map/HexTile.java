package civgame.model.map;

public class HexTile {

    private final HexCoordinate coordinate;
    private final TerrainType terrainType;
    private final TileResourceType resourceType;
    private boolean resourceDepleted;

    // TODO : add a field for fog of war visibility state (UNSEEN / SEEN / VISIBLE)

    public HexTile(HexCoordinate coordinate, TerrainType terrainType, TileResourceType resourceType) {
        this.coordinate = coordinate;
        this.terrainType = terrainType;
        this.resourceType = resourceType;
        this.resourceDepleted = false;
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
}