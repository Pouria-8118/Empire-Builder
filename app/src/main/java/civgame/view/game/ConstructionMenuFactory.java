package civgame.view.game;

import civgame.model.building.Building;
import civgame.model.building.BuildingCostProvider;
import civgame.model.building.BuildingPlacementValidator;
import civgame.model.building.BuildingType;
import civgame.model.building.TownHallLevel;
import civgame.model.building.TownHallUpgradeType;
import civgame.model.game.ConstructionService;
import civgame.model.game.DemolitionService;
import civgame.model.game.GameSession;
import civgame.model.game.RoadConstructionService;
import civgame.model.game.WallConstructionService;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.map.TileVisibility;
import civgame.model.resource.ResourceType;
import civgame.model.unit.Builder;
import civgame.util.Constants;
import civgame.util.MenuIconResolver;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ConstructionMenuFactory {

    private final ConstructionService constructionService;
    private final RoadConstructionService roadConstructionService;
    private final WallConstructionService wallConstructionService;
    private final DemolitionService demolitionService;

    public ConstructionMenuFactory(ConstructionService constructionService, RoadConstructionService roadConstructionService, WallConstructionService wallConstructionService, DemolitionService demolitionService) {
        this.constructionService = constructionService;
        this.roadConstructionService = roadConstructionService;
        this.wallConstructionService = wallConstructionService;
        this.demolitionService = demolitionService;
    }

    public JPopupMenu buildMenu(GameSession gameSession, Builder builder, Runnable onBuildSuccess) {
        JPopupMenu popupMenu = new JPopupMenu();
        PopupMenuStyler.style(popupMenu);

        HexTile tile = gameSession.getHexMap().getTile(builder.getCoordinate());
        List<BuildingType> availableTypes = BuildingPlacementValidator.getAvailableBuildingTypes(tile, gameSession.getHexMap());

        if (availableTypes.isEmpty()) {
            JMenuItem emptyItem = StyledMenuItemFactory.createItem(null, "No valid building here", "This tile has no matching resource", "", false, null);
            popupMenu.add(emptyItem);
        } else {
            for (int i = 0; i < availableTypes.size(); i++) {
                popupMenu.add(createMenuItem(gameSession, builder, availableTypes.get(i), onBuildSuccess));
            }
        }

        popupMenu.add(new JSeparator());
        popupMenu.add(createRoadMenuItem(gameSession, builder, onBuildSuccess));
        popupMenu.add(createWallMenu(gameSession, builder, onBuildSuccess));
        popupMenu.add(createDemolishMenu(gameSession, builder, onBuildSuccess));

        return popupMenu;
    }

    private JMenu createDemolishMenu(GameSession gameSession, Builder builder, Runnable onActionSuccess) {
        JMenu demolishMenu = new JMenu("Demolish");
        MenuComponentStyler.style(demolishMenu);
        boolean anyItemAdded = false;

        List<HexCoordinate> selfAndNeighbors = new ArrayList<HexCoordinate>();
        selfAndNeighbors.add(builder.getCoordinate());
        selfAndNeighbors.addAll(builder.getCoordinate().getNeighbors());

        for (int i = 0; i < selfAndNeighbors.size(); i++) {
            HexCoordinate coordinate = selfAndNeighbors.get(i);
            if (!gameSession.getHexMap().containsCoordinate(coordinate)) {
                continue;
            }

            Building building = gameSession.getBuildingAt(coordinate);
            if (building != null) {
                demolishMenu.add(createDemolishBuildingItem(gameSession, builder, coordinate, building, onActionSuccess));
                anyItemAdded = true;
            }

            HexTile tile = gameSession.getHexMap().getTile(coordinate);
            if (tile.isRoadPresent()) {
                demolishMenu.add(createDemolishRoadItem(gameSession, builder, coordinate, onActionSuccess));
                anyItemAdded = true;
            }
        }

        List<HexCoordinate> neighbors = builder.getCoordinate().getNeighbors();
        for (int i = 0; i < neighbors.size(); i++) {
            HexCoordinate neighborCoordinate = neighbors.get(i);
            if (gameSession.getHexMap().hasActiveWall(builder.getCoordinate(), neighborCoordinate)) {
                demolishMenu.add(createDemolishWallItem(gameSession, builder, neighborCoordinate, onActionSuccess));
                anyItemAdded = true;
            }
        }

        if (!anyItemAdded) {
            demolishMenu.add(StyledMenuItemFactory.createItem(null, "Nothing to demolish", "No building, road or wall nearby", "", false, null));
        }

        return demolishMenu;
    }

    private JMenuItem createDemolishBuildingItem(GameSession gameSession, Builder builder, HexCoordinate targetCoordinate, Building building, Runnable onActionSuccess) {
        String title = "Building: " + building.getBuildingType().name() + " at (" + targetCoordinate.getQ() + ", " + targetCoordinate.getR() + ")";
        boolean canDemolish = demolitionService.canDemolishBuilding(gameSession, builder, targetCoordinate);
        String reason = canDemolish ? "" : "Not enough Action Points";

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int confirmation = JOptionPane.showConfirmDialog(null, "Demolish this building permanently? Spent resources will not be refunded.", "Confirm Demolition", JOptionPane.YES_NO_OPTION);
                if (confirmation != JOptionPane.YES_OPTION) {
                    return;
                }

                boolean success = demolitionService.demolishBuilding(gameSession, builder, targetCoordinate);
                if (success && onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        };

        return StyledMenuItemFactory.createItem(new DemolishIcon(), title, "AP: " + Constants.DEMOLITION_AP_COST, reason, canDemolish, actionListener);
    }

    private JMenuItem createDemolishRoadItem(GameSession gameSession, Builder builder, HexCoordinate targetCoordinate, Runnable onActionSuccess) {
        String title = "Road at (" + targetCoordinate.getQ() + ", " + targetCoordinate.getR() + ")";
        boolean canDemolish = demolitionService.canDemolishRoad(gameSession, builder, targetCoordinate);
        String reason = canDemolish ? "" : "Not enough Action Points";

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int confirmation = JOptionPane.showConfirmDialog(null, "Demolish this road permanently?", "Confirm Demolition", JOptionPane.YES_NO_OPTION);
                if (confirmation != JOptionPane.YES_OPTION) {
                    return;
                }

                boolean success = demolitionService.demolishRoad(gameSession, builder, targetCoordinate);
                if (success && onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        };

        return StyledMenuItemFactory.createItem(new DemolishIcon(), title, "AP: " + Constants.DEMOLITION_AP_COST, reason, canDemolish, actionListener);
    }

    private JMenuItem createDemolishWallItem(GameSession gameSession, Builder builder, HexCoordinate neighborCoordinate, Runnable onActionSuccess) {
        HexCoordinate fromCoordinate = builder.getCoordinate();
        String title = "Wall toward (" + neighborCoordinate.getQ() + ", " + neighborCoordinate.getR() + ")";
        boolean canDemolish = demolitionService.canDemolishWall(gameSession, builder, fromCoordinate, neighborCoordinate);
        String reason = canDemolish ? "" : "Not enough Action Points";

        int deltaQ = neighborCoordinate.getQ() - fromCoordinate.getQ();
        int deltaR = neighborCoordinate.getR() - fromCoordinate.getR();
        Icon directionIcon = new HexEdgeDirectionIcon(deltaQ, deltaR);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int confirmation = JOptionPane.showConfirmDialog(null, "Demolish this wall permanently?", "Confirm Demolition", JOptionPane.YES_NO_OPTION);
                if (confirmation != JOptionPane.YES_OPTION) {
                    return;
                }

                boolean success = demolitionService.demolishWall(gameSession, builder, fromCoordinate, neighborCoordinate);
                if (success && onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        };

                return StyledMenuItemFactory.createItem(directionIcon, title, "AP: " + Constants.DEMOLITION_AP_COST, reason, canDemolish, actionListener);
    }

    private JMenu createWallMenu(GameSession gameSession, Builder builder, Runnable onActionSuccess) {
        JMenu wallMenu = new JMenu("Build Wall");
        MenuComponentStyler.style(wallMenu);

        List<HexCoordinate> neighbors = builder.getCoordinate().getNeighbors();
        for (int i = 0; i < neighbors.size(); i++) {
            wallMenu.add(createWallDirectionItem(gameSession, builder, neighbors.get(i), onActionSuccess));
        }

        return wallMenu;
    }

    private JMenuItem createWallDirectionItem(GameSession gameSession, Builder builder, HexCoordinate neighborCoordinate, Runnable onActionSuccess) {
        HexCoordinate fromCoordinate = builder.getCoordinate();
        boolean canBuild = wallConstructionService.canBuildWall(gameSession, builder, fromCoordinate, neighborCoordinate);
        String reason = canBuild ? "" : buildWallUnavailabilityReason(gameSession, fromCoordinate, neighborCoordinate, builder);

        String title = "Wall toward (" + neighborCoordinate.getQ() + ", " + neighborCoordinate.getR() + ")";
        String detailText = "Stone: " + Constants.WALL_STONE_COST + "   Wood: " + Constants.WALL_WOOD_COST;

        int deltaQ = neighborCoordinate.getQ() - fromCoordinate.getQ();
        int deltaR = neighborCoordinate.getR() - fromCoordinate.getR();
        Icon directionIcon = new HexEdgeDirectionIcon(deltaQ, deltaR);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean success = wallConstructionService.buildWall(gameSession, builder, fromCoordinate, neighborCoordinate);
                if (success && onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        };

        return StyledMenuItemFactory.createItem(directionIcon, title, detailText, reason, canBuild, actionListener);
    }

    private String buildWallUnavailabilityReason(GameSession gameSession, HexCoordinate fromCoordinate, HexCoordinate neighborCoordinate, Builder builder) {
        HexMap hexMap = gameSession.getHexMap();

        if (!hexMap.containsCoordinate(neighborCoordinate)) {
            return "Outside the map";
        }

        HexTile fromTile = hexMap.getTile(fromCoordinate);
        HexTile toTile = hexMap.getTile(neighborCoordinate);

        if (toTile.getVisibility() == TileVisibility.UNSEEN) {
            return "Not explored yet";
        }
        if (isSeaOrMountainRange(fromTile) || isSeaOrMountainRange(toTile)) {
            return "Cannot build on a sea or mountain range border";
        }
        if (!gameSession.isWithinBorder(fromCoordinate) && !gameSession.isWithinBorder(neighborCoordinate)) {
            return "Outside your claimed border";
        }
        if (hexMap.hasActiveWall(fromCoordinate, neighborCoordinate)) {
            return "Wall already built here";
        }
        if (!builder.hasEnoughActionPoints(Constants.WALL_CONSTRUCTION_AP_COST)) {
            return "Not enough Action Points";
        }
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.STONE, Constants.WALL_STONE_COST)) {
            return "Not enough Stone";
        }
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.WOOD, Constants.WALL_WOOD_COST)) {
            return "Not enough Wood";
        }

        return "";
    }

    private boolean isSeaOrMountainRange(HexTile tile) {
        return tile.getTerrainType() == TerrainType.SEA || tile.getTerrainType() == TerrainType.MOUNTAIN_RANGE;
    }

    private JMenuItem createRoadMenuItem(GameSession gameSession, Builder builder, Runnable onBuildSuccess) {
        HexCoordinate targetCoordinate = builder.getCoordinate();
        String detailText = "Wood: " + Constants.ROAD_CONSTRUCTION_WOOD_COST + "   AP: " + Constants.ROAD_CONSTRUCTION_AP_COST;
        String reason = buildRoadUnavailabilityReason(gameSession, builder, targetCoordinate);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean success = roadConstructionService.buildRoad(gameSession, builder, targetCoordinate);
                if (success && onBuildSuccess != null) {
                    onBuildSuccess.run();
                }
            }
        };

        return StyledMenuItemFactory.createItem(null, "Build Road", detailText, reason, reason.isEmpty(), actionListener);
    }

    private String buildRoadUnavailabilityReason(GameSession gameSession, Builder builder, HexCoordinate targetCoordinate) {
        HexTile targetTile = gameSession.getHexMap().getTile(targetCoordinate);

        if (targetTile.isRoadPresent()) {
            return "Road already built here";
        }
        if (targetTile.getTerrainType() == TerrainType.SEA || targetTile.getTerrainType() == TerrainType.MOUNTAIN_RANGE) {
            return "Cannot build a road here";
        }
        if (!gameSession.isWithinBorder(targetCoordinate)) {
            return "Outside your claimed border";
        }
        if (!builder.hasEnoughActionPoints(Constants.ROAD_CONSTRUCTION_AP_COST)) {
            return "Not enough Action Points";
        }
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.WOOD, Constants.ROAD_CONSTRUCTION_WOOD_COST)) {
            return "Not enough Wood";
        }

        return "";
    }

    private JMenuItem createMenuItem(GameSession gameSession, Builder builder, BuildingType buildingType, Runnable onBuildSuccess) {
        HexCoordinate targetCoordinate = builder.getCoordinate();
        int woodCost = BuildingCostProvider.getWoodCost(buildingType);
        int stoneCost = BuildingCostProvider.getStoneCost(buildingType);
        int actionPointCost = BuildingCostProvider.getConstructionActionPointCost(buildingType);

        String detailText = "Wood: " + woodCost;
        if (stoneCost > 0) {
            detailText = detailText + "   Stone: " + stoneCost;
        }
        detailText = detailText + "   AP: " + actionPointCost;

        boolean canBuild = constructionService.canBuild(gameSession, builder, buildingType, targetCoordinate);
        String reason = canBuild ? "" : buildUnavailabilityReason(gameSession, builder, buildingType, targetCoordinate, woodCost, stoneCost, actionPointCost);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean success = constructionService.build(gameSession, builder, buildingType, targetCoordinate);
                if (success && onBuildSuccess != null) {
                    onBuildSuccess.run();
                }
            }
        };

        return StyledMenuItemFactory.createItem(
                MenuIconResolver.resolveForBuilding(buildingType),
                "Build " + buildingType.name(),
                detailText,
                reason,
                canBuild,
                actionListener
        );
    }

    private String buildUnavailabilityReason(GameSession gameSession, Builder builder, BuildingType buildingType, HexCoordinate targetCoordinate, int woodCost, int stoneCost, int actionPointCost) {
        if (builder.isOutOfCharges()) {
            return "No charges left on this Builder";
        }
        if (gameSession.getBuildingAt(targetCoordinate) != null) {
            return "This tile is already occupied";
        }
        if (!gameSession.isWithinBorder(targetCoordinate)) {
            return "Outside your claimed border";
        }
        if ((buildingType == BuildingType.BAZAAR || buildingType == BuildingType.DOCK)
                && !gameSession.getTownHallLevelService().isAtLeastLevel(gameSession, TownHallLevel.SETTLEMENT)) {
            return "Requires Town Hall level 2";
        }
        if (isTechLocked(gameSession, buildingType)) {
            return "Locked - requires technology";
        }
        if (!builder.hasEnoughActionPoints(actionPointCost)) {
            return "Not enough Action Points";
        }
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.WOOD, woodCost)) {
            return "Not enough Wood";
        }
        if (stoneCost > 0 && !gameSession.getResourceStorage().hasEnough(ResourceType.STONE, stoneCost)) {
            return "Not enough Stone";
        }

        return "Unavailable here";
    }

    private boolean isTechLocked(GameSession gameSession, BuildingType buildingType) {
        if (buildingType == BuildingType.STONE_MINE) {
            return !gameSession.getTownHallUpgradeService().isUnlocked(TownHallUpgradeType.STONE_MINE_TECH);
        }
        if (buildingType == BuildingType.IRON_MINE) {
            return !gameSession.getTownHallUpgradeService().isUnlocked(TownHallUpgradeType.IRON_MINE_TECH);
        }
        return false;
    }
}