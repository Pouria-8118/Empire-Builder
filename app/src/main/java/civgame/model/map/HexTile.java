package civgame.model.map;

public class HexTile {

    private final HexCoordinate coordinate;
    private final TerrainType terrainType;
    private final TileResourceType resourceType;
    private boolean resourceDepleted;
    private boolean ironDepositPresent;
    private TileVisibility visibility;

    public HexTile(HexCoordinate coordinate, TerrainType terrainType, TileResourceType resourceType) {
        this.coordinate = coordinate;
        this.terrainType = terrainType;
        this.resourceType = resourceType;
        this.resourceDepleted = false;
        this.ironDepositPresent = false;
        this.visibility = TileVisibility.UNSEEN;
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