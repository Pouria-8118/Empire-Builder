package civgame.view.game;

import civgame.model.building.Building;
import civgame.model.game.GameSession;
import civgame.model.game.StationingService;
import civgame.model.unit.Worker;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StationingMenuFactory {

    private final StationingService stationingService;

    public StationingMenuFactory(StationingService stationingService) {
        this.stationingService = stationingService;
    }

    public JPopupMenu buildMenu(GameSession gameSession, Worker worker, Runnable onActionSuccess) {
        JPopupMenu popupMenu = new JPopupMenu();
        Building buildingHere = gameSession.getBuildingAt(worker.getCoordinate());

        if (buildingHere == null) {
            JMenuItem emptyItem = new JMenuItem("No building on this tile");
            emptyItem.setEnabled(false);
            popupMenu.add(emptyItem);
            return popupMenu;
        }

        boolean alreadyStationed = buildingHere.isWorkerStationed(worker);

        if (alreadyStationed) {
            popupMenu.add(createUnstationItem(worker, buildingHere, onActionSuccess));
        } else {
            popupMenu.add(createStationItem(gameSession, worker, buildingHere, onActionSuccess));
        }

        return popupMenu;
    }

    private JMenuItem createStationItem(GameSession gameSession, Worker worker, Building building, Runnable onActionSuccess) {
        JMenuItem menuItem = new JMenuItem("Station worker (AP cost: 1)");
        boolean canStation = stationingService.canStation(worker, building);
        menuItem.setEnabled(canStation);

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean success = stationingService.station(worker, building);
                if (success && onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        });

        return menuItem;
    }

    private JMenuItem createUnstationItem(Worker worker, Building building, Runnable onActionSuccess) {
        JMenuItem menuItem = new JMenuItem("Unstation worker");

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                stationingService.unstation(worker, building);
                if (onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        });

        return menuItem;
    }
}