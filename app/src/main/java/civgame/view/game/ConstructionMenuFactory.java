package civgame.view.game;

import civgame.model.building.BuildingCostProvider;
import civgame.model.building.BuildingPlacementValidator;
import civgame.model.building.BuildingType;
import civgame.model.game.ConstructionService;
import civgame.model.game.GameSession;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexTile;
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
            BuildingType buildingType = availableTypes.get(i);
            popupMenu.add(createMenuItem(gameSession, builder, buildingType, onBuildSuccess));
        }

        return popupMenu;
    }

    private JMenuItem createMenuItem(GameSession gameSession, Builder builder, BuildingType buildingType, Runnable onBuildSuccess) {
        int woodCost = BuildingCostProvider.getWoodCost(buildingType);
        int actionPointCost = BuildingCostProvider.getConstructionActionPointCost(buildingType);
        String label = buildingType.name() + " (Wood: " + woodCost + ", AP: " + actionPointCost + ")";

        JMenuItem menuItem = new JMenuItem(label);
        HexCoordinate targetCoordinate = builder.getCoordinate();

        boolean canBuild = constructionService.canBuild(gameSession, builder, buildingType, targetCoordinate);
        menuItem.setEnabled(canBuild);

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
}