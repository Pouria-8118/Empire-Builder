package civgame.model.building;

import civgame.model.map.HexCoordinate;
import civgame.util.BuildingIdGenerator;

public class BuildingFactory {

    public Building createBuilding(BuildingType buildingType, HexCoordinate coordinate) {
        if (buildingType == BuildingType.LUMBER_MILL) {
            String id = BuildingIdGenerator.generateNextId("LUMBER_MILL");
            return new LumberMill(id, coordinate);
        }
        if (buildingType == BuildingType.STONE_MINE) {
            String id = BuildingIdGenerator.generateNextId("STONE_MINE");
            return new StoneMine(id, coordinate);
        }
        if (buildingType == BuildingType.IRON_MINE) {
            String id = BuildingIdGenerator.generateNextId("IRON_MINE");
            return new IronMine(id, coordinate);
        }
        if (buildingType == BuildingType.FARM) {
            String id = BuildingIdGenerator.generateNextId("FARM");
            return new Farm(id, coordinate);
        }
        if (buildingType == BuildingType.DOCK) {
            String id = BuildingIdGenerator.generateNextId("DOCK");
            return new Dock(id, coordinate);
        }
        if (buildingType == BuildingType.MONUMENT) {
            String id = BuildingIdGenerator.generateNextId("MONUMENT");
            return new Monument(id, coordinate);
        }
        if (buildingType == BuildingType.BAZAAR) {
            String id = BuildingIdGenerator.generateNextId("BAZAAR");
            return new Bazaar(id, coordinate);
        }
        if (buildingType == BuildingType.TOWN_HALL) {
            String id = BuildingIdGenerator.generateNextId("TOWN_HALL");
            return new TownHallBuilding(id, coordinate);
        }

        String id = BuildingIdGenerator.generateNextId("STABLE");
        return new Stable(id, coordinate);
    }
}