package civgame.controller.game;

import civgame.model.building.Building;
import civgame.model.game.BorderExpansionService;
import civgame.model.game.ConstructionService;
import civgame.model.game.FogOfWarCalculator;
import civgame.model.game.GameSession;
import civgame.model.game.HexPathFinder;
import civgame.model.game.StationingService;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexTile;
import civgame.model.unit.BorderExpander;
import civgame.model.unit.Builder;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;
import civgame.model.unit.Worker;
import civgame.util.HexGeometry;
import civgame.util.MenuIconResolver;
import civgame.util.TerrainMovementCostProvider;
import civgame.view.game.BorderExpansionMenuFactory;
import civgame.view.game.CameraState;
import civgame.view.game.ConstructionMenuFactory;
import civgame.view.game.GameFrame;
import civgame.view.game.MapPanel;
import civgame.view.game.PopupMenuStyler;
import civgame.view.game.SelectionState;
import civgame.view.game.StationingMenuFactory;
import civgame.view.game.StyledMenuItemFactory;
import civgame.view.game.TownHallMenuFactory;
import civgame.view.game.UnitMovementAnimator;
import civgame.view.hud.SelectedUnitPanel;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

public class GameController {

    private final MapPanel mapPanel;
    private final CameraState cameraState;
    private final SelectionState selectionState;
    private final UnitMovementAnimator movementAnimator;
    private final GameSession gameSession;
    private final HexPathFinder pathFinder;
    private final ConstructionMenuFactory constructionMenuFactory;
    private final StationingMenuFactory stationingMenuFactory;
    private final TownHallMenuFactory townHallMenuFactory;
    private final BorderExpansionMenuFactory borderExpansionMenuFactory;
    private final SelectedUnitPanel selectedUnitPanel;
    private final FogOfWarCalculator fogOfWarCalculator;
    private final StationingService stationingService;
    private int lastDragX;
    private int lastDragY;
    private boolean wasDragged;

    public GameController(GameFrame gameFrame, GameSession gameSession, FogOfWarCalculator fogOfWarCalculator) {
        this.mapPanel = gameFrame.getMapPanel();
        this.cameraState = gameFrame.getCameraState();
        this.selectionState = gameFrame.getSelectionState();
        this.movementAnimator = gameFrame.getMovementAnimator();
        this.gameSession = gameSession;
        this.pathFinder = new HexPathFinder();
        this.constructionMenuFactory = new ConstructionMenuFactory(new ConstructionService());
        this.stationingService = new StationingService();
        this.stationingMenuFactory = new StationingMenuFactory(stationingService);
        this.townHallMenuFactory = new TownHallMenuFactory();
        this.borderExpansionMenuFactory = new BorderExpansionMenuFactory(new BorderExpansionService());
        this.selectedUnitPanel = gameFrame.getSelectedUnitPanel();
        this.fogOfWarCalculator = fogOfWarCalculator;
        registerListeners();
        registerEscapeKeyBinding();
        refreshSelectedUnitDisplay();
    }

    private void registerListeners() {
        MapMouseListener mapMouseListener = new MapMouseListener();
        mapPanel.addMouseListener(mapMouseListener);
        mapPanel.addMouseMotionListener(mapMouseListener);
        mapPanel.addMouseWheelListener(new ZoomListener());
    }

    private void handleLeftClick(int screenX, int screenY) {
        HexCoordinate clickedCoordinate = resolveClickedCoordinate(screenX, screenY);

        if (!gameSession.getHexMap().containsCoordinate(clickedCoordinate)) {
            return;
        }

        if (shouldPrioritizeMovingWorkerOntoBuilding(clickedCoordinate)) {
            attemptMoveSelectedUnit(clickedCoordinate);
            return;
        }

        List<Unit> unitsHere = findUnitsAtCoordinate(clickedCoordinate);

        if (!unitsHere.isEmpty()) {
            selectUnitFromStackOrToggle(unitsHere);
            return;
        }

        attemptMoveSelectedUnit(clickedCoordinate);
    }

    private boolean shouldPrioritizeMovingWorkerOntoBuilding(HexCoordinate clickedCoordinate) {
        if (!selectionState.hasSelection()) {
            return false;
        }

        Unit selectedUnit = selectionState.getSelectedUnit();
        if (selectedUnit.getUnitType() != UnitType.WORKER) {
            return false;
        }
        if (selectedUnit.getCoordinate().equals(clickedCoordinate)) {
            return false;
        }

        return gameSession.getBuildingAt(clickedCoordinate) != null;
    }

    private void selectUnitFromStackOrToggle(List<Unit> unitsHere) {
        Unit currentlySelected = selectionState.getSelectedUnit();
        int currentIndex = unitsHere.indexOf(currentlySelected);

        if (currentIndex == -1) {
            selectionState.setSelectedUnit(unitsHere.get(0));
            updateReachableTilesForSelection();
        } else if (unitsHere.size() == 1) {
            selectionState.clearSelection();
        } else {
            int nextIndex = (currentIndex + 1) % unitsHere.size();
            selectionState.setSelectedUnit(unitsHere.get(nextIndex));
            updateReachableTilesForSelection();
        }

        refreshSelectedUnitDisplay();
        mapPanel.repaint();
    }

