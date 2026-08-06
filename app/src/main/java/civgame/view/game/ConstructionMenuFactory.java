package civgame.view.game;

import civgame.model.building.BuildingCostProvider;
import civgame.model.building.BuildingPlacementValidator;
import civgame.model.building.BuildingType;
import civgame.model.building.TownHallUpgradeType;
import civgame.model.game.ConstructionService;
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

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ConstructionMenuFactory {

    private final ConstructionService constructionService;
    private final RoadConstructionService roadConstructionService;
    private final WallConstructionService wallConstructionService;

    public ConstructionMenuFactory(ConstructionService constructionService, RoadConstructionService roadConstructionService, WallConstructionService wallConstructionService) {
        this.constructionService = constructionService;
        this.roadConstructionService = roadConstructionService;
        this.wallConstructionService = wallConstructionService;
    }

    public JPopupMenu buildMenu(GameSession gameSession, Builder builder, Runnable onBuildSuccess) {
        JPopupMenu popupMenu = new JPopupMenu();
        PopupMenuStyler.style(popupMenu);

        HexTile tile = gameSession.getHexMap().getTile(builder.getCoordinate());
        List<BuildingType> availableTypes = BuildingPlacementValidator.getAvailableBuildingTypes(tile);

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

        return popupMenu;
    }

    private JMenu createWallMenu(GameSession gameSession, Builder builder, Runnable onBuildSuccess) {
        JMenu wallMenu = new JMenu("Build Wall");
        List<HexCoordinate> neighbors = builder.getCoordinate().getNeighbors();

        for (int i = 0; i < neighbors.size(); i++) {
            HexCoordinate neighborCoordinate = neighbors.get(i);
            wallMenu.add(createWallDirectionItem(gameSession, builder, neighborCoordinate, onBuildSuccess));
        }

        return wallMenu;
    }

    private JMenuItem createWallDirectionItem(GameSession gameSession, Builder builder, HexCoordinate neighborCoordinate, Runnable onBuildSuccess) {
        HexCoordinate fromCoordinate = builder.getCoordinate();
        String title = "Toward (" + neighborCoordinate.getQ() + ", " + neighborCoordinate.getR() + ")";
        String detailText = "Stone: " + Constants.WALL_STONE_COST + "  Wood: " + Constants.WALL_WOOD_COST + "  AP: " + Constants.WALL_CONSTRUCTION_AP_COST;
        String reason = buildWallUnavailabilityReason(gameSession, fromCoordinate, neighborCoordinate, builder);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean success = wallConstructionService.buildWall(gameSession, builder, fromCoordinate, neighborCoordinate);
                if (success && onBuildSuccess != null) {
                    onBuildSuccess.run();
                }
            }
        };

        return StyledMenuItemFactory.createItem(null, title, detailText, reason, reason.isEmpty(), actionListener);
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
        int woodCost = BuildingCostProvider.getWoodCost(buildingType);
        int actionPointCost = BuildingCostProvider.getConstructionActionPointCost(buildingType);
        HexCoordinate targetCoordinate = builder.getCoordinate();

        String detailText = "Wood: " + woodCost + "   AP: " + actionPointCost;
        String reason = buildUnavailabilityReason(gameSession, builder, buildingType, targetCoordinate);

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
                reason.isEmpty(),
                actionListener
        );
    }

    private String buildUnavailabilityReason(GameSession gameSession, Builder builder, BuildingType buildingType, HexCoordinate targetCoordinate) {
        if (builder.isOutOfCharges()) {
            return "No charges left on this Builder";
        }
        if (gameSession.getBuildingAt(targetCoordinate) != null) {
            return "This tile is already occupied";
        }
        if (!gameSession.isWithinBorder(targetCoordinate)) {
            return "Outside your claimed border";
        }
        if (isTechLocked(gameSession, buildingType)) {
            return "Locked - requires technology";
        }

        int actionPointCost = BuildingCostProvider.getConstructionActionPointCost(buildingType);
        if (!builder.hasEnoughActionPoints(actionPointCost)) {
            return "Not enough Action Points";
        }

        int woodCost = BuildingCostProvider.getWoodCost(buildingType);
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.WOOD, woodCost)) {
            return "Not enough Wood";
        }

        return "";
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