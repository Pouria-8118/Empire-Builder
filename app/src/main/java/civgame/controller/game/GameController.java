package civgame.controller.game;

import civgame.model.building.Building;
import civgame.model.building.Dock;
import civgame.model.building.TownHallUpgradeType;
import civgame.model.game.BorderExpansionService;
import civgame.model.game.ConstructionService;
import civgame.model.game.RoadConstructionService;
import civgame.model.game.SeasonType;
import civgame.model.game.WallConstructionService;
import civgame.model.game.FogOfWarCalculator;
import civgame.model.game.GameSaveManager;
import civgame.model.game.GameSession;
import civgame.model.game.HexPathFinder;
import civgame.model.game.StationingService;
import civgame.model.game.DemolitionService;
import civgame.model.game.FishingService;
import civgame.model.game.CombatTargetService;
import civgame.model.game.MilitaryStackingRules;
import civgame.model.game.NavalMovementService;
import civgame.model.game.AttackExecutionService;
import civgame.model.game.AttackOutcome;
import civgame.model.game.TradingPostTradeService;
import civgame.model.game.TribeDiplomacyService;
import civgame.model.game.TribeGiftService;
import civgame.model.game.TribeTradeService;
import civgame.model.game.TribeMissionService;
import civgame.model.item.Item;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexTile;
import civgame.model.map.TerrainType;
import civgame.model.map.TradingPost;
import civgame.model.tribe.Tribe;
import civgame.model.unit.BorderExpander;
import civgame.model.unit.Builder;
import civgame.model.unit.Explorer;
import civgame.model.unit.FishingBoat;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;
import civgame.model.unit.Worker;
import civgame.model.unit.MilitaryUnitCapProvider;
import civgame.util.HexGeometry;
import civgame.util.TerrainMovementCostProvider;
import civgame.util.EdgeMovementCostProvider;
import civgame.util.Constants;
import java.util.HashSet;
import civgame.view.game.BorderExpansionMenuFactory;
import civgame.view.game.CameraState;
import civgame.view.game.ConstructionMenuFactory;
import civgame.view.game.DockMenuFactory;
import civgame.view.game.ExplorerMenuFactory;
import civgame.view.game.FishingBoatMenuFactory;
import civgame.view.game.GameFrame;
import civgame.view.game.MapPanel;
import civgame.view.game.SaveLoadDialog;
import civgame.view.game.SelectionState;
import civgame.view.game.StationingMenuFactory;
import civgame.view.game.TownHallMenuFactory;
import civgame.view.game.UnitMovementAnimator;
import civgame.view.game.CombatMenuFactory;
import civgame.view.game.CombatResultDialog;
import civgame.view.game.TradingPostMenuFactory;
import civgame.view.game.TribeMenuFactory;
import civgame.view.hud.SelectedUnitPanel;
import civgame.view.trade.TradeDialog;
import civgame.view.trade.TradeInboxDialog;
import civgame.view.item.InventoryPanel;
import civgame.client.MultiplayerGameClientState;
import civgame.client.diplomacy.DiplomacyClientState;
import civgame.client.ClientNetworkManager;
import civgame.network.message.trade.TradeOfferRequest;
import civgame.network.message.trade.TradeAcceptRequest;
import civgame.network.message.trade.TradeRejectRequest;
import civgame.network.message.item.ItemUseRequest;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private final ExplorerMenuFactory explorerMenuFactory;
    private final CombatMenuFactory combatMenuFactory;
    private final CombatTargetService combatTargetService;
    private final AttackExecutionService attackExecutionService;
    private final TradingPostMenuFactory tradingPostMenuFactory;
    private final TribeMenuFactory tribeMenuFactory;
    private final DockMenuFactory dockMenuFactory;
    private final FishingBoatMenuFactory fishingBoatMenuFactory;
    private final NavalMovementService navalMovementService;
    private final GameSaveManager saveManager;
    private final GameLoadListener gameLoadListener;
    private final SelectedUnitPanel selectedUnitPanel;
    private final FogOfWarCalculator fogOfWarCalculator;
    private final StationingService stationingService;
    private final GameFrame gameFrame;
    private PvpAttackRequestListener pvpAttackRequestListener;
    private int lastDragX;
    private int lastDragY;
    private boolean wasDragged;
    private MultiplayerGameClientState multiplayerState;
    private DiplomacyClientState diplomacyState;
    private ClientNetworkManager networkManager;
    private String pendingItemIdForUse;

    public GameController(GameFrame gameFrame, GameSession gameSession, FogOfWarCalculator fogOfWarCalculator, GameLoadListener gameLoadListener) {
        this.gameLoadListener = gameLoadListener;
        this.mapPanel = gameFrame.getMapPanel();
        this.cameraState = gameFrame.getCameraState();
        this.selectionState = gameFrame.getSelectionState();
        this.movementAnimator = gameFrame.getMovementAnimator();
        this.gameSession = gameSession;
        this.pathFinder = new HexPathFinder();
        this.constructionMenuFactory = new ConstructionMenuFactory(new ConstructionService(), new RoadConstructionService(), new WallConstructionService(), new DemolitionService());
        this.stationingService = new StationingService();
        this.stationingMenuFactory = new StationingMenuFactory(stationingService);
        this.townHallMenuFactory = new TownHallMenuFactory();
        this.borderExpansionMenuFactory = new BorderExpansionMenuFactory(new BorderExpansionService());
        this.explorerMenuFactory = new ExplorerMenuFactory();
        this.combatMenuFactory = new CombatMenuFactory();
        this.combatTargetService = new CombatTargetService();
        this.attackExecutionService = new AttackExecutionService();
        this.tradingPostMenuFactory = new TradingPostMenuFactory(new TradingPostTradeService());
        this.tribeMenuFactory = new TribeMenuFactory(new TribeGiftService(), new TribeTradeService(), new TribeDiplomacyService(), new TribeMissionService());
        this.dockMenuFactory = new DockMenuFactory();
        this.fishingBoatMenuFactory = new FishingBoatMenuFactory(new FishingService());
        this.navalMovementService = new NavalMovementService();
        this.saveManager = new GameSaveManager();
        this.selectedUnitPanel = gameFrame.getSelectedUnitPanel();
        registerSaveLoadKeyBinding();
        this.fogOfWarCalculator = fogOfWarCalculator;
        this.gameFrame = gameFrame;
        this.multiplayerState = null;
        this.diplomacyState = null;
        this.networkManager = null;
        this.pendingItemIdForUse = null;
        registerListeners();
        registerEscapeKeyBinding();
        registerMultiplayerButtons();
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

        if (selectionState.isAttackTargetingActive()) {
            handleAttackTargetClick(clickedCoordinate);
            return;
        }

        if (pendingItemIdForUse != null) {
            handleItemUseClick(clickedCoordinate);
            return;
        }

        if (shouldPrioritizeMovingOntoOccupiedTile(clickedCoordinate)) {
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

    private boolean shouldPrioritizeMovingOntoOccupiedTile(HexCoordinate clickedCoordinate) {
        if (!selectionState.hasSelection()) {
            return false;
        }

        Unit selectedUnit = selectionState.getSelectedUnit();
        if (selectedUnit.getCoordinate().equals(clickedCoordinate)) {
            return false;
        }
        if (!selectionState.getReachableTiles().contains(clickedCoordinate)) {
            return false;
        }

        return !findUnitsAtCoordinate(clickedCoordinate).isEmpty();
    }

    private void handleAttackTargetClick(HexCoordinate clickedCoordinate) {
        Unit selectedUnit = selectionState.getSelectedUnit();

        selectionState.setAttackTargetingActive(false);
        selectionState.setAttackableTiles(new HashSet<HexCoordinate>());

        if (selectedUnit == null) {
            mapPanel.repaint();
            return;
        }

        HexCoordinate attackerCoordinate = selectedUnit.getCoordinate();

        if (!combatTargetService.isValidAttack(gameSession, attackerCoordinate, clickedCoordinate)) {
            mapPanel.repaint();
            return;
        }

        civgame.model.game.EnemyUnitMarker enemyUnit = gameSession.getEnemyUnitAt(clickedCoordinate);
        if (enemyUnit != null) {
            sendPvPAttackRequest(selectedUnit, enemyUnit);
            return;
        }

        AttackOutcome outcome;
        if (gameSession.getHexMap().hasActiveWall(attackerCoordinate, clickedCoordinate)) {
            outcome = resolveWallChoiceAndAttack(attackerCoordinate, clickedCoordinate);
        } else {
            outcome = attackExecutionService.executeAttack(gameSession, attackerCoordinate, clickedCoordinate);
        }

        if (outcome == null) {
            mapPanel.repaint();
            return;
        }

        if (outcome.getCombatResult() != null) {
            CombatResultDialog resultDialog = new CombatResultDialog(outcome.getCombatResult());
            resultDialog.setVisible(true);
        }

        refreshSelectedUnitDisplay();
        mapPanel.repaint();
    }
    
    private void handleItemUseClick(HexCoordinate clickedCoordinate) {
        if (pendingItemIdForUse == null) {
            return;
        }

        List<Unit> unitsHere = findUnitsAtCoordinate(clickedCoordinate);
        if (unitsHere.isEmpty()) {
            JOptionPane.showMessageDialog(gameFrame, "No unit at this location.", "Item Use", JOptionPane.WARNING_MESSAGE);
            pendingItemIdForUse = null;
            return;
        }

        Unit targetUnit = unitsHere.get(0);
        if (!isMyUnit(targetUnit)) {
            JOptionPane.showMessageDialog(gameFrame, "You can only use items on your own units.", "Item Use", JOptionPane.WARNING_MESSAGE);
            pendingItemIdForUse = null;
            return;
        }

        sendItemUseRequest(pendingItemIdForUse, targetUnit.getId(), clickedCoordinate);
        pendingItemIdForUse = null;
    }
    
    private boolean isMyUnit(Unit unit) {
        List<Unit> myUnits = gameSession.getUnits();
        for (int i = 0; i < myUnits.size(); i++) {
            if (myUnits.get(i).getId().equals(unit.getId())) {
                return true;
            }
        }
        return false;
    }
    
    private void sendPvPAttackRequest(Unit attackerUnit, civgame.model.game.EnemyUnitMarker enemyUnit) {
        if (pvpAttackRequestListener != null) {
            pvpAttackRequestListener.onAttackRequested(attackerUnit, enemyUnit);
        }
    }
    
    public void setPvpAttackRequestListener(PvpAttackRequestListener listener) {
        this.pvpAttackRequestListener = listener;
    }
    
    public PvpAttackRequestListener getPvpAttackRequestListener() {
        return pvpAttackRequestListener;
    }
    
    public interface PvpAttackRequestListener {
        void onAttackRequested(Unit attackerUnit, civgame.model.game.EnemyUnitMarker enemyUnit);
    }

    private AttackOutcome resolveWallChoiceAndAttack(HexCoordinate attackerCoordinate, HexCoordinate defenderCoordinate) {
        String[] options = {"Attack the Wall", "Attack Through (+2 defense penalty)", "Cancel"};
        int choice = JOptionPane.showOptionDialog(mapPanel, "A wall separates you from this hex.", "Wall Encountered",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            return attackExecutionService.executeWallAttack(gameSession, attackerCoordinate, defenderCoordinate);
        }
        if (choice == 1) {
            return attackExecutionService.executeAttack(gameSession, attackerCoordinate, defenderCoordinate);
        }
        return null;
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

        if (isNavalUnit(selectedUnit)) {
            Set<HexCoordinate> seaTiles = navalMovementService.findReachableSeaTiles(gameSession.getHexMap(), selectedUnit.getCoordinate(), selectedUnit.getCurrentActionPoints(), gameSession.getCurrentSeason());
            selectionState.setReachableTiles(seaTiles);
            return;
        }

        boolean allowUnseenTiles = selectedUnit.getUnitType() == UnitType.EXPLORER;
        boolean navalTechUnlocked = gameSession.getTownHallUpgradeService().isUnlocked(TownHallUpgradeType.NAVAL_TECHNIQUES);

        Set<HexCoordinate> reachableTiles = pathFinder.findReachableTiles(
                gameSession.getHexMap(),
                selectedUnit.getCoordinate(),
                selectedUnit.getCurrentActionPoints(),
                allowUnseenTiles,
                navalTechUnlocked,
                gameSession.getCurrentSeason(),
                gameSession
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

        TradingPost tradingPost = gameSession.getTradingPostAt(clickedCoordinate);
        if (tradingPost != null) {
            showTradingPostMenu(tradingPost, mouseEvent);
            return;
        }

        Tribe tribe = gameSession.getTribeAt(clickedCoordinate);
        if (tribe != null && tribe.isDiscovered()) {
            showTribeMenu(tribe, mouseEvent);
            return;
        }

        if (gameSession.getBuildingAt(clickedCoordinate) instanceof Dock) {
            showDockMenu((Dock) gameSession.getBuildingAt(clickedCoordinate), mouseEvent);
            return;
        }

        List<Unit> unitsHere = findUnitsAtCoordinate(clickedCoordinate);
        if (unitsHere.isEmpty()) {
            return;
        }

        Unit targetUnit = resolveRightClickTarget(unitsHere);
        selectionState.setSelectedUnit(targetUnit);
        updateReachableTilesForSelection();
        refreshSelectedUnitDisplay();
        mapPanel.repaint();

        if (targetUnit.getUnitType() == UnitType.BUILDER) {
            showConstructionMenu((Builder) targetUnit, mouseEvent);
        } else if (targetUnit.getUnitType() == UnitType.WORKER) {
            showStationingMenu((Worker) targetUnit, mouseEvent);
        } else if (targetUnit.getUnitType() == UnitType.BORDER_EXPANDER) {
            showBorderExpansionMenu((BorderExpander) targetUnit, mouseEvent);
        } else if (targetUnit.getUnitType() == UnitType.EXPLORER) {
            showExplorerMenu((Explorer) targetUnit, mouseEvent);
        } else if (MilitaryUnitCapProvider.isMilitaryUnitType(targetUnit.getUnitType())) {
            showCombatMenu(targetUnit, mouseEvent);
        } else if (targetUnit.getUnitType() == UnitType.FISHING_BOAT) {
            showFishingBoatMenu((FishingBoat) targetUnit, mouseEvent);
        }
    }

    private Unit resolveRightClickTarget(List<Unit> unitsHere) {
        Unit currentlySelected = selectionState.getSelectedUnit();
        int currentIndex = unitsHere.indexOf(currentlySelected);

        if (currentIndex == -1) {
            return unitsHere.get(0);
        }

        int nextIndex = (currentIndex + 1) % unitsHere.size();
        return unitsHere.get(nextIndex);
    }
    
    public boolean canAttackEnemyUnit(civgame.model.game.EnemyUnitMarker enemyUnit) {
        return enemyUnit != null && enemyUnit.isMilitaryUnit();
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

    private void showExplorerMenu(Explorer explorer, MouseEvent mouseEvent) {
        Runnable onSuccess = createRepaintRunnable();
        JPopupMenu popupMenu = explorerMenuFactory.buildMenu(explorer, onSuccess);
        popupMenu.show(mapPanel, mouseEvent.getX(), mouseEvent.getY());
    }

    private void showCombatMenu(Unit unit, MouseEvent mouseEvent) {
        Runnable onAttackModeActivated = new Runnable() {
            @Override
            public void run() {
                selectionState.setAttackableTiles(combatTargetService.getAttackableTargets(gameSession, unit.getCoordinate()));
                selectionState.setAttackTargetingActive(true);
                mapPanel.repaint();
            }
        };

        JPopupMenu popupMenu = combatMenuFactory.buildMenu(gameSession, unit, onAttackModeActivated);
        popupMenu.show(mapPanel, mouseEvent.getX(), mouseEvent.getY());
    }
    
    private void showTradingPostMenu(TradingPost tradingPost, MouseEvent mouseEvent) {
        Runnable onSuccess = createRepaintRunnable();
        JPopupMenu popupMenu = tradingPostMenuFactory.buildMenu(gameSession, tradingPost, onSuccess);
        popupMenu.show(mapPanel, mouseEvent.getX(), mouseEvent.getY());
    }

    private void showTribeMenu(Tribe tribe, MouseEvent mouseEvent) {
        Runnable onSuccess = createRepaintRunnable();
        JPopupMenu popupMenu = tribeMenuFactory.buildMenu(gameSession, tribe, onSuccess);
        popupMenu.show(mapPanel, mouseEvent.getX(), mouseEvent.getY());
    }

    private void showDockMenu(Dock dock, MouseEvent mouseEvent) {
        Runnable onSuccess = createRepaintRunnable();
        JPopupMenu popupMenu = dockMenuFactory.buildMenu(gameSession, dock, onSuccess);
        popupMenu.show(mapPanel, mouseEvent.getX(), mouseEvent.getY());
    }

    private void showFishingBoatMenu(FishingBoat boat, MouseEvent mouseEvent) {
        Runnable onSuccess = createRepaintRunnable();
        JPopupMenu popupMenu = fishingBoatMenuFactory.buildMenu(gameSession, boat, onSuccess);
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
                gameFrame.refreshHud(gameSession);
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

        if (isNavalUnit(selectedUnit)) {
            attemptMoveNavalUnit(selectedUnit, destination);
            return;
        }

        boolean allowUnseenTiles = selectedUnit.getUnitType() == UnitType.EXPLORER;
        boolean navalTechUnlocked = gameSession.getTownHallUpgradeService().isUnlocked(TownHallUpgradeType.NAVAL_TECHNIQUES);

        List<HexCoordinate> fullPath = pathFinder.findPath(gameSession.getHexMap(), selectedUnit.getCoordinate(), destination, allowUnseenTiles, navalTechUnlocked, gameSession.getCurrentSeason(), gameSession);

        if (fullPath == null || fullPath.isEmpty()) {
            return;
        }

        int movementCost = recalculatePathCost(selectedUnit.getCoordinate(), fullPath);

        if (!selectedUnit.hasEnoughActionPoints(movementCost)) {
            return;
        }

        if (MilitaryUnitCapProvider.isMilitaryUnitType(selectedUnit.getUnitType())) {
            if (!MilitaryStackingRules.canAcceptUnit(gameSession, destination, selectedUnit.getUnitType())) {
                return;
            }
        }

        unstationIfWorkerIsLeavingItsBuilding(selectedUnit);

        selectedUnit.consumeActionPoints(movementCost);
        refreshSelectedUnitDisplay();
        movementAnimator.startMovement(selectedUnit, fullPath, createMovementFinishedCallback(selectedUnit));
    }

    private boolean isNavalUnit(Unit unit) {
        return unit.getUnitType() == UnitType.FISHING_BOAT || unit.getUnitType() == UnitType.GALLEY;
    }

    private void attemptMoveNavalUnit(Unit selectedUnit, HexCoordinate destination) {
        if (!navalMovementService.isValidNavalDestination(gameSession.getHexMap(), destination)) {
            return;
        }

        List<HexCoordinate> fullPath = navalMovementService.findSeaPath(gameSession.getHexMap(), selectedUnit.getCoordinate(), destination);
        if (fullPath == null || fullPath.isEmpty()) {
            return;
        }

        int movementCost = fullPath.size() * navalMovementService.getStepCost(gameSession.getCurrentSeason());
        if (!selectedUnit.hasEnoughActionPoints(movementCost)) {
            return;
        }

        selectedUnit.consumeActionPoints(movementCost);
        refreshSelectedUnitDisplay();
        movementAnimator.startMovement(selectedUnit, fullPath, createMovementFinishedCallback(selectedUnit));
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

    private int recalculatePathCost(HexCoordinate startCoordinate, List<HexCoordinate> path) {
        int totalCost = 0;
        HexCoordinate previousCoordinate = startCoordinate;
        SeasonType currentSeason = gameSession.getCurrentSeason();

        for (int i = 0; i < path.size(); i++) {
            HexCoordinate currentCoordinate = path.get(i);
            HexTile tile = gameSession.getHexMap().getTile(currentCoordinate);
            int baseCost = TerrainMovementCostProvider.getMovementCost(tile.getTerrainType(), currentSeason);
            totalCost = totalCost + EdgeMovementCostProvider.getStepCost(gameSession.getHexMap(), previousCoordinate, currentCoordinate, baseCost);
            previousCoordinate = currentCoordinate;
        }

        return totalCost;
    }

    private Runnable createMovementFinishedCallback(Unit movedUnit) {
        return new Runnable() {
            @Override
            public void run() {
                HexTile arrivalTile = gameSession.getHexMap().getTile(movedUnit.getCoordinate());
                boolean nowOnSea = arrivalTile != null && arrivalTile.getTerrainType() == TerrainType.SEA;
                movedUnit.setFloating(nowOnSea);
                if (nowOnSea) {
                    movedUnit.zeroRemainingActionPoints();
                }

                if (MilitaryUnitCapProvider.isMilitaryUnitType(movedUnit.getUnitType()) 
                    && movedUnit.getCoordinate().equals(gameSession.getTownHall().getCoordinate())) {
                    gameSession.getHappinessService().applyOneTimeDelta(Constants.TOWN_HALL_GARRISON_HAPPINESS_BONUS);
                }

                fogOfWarCalculator.updateVisibility(gameSession);
                if (selectionState.hasSelection()) {
                    updateReachableTilesForSelection();
                }
                refreshSelectedUnitDisplay();
                mapPanel.repaint();
            }
        };
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

    private void registerSaveLoadKeyBinding() {
        InputMap inputMap = mapPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = mapPanel.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "openSaveLoad");
        actionMap.put("openSaveLoad", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SaveLoadDialog dialog = new SaveLoadDialog(saveManager, gameSession, new SaveLoadDialog.LoadCallback() {
                    @Override
                    public void onGameLoaded(civgame.model.game.GameSession loadedGameSession) {
                        gameLoadListener.onGameLoaded(loadedGameSession);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    private void registerMultiplayerButtons() {
        civgame.view.hud.HudPanel hudPanel = gameFrame.getHudPanel();
        hudPanel.setTradeButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openTradeDialog();
            }
        });
        hudPanel.setInboxButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openTradeInbox();
            }
        });
        hudPanel.setInventoryButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openInventory();
            }
        });
    }
    
    public void setMultiplayerState(MultiplayerGameClientState state) {
        this.multiplayerState = state;
    }
    
    public void setDiplomacyState(DiplomacyClientState state) {
        this.diplomacyState = state;
    }
    
    public void setNetworkManager(ClientNetworkManager manager) {
        this.networkManager = manager;
    }
    
    public void openTradeDialog() {
        if (multiplayerState == null) {
            JOptionPane.showMessageDialog(gameFrame, "Trade is only available in multiplayer mode.", "Trade", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (!multiplayerState.isMyTurn()) {
            JOptionPane.showMessageDialog(gameFrame, "You can only trade during your turn.", "Trade", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<String> playerNames = new ArrayList<>();
        List<String> playerClientIds = new ArrayList<>();
        if (diplomacyState != null) {
            List<DiplomacyClientState.OpponentInfo> opponents = diplomacyState.getAllOpponents();
            for (int i = 0; i < opponents.size(); i++) {
                playerNames.add(opponents.get(i).getUsername());
                playerClientIds.add(opponents.get(i).getClientId());
            }
        }
        if (playerNames.isEmpty()) {
            JOptionPane.showMessageDialog(gameFrame, "No other players available for trade.", "Trade", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        TradeDialog dialog = new TradeDialog(gameFrame, playerNames, playerClientIds);
        dialog.setSubmitListener(new TradeDialog.TradeSubmitListener() {
            @Override
            public void onTradeSubmitted(String receiverClientId,
                                         Map<civgame.model.resource.ResourceType, Integer> offered,
                                         Map<civgame.model.resource.ResourceType, Integer> requested) {
                sendTradeOffer(receiverClientId, offered, requested);
            }
        });
        dialog.setVisible(true);
    }

    private void sendTradeOffer(String receiverClientId,
                                Map<civgame.model.resource.ResourceType, Integer> offered,
                                Map<civgame.model.resource.ResourceType, Integer> requested) {
        if (networkManager == null || multiplayerState == null) {
            return;
        }
        TradeOfferRequest request = new TradeOfferRequest(multiplayerState.getMyClientId(), receiverClientId, offered, requested);
        try {
            networkManager.sendMessageObjectAsync(request);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(gameFrame, "Failed to send trade offer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void openTradeInbox() {
        TradeInboxDialog dialog = new TradeInboxDialog(gameFrame);
        dialog.setActionListener(new TradeInboxDialog.TradeActionListener() {
            @Override
            public void onAccept(String offerId) {
                sendTradeAccept(offerId);
            }

            @Override
            public void onReject(String offerId) {
                sendTradeReject(offerId);
            }
        });
        dialog.setVisible(true);
    }

    private void sendTradeAccept(String offerId) {
        if (networkManager == null || multiplayerState == null) {
            return;
        }
        TradeAcceptRequest request = new TradeAcceptRequest(multiplayerState.getMyClientId(), offerId);
        try {
            networkManager.sendMessageObjectAsync(request);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(gameFrame, "Failed to accept trade: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendTradeReject(String offerId) {
        if (networkManager == null || multiplayerState == null) {
            return;
        }
        TradeRejectRequest request = new TradeRejectRequest(multiplayerState.getMyClientId(), offerId);
        try {
            networkManager.sendMessageObjectAsync(request);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(gameFrame, "Failed to reject trade: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void openInventory() {
        InventoryPanel dialog = new InventoryPanel(gameFrame);
        List<Item> items = new ArrayList<>();
        if (gameSession.getInventory() != null) {
            items = gameSession.getInventory().getAllItems();
        }
        dialog.updateItems(items);
        dialog.setUseListener(new InventoryPanel.ItemUseListener() {
            @Override
            public void onItemUse(String itemId) {
                selectUnitForItemUse(itemId);
            }
        });
        dialog.setVisible(true);
    }
    
    private void selectUnitForItemUse(String itemId) {
        JOptionPane.showMessageDialog(gameFrame, "Click on a unit to use the item.", "Item Use", JOptionPane.INFORMATION_MESSAGE);
        pendingItemIdForUse = itemId;
    }
    
    private void sendItemUseRequest(String itemId, String targetUnitId, HexCoordinate targetCoordinate) {
        if (networkManager == null || multiplayerState == null) {
            return;
        }
        ItemUseRequest request = new ItemUseRequest(multiplayerState.getMyClientId(), itemId, targetUnitId, targetCoordinate.getQ(), targetCoordinate.getR());
        try {
            networkManager.sendMessageObjectAsync(request);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(gameFrame, "Failed to use item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void handleDiplomacyAction(DiplomacyClientState.OpponentInfo opponent, civgame.view.diplomacy.DiplomacyPanel.DiplomacyAction action) {
        if (networkManager == null || multiplayerState == null) {
            return;
        }
        if (!multiplayerState.isMyTurn()) {
            JOptionPane.showMessageDialog(gameFrame, "You can only perform diplomatic actions during your turn.", "Diplomacy", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (action == civgame.view.diplomacy.DiplomacyPanel.DiplomacyAction.DECLARE_WAR) {
                civgame.network.message.diplomacy.DeclareWarRequest request = new civgame.network.message.diplomacy.DeclareWarRequest(multiplayerState.getMyClientId(), opponent.getClientId());
                networkManager.sendMessageObjectAsync(request);
            } else if (action == civgame.view.diplomacy.DiplomacyPanel.DiplomacyAction.OFFER_PEACE) {
                civgame.network.message.diplomacy.OfferPeaceRequest request = new civgame.network.message.diplomacy.OfferPeaceRequest(multiplayerState.getMyClientId(), opponent.getClientId());
                networkManager.sendMessageObjectAsync(request);
            } else if (action == civgame.view.diplomacy.DiplomacyPanel.DiplomacyAction.PROPOSE_ALLIANCE) {
                civgame.network.message.diplomacy.ProposeAllianceRequest request = new civgame.network.message.diplomacy.ProposeAllianceRequest(multiplayerState.getMyClientId(), opponent.getClientId());
                networkManager.sendMessageObjectAsync(request);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(gameFrame, "Failed to send diplomatic action: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}