    private void updateReachableTilesForSelection() {
        Unit selectedUnit = selectionState.getSelectedUnit();
        boolean allowUnseenTiles = selectedUnit.getUnitType() == UnitType.EXPLORER;

        java.util.Set<HexCoordinate> reachableTiles = pathFinder.findReachableTiles(
                gameSession.getHexMap(),
                selectedUnit.getCoordinate(),
                selectedUnit.getCurrentActionPoints(),
                allowUnseenTiles
        );
        selectionState.setReachableTiles(reachableTiles);
    }

    private void handleRightClick(int screenX, int screenY, MouseEvent mouseEvent) {
        HexCoordinate clickedCoordinate = resolveClickedCoordinate(screenX, screenY);

        if (!gameSession.getHexMap().containsCoordinate(clickedCoordinate)) {
            return;
        }

        if (clickedCoordinate.equals(gameSession.getTownHall().getCoordinate())) {
            showTownHallMenu(mouseEvent);
            return;
        }

        Unit clickedUnit = findUnitAtCoordinate(clickedCoordinate);
        if (clickedUnit == null) {
            return;
        }

        if (clickedUnit.getUnitType() == UnitType.BUILDER) {
            showConstructionMenu((Builder) clickedUnit, mouseEvent);
        } else if (clickedUnit.getUnitType() == UnitType.WORKER) {
            showStationingMenu((Worker) clickedUnit, mouseEvent);
        } else if (clickedUnit.getUnitType() == UnitType.BORDER_EXPANDER) {
            showBorderExpansionMenu((BorderExpander) clickedUnit, mouseEvent);
        } else {
            showUnitInfoMenu(clickedUnit, mouseEvent);
        }
    }

    private void showTownHallMenu(MouseEvent mouseEvent) {
        Runnable onSuccess = createRepaintRunnable();
        JPopupMenu popupMenu = townHallMenuFactory.buildMenu(gameSession, onSuccess);
        popupMenu.show(mapPanel, mouseEvent.getX(), mouseEvent.getY());
    }

    private void showBorderExpansionMenu(BorderExpander expander, MouseEvent mouseEvent) {
        Runnable onSuccess = createRepaintRunnable();
        JPopupMenu popupMenu = borderExpansionMenuFactory.buildMenu(gameSession, expander, onSuccess);
        popupMenu.show(mapPanel, mouseEvent.getX(), mouseEvent.getY());
    }

    private void showUnitInfoMenu(Unit unit, MouseEvent mouseEvent) {
        JPopupMenu popupMenu = new JPopupMenu();
        PopupMenuStyler.style(popupMenu);

        String apText = unit.getCurrentActionPoints() + "/" + unit.getMaxActionPoints();
        JMenuItem infoItem = StyledMenuItemFactory.createItem(
                MenuIconResolver.resolveForUnit(unit.getUnitType()),
                unit.getUnitType().name(),
                "Action Points: " + apText,
                "",
                false,
                null
        );
        popupMenu.add(infoItem);
        popupMenu.show(mapPanel, mouseEvent.getX(), mouseEvent.getY());
    }

    private void showConstructionMenu(Builder builder, MouseEvent mouseEvent) {
        Runnable onSuccess = createRepaintRunnable();
        JPopupMenu popupMenu = constructionMenuFactory.buildMenu(gameSession, builder, onSuccess);
        popupMenu.show(mapPanel, mouseEvent.getX(), mouseEvent.getY());
    }

    private void showStationingMenu(Worker worker, MouseEvent mouseEvent) {
        Runnable onSuccess = createRepaintRunnable();
        JPopupMenu popupMenu = stationingMenuFactory.buildMenu(gameSession, worker, onSuccess);
        popupMenu.show(mapPanel, mouseEvent.getX(), mouseEvent.getY());
    }

