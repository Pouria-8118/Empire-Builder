package civgame.view.game;

import civgame.model.building.Building;
import civgame.model.game.GameSession;
import civgame.model.game.StationingService;
import civgame.model.map.HexTile;
import civgame.model.unit.Worker;
import civgame.util.Constants;
import civgame.util.MenuIconResolver;

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
        PopupMenuStyler.style(popupMenu);

        Building buildingHere = gameSession.getBuildingAt(worker.getCoordinate());

        if (buildingHere == null) {
            JMenuItem emptyItem = StyledMenuItemFactory.createItem(null, "No building here", "This worker must stand on a building tile", "", false, null);
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
        boolean canStation = stationingService.canStation(gameSession, worker, building);
        String reason = buildUnavailabilityReason(gameSession, worker, building, canStation);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean success = stationingService.station(gameSession, worker, building);
                if (success && onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        };

        String detailText = "AP cost: " + Constants.WORKER_STATION_AP_COST + "   |   " + building.getStationedWorkers().size() + "/" + building.getWorkerCapacity() + " slots used";

        return StyledMenuItemFactory.createItem(
                MenuIconResolver.resolveForUnit(worker.getUnitType()),
                "Station Worker",
                detailText,
                reason,
                canStation,
                actionListener
        );
    }

    private String buildUnavailabilityReason(GameSession gameSession, Worker worker, Building building, boolean canStation) {
        if (canStation) {
            return "";
        }

        if (building.isFull()) {
            return "Building is full";
        }
        if (!worker.hasEnoughActionPoints(Constants.WORKER_STATION_AP_COST)) {
            return "Not enough Action Points";
        }
        if (stationingService.isWorkerStationedAnywhere(gameSession, worker)) {
            return "Already working elsewhere";
        }

        HexTile tile = gameSession.getHexMap().getTile(building.getCoordinate());
        if (tile == null || !tile.hasResource()) {
            return "Resource depleted";
        }

        return "Cannot station here";
    }

    private JMenuItem createUnstationItem(Worker worker, Building building, Runnable onActionSuccess) {
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                stationingService.unstation(worker, building);
                if (onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        };

        return StyledMenuItemFactory.createItem(
                MenuIconResolver.resolveForUnit(worker.getUnitType()),
                "Unstation Worker",
                "Free of cost",
                "",
                true,
                actionListener
        );
    }
}