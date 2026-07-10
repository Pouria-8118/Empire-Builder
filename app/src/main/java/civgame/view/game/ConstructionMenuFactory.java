package civgame.view.game;

import civgame.model.building.BuildingCostProvider;
import civgame.model.building.BuildingPlacementValidator;
import civgame.model.building.BuildingType;
import civgame.model.building.TownHallUpgradeType;
import civgame.model.game.ConstructionService;
import civgame.model.game.GameSession;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexTile;
import civgame.model.resource.ResourceType;
import civgame.model.unit.Builder;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ConstructionMenuFactory {

    private final ConstructionService constructionService;

    public ConstructionMenuFactory(ConstructionService constructionService) {
        this.constructionService = constructionService;
    }

    public JPopupMenu buildMenu(GameSession gameSession, Builder builder, Runnable onBuildSuccess) {
        JPopupMenu popupMenu = new JPopupMenu();
        HexTile tile = gameSession.getHexMap().getTile(builder.getCoordinate());
        List<BuildingType> availableTypes = BuildingPlacementValidator.getAvailableBuildingTypes(tile);

        if (availableTypes.isEmpty()) {
            JMenuItem emptyItem = new JMenuItem("No valid building for this tile");
            emptyItem.setEnabled(false);
            popupMenu.add(emptyItem);
            return popupMenu;
        }

        for (int i = 0; i < availableTypes.size(); i++) {
            popupMenu.add(createMenuItem(gameSession, builder, availableTypes.get(i), onBuildSuccess));
        }

        return popupMenu;
    }

    private JMenuItem createMenuItem(GameSession gameSession, Builder builder, BuildingType buildingType, Runnable onBuildSuccess) {
        int woodCost = BuildingCostProvider.getWoodCost(buildingType);
        int actionPointCost = BuildingCostProvider.getConstructionActionPointCost(buildingType);
        HexCoordinate targetCoordinate = builder.getCoordinate();

        String label = buildingType.name() + " (Wood: " + woodCost + ", AP: " + actionPointCost + ")";
        String reason = buildUnavailabilityReason(gameSession, builder, buildingType, targetCoordinate);

        JMenuItem menuItem = new JMenuItem(label + reason);
        menuItem.setEnabled(reason.isEmpty());

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean success = constructionService.build(gameSession, builder, buildingType, targetCoordinate);
                if (success && onBuildSuccess != null) {
                    onBuildSuccess.run();
                }
            }
        });

        return menuItem;
    }

    private String buildUnavailabilityReason(GameSession gameSession, Builder builder, BuildingType buildingType, HexCoordinate targetCoordinate) {
        if (builder.isOutOfCharges()) {
            return " [No charges left]";
        }
        if (gameSession.getBuildingAt(targetCoordinate) != null) {
            return " [Tile occupied]";
        }
        if (!gameSession.isWithinBorder(targetCoordinate)) {
            return " [Outside border]";
        }
        if (isTechLocked(gameSession, buildingType)) {
            return " [Locked - needs tech]";
        }

        int actionPointCost = BuildingCostProvider.getConstructionActionPointCost(buildingType);
        if (!builder.hasEnoughActionPoints(actionPointCost)) {
            return " [Not enough AP]";
        }

        int woodCost = BuildingCostProvider.getWoodCost(buildingType);
        if (!gameSession.getResourceStorage().hasEnough(ResourceType.WOOD, woodCost)) {
            return " [Not enough Wood]";
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