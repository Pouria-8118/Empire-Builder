package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitFactory;
import civgame.model.unit.UnitType;
import civgame.util.Constants;

import java.util.List;

public class TurnEngine {

    private final ResourceProductionService resourceProductionService;
    private final UpkeepService upkeepService;
    private final UnitFactory unitFactory;
    private final SpawnPositionFinder spawnPositionFinder;

    public TurnEngine() {
        this.resourceProductionService = new ResourceProductionService();
        this.upkeepService = new UpkeepService();
        this.unitFactory = new UnitFactory();
        this.spawnPositionFinder = new SpawnPositionFinder();
    }

    public TurnSummary executeTurn(GameSession gameSession) {
        refreshAllUnitActionPoints(gameSession);

        resourceProductionService.applyProduction(gameSession);
        processProductionQueue(gameSession);

        List<String> destroyedBuildingIds = upkeepService.applyBuildingUpkeep(gameSession);
        boolean starvationTriggered = upkeepService.applyUnitFoodConsumption(gameSession);

        gameSession.setInStarvation(starvationTriggered);
        if (starvationTriggered) {
            applyStarvationPenalty(gameSession);
        }

        gameSession.incrementTurnNumber();

        return new TurnSummary(destroyedBuildingIds, starvationTriggered, gameSession.getCurrentTurnNumber());
    }

    private void refreshAllUnitActionPoints(GameSession gameSession) {
        List<Unit> units = gameSession.getUnits();
        for (int i = 0; i < units.size(); i++) {
            units.get(i).refreshActionPoints();
        }
    }

    private void applyStarvationPenalty(GameSession gameSession) {
        List<Unit> units = gameSession.getUnits();
        for (int i = 0; i < units.size(); i++) {
            units.get(i).applyActionPointPenalty(Constants.STARVATION_ACTION_POINT_PENALTY);
        }
    }

    private void processProductionQueue(GameSession gameSession) {
        ProductionQueueItem completedItem = gameSession.getTownHallProductionQueue().advanceOneTurn();
        if (completedItem == null) {
            return;
        }

        if (completedItem.getCategory() == ProductionQueueItem.QueueItemCategory.UNIT) {
            completeUnitProduction(gameSession, completedItem.getUnitType());
        } else {
            gameSession.getTownHallUpgradeService().applyUpgradeEffects(gameSession, completedItem.getUpgradeType());
        }
    }

    private void completeUnitProduction(GameSession gameSession, UnitType unitType) {
        if (gameSession.hasReachedUnitCap()) {
            return;
        }

        HexCoordinate spawnCoordinate = spawnPositionFinder.findFreeNeighbor(gameSession, gameSession.getTownHall().getCoordinate());
        Unit newUnit = unitFactory.createUnit(unitType, spawnCoordinate);
        gameSession.addUnit(newUnit);
    }
}