    private Runnable createRepaintRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                refreshSelectedUnitDisplay();
                if (selectionState.hasSelection()) {
                    updateReachableTilesForSelection();
                }
                mapPanel.repaint();
            }
        };
    }

    private HexCoordinate resolveClickedCoordinate(int screenX, int screenY) {
        double hexSize = mapPanel.getCurrentHexSize();
        double centerX = mapPanel.getPanelCenterX();
        double centerY = mapPanel.getPanelCenterY();

        double relativeX = screenX - centerX;
        double relativeY = screenY - centerY;

        return HexGeometry.pixelToAxial(relativeX, relativeY, hexSize);
    }

    private void attemptMoveSelectedUnit(HexCoordinate destination) {
        if (!selectionState.hasSelection()) {
            return;
        }
        if (movementAnimator.isAnimating()) {
            return;
        }
        if (destination.equals(gameSession.getTownHall().getCoordinate())) {
            return;
        }
        if (!selectionState.getReachableTiles().contains(destination)) {
            return;
        }

        Unit selectedUnit = selectionState.getSelectedUnit();
        boolean allowUnseenTiles = selectedUnit.getUnitType() == UnitType.EXPLORER;

        List<HexCoordinate> fullPath = pathFinder.findPath(gameSession.getHexMap(), selectedUnit.getCoordinate(), destination, allowUnseenTiles);

        if (fullPath == null || fullPath.isEmpty()) {
            return;
        }

        List<HexCoordinate> freePath = removeOccupiedTilesFromPathEnd(fullPath);

        if (freePath.isEmpty()) {
            return;
        }

        int movementCost = recalculatePathCost(freePath);

        if (!selectedUnit.hasEnoughActionPoints(movementCost)) {
            return;
        }

        unstationIfWorkerIsLeavingItsBuilding(selectedUnit);

        selectedUnit.consumeActionPoints(movementCost);
        refreshSelectedUnitDisplay();
        movementAnimator.startMovement(selectedUnit, freePath, createMovementFinishedCallback());
    }


    private void unstationIfWorkerIsLeavingItsBuilding(Unit unit) {
        if (unit.getUnitType() != UnitType.WORKER) {
            return;
        }

        Worker worker = (Worker) unit;
        Building currentBuilding = stationingService.findBuildingContainingWorker(gameSession, worker);
        if (currentBuilding != null) {
            stationingService.unstation(worker, currentBuilding);
        }
    }

    private List<HexCoordinate> removeOccupiedTilesFromPathEnd(List<HexCoordinate> path) {
        List<HexCoordinate> freePath = new ArrayList<HexCoordinate>();

        for (int i = 0; i < path.size(); i++) {
            HexCoordinate coordinate = path.get(i);
            boolean tileOccupied = findUnitAtCoordinate(coordinate) != null;
            boolean hasBuilding = gameSession.getBuildingAt(coordinate) != null;

            if (tileOccupied && !hasBuilding) {
                break;
            }

            freePath.add(coordinate);
        }

        return freePath;
    }

    private int recalculatePathCost(List<HexCoordinate> path) {
        int totalCost = 0;

        for (int i = 0; i < path.size(); i++) {
            HexTile tile = gameSession.getHexMap().getTile(path.get(i));
            totalCost = totalCost + TerrainMovementCostProvider.getMovementCost(tile.getTerrainType());
        }

        return totalCost;
    }

    private Runnable createMovementFinishedCallback() {
        return new Runnable() {
            @Override
            public void run() {
                fogOfWarCalculator.updateVisibility(gameSession);
                if (selectionState.hasSelection()) {
                    updateReachableTilesForSelection();
                }
                mapPanel.repaint();
            }
        };
    }

    private Unit findUnitAtCoordinate(HexCoordinate coordinate) {
        List<Unit> unitsHere = findUnitsAtCoordinate(coordinate);
        if (unitsHere.isEmpty()) {
            return null;
        }
        return unitsHere.get(0);
    }

    private List<Unit> findUnitsAtCoordinate(HexCoordinate coordinate) {
        List<Unit> result = new ArrayList<Unit>();
        List<Unit> units = gameSession.getUnits();

        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getCoordinate().equals(coordinate)) {
                result.add(units.get(i));
            }
        }

        return result;
    }

    private class MapMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            lastDragX = mouseEvent.getX();
            lastDragY = mouseEvent.getY();
            wasDragged = false;
        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent) {
            int deltaX = mouseEvent.getX() - lastDragX;
            int deltaY = mouseEvent.getY() - lastDragY;

            cameraState.pan(deltaX, deltaY);

            lastDragX = mouseEvent.getX();
            lastDragY = mouseEvent.getY();
            wasDragged = true;

            mapPanel.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            if (wasDragged) {
                return;
            }

            if (mouseEvent.isPopupTrigger() || mouseEvent.getButton() == MouseEvent.BUTTON3) {
                handleRightClick(mouseEvent.getX(), mouseEvent.getY(), mouseEvent);
            } else {
                handleLeftClick(mouseEvent.getX(), mouseEvent.getY());
            }
        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
            mapPanel.updateTooltipForPosition(mouseEvent.getX(), mouseEvent.getY());
        }
    }

    private class ZoomListener implements MouseWheelListener {

        @Override
        public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
            if (mouseWheelEvent.getWheelRotation() < 0) {
                cameraState.zoomIn();
            } else {
                cameraState.zoomOut();
            }

            mapPanel.repaint();
        }
    }

    private void refreshSelectedUnitDisplay() {
        Unit selectedUnit = selectionState.getSelectedUnit();

        if (selectedUnit == null) {
            selectedUnitPanel.showNothingSelected();
            return;
        }

        if (!gameSession.getUnits().contains(selectedUnit)) {
            selectionState.clearSelection();
            selectedUnitPanel.showNothingSelected();
            return;
        }

        selectedUnitPanel.showUnit(selectedUnit);
    }

    private void registerEscapeKeyBinding() {
        InputMap inputMap = mapPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = mapPanel.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clearSelection");
        actionMap.put("clearSelection", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                selectionState.clearSelection();
                refreshSelectedUnitDisplay();
                mapPanel.repaint();
            }
        });
    }
}