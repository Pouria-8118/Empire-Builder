package civgame.model.game;

import civgame.model.event.TurnEndedEvent;
import civgame.model.tribe.Tribe;
import civgame.model.unit.Unit;
import civgame.model.unit.Worker;
import civgame.model.animal.WildAnimal;
import civgame.util.Constants;

import java.util.List;
import java.util.ArrayList;

public class TurnEngine {

    private final ResourceProductionService resourceProductionService;
    private final UpkeepService upkeepService;
    private final AutoExploreService autoExploreService;
    private final WildAnimalCombatService wildAnimalCombatService;
    private final TribeMissionService tribeMissionService;
    private final TribeBehaviorService tribeBehaviorService;

    public TurnEngine() {
        this.resourceProductionService = new ResourceProductionService();
        this.upkeepService = new UpkeepService();
        this.autoExploreService = new AutoExploreService();
        this.wildAnimalCombatService = new WildAnimalCombatService();
        this.tribeMissionService = new TribeMissionService();
        this.tribeBehaviorService = new TribeBehaviorService();
    }

    public TurnSummary executeTurn(GameSession gameSession) {
        refreshAllUnitActionPoints(gameSession);
        applyRebellionMovementPenalty(gameSession);
        autoExploreService.runAutoExploreForAllUnits(gameSession);
        processWildAnimalTurns(gameSession);

        List<String> depletedResourceDescriptions = resourceProductionService.applyProduction(gameSession);
        String completedProductionDescription = processProductionQueue(gameSession);

        List<String> destroyedBuildingIds = upkeepService.applyBuildingUpkeep(gameSession);
        upkeepService.applyWallUpkeep(gameSession);
        boolean starvationTriggered = upkeepService.applyUnitFoodConsumption(gameSession);

        gameSession.setInStarvation(starvationTriggered);
        if (starvationTriggered) {
            applyStarvationPenalty(gameSession);
        }

        gameSession.getHappinessService().applyPerTurnContributions(gameSession);
        gameSession.resetBazaarTradeFlag();
        gameSession.resetTradingPostTradeFlag();
        resetTribeTradeFlags(gameSession);
        tribeMissionService.checkAllActiveMissionConditions(gameSession);
        tribeBehaviorService.processTribeTurns(gameSession);
        tribeBehaviorService.processTribeTurns(gameSession);

        gameSession.incrementTurnNumber();
        gameSession.getEventBus().publish(new TurnEndedEvent(gameSession.getCurrentTurnNumber()));

        return new TurnSummary(destroyedBuildingIds, starvationTriggered, gameSession.getCurrentTurnNumber(), completedProductionDescription, depletedResourceDescriptions);
    }

    private void refreshAllUnitActionPoints(GameSession gameSession) {
        List<Unit> units = gameSession.getUnits();
        for (int i = 0; i < units.size(); i++) {
            units.get(i).refreshActionPoints();
        }
    }

    private void applyRebellionMovementPenalty(GameSession gameSession) {
        if (gameSession.getHappinessService().getCurrentLevel() != HappinessLevel.REBELLION) {
            return;
        }

        List<Unit> units = gameSession.getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (unit instanceof Worker) {
                unit.applyActionPointPenalty(Constants.REBELLION_ACTION_POINT_PENALTY);
            }
        }
        // TODO: also apply this penalty to military units once they exist.
    }

    private void processWildAnimalTurns(GameSession gameSession) {
        List<WildAnimal> wildAnimalsSnapshot = new ArrayList<WildAnimal>(gameSession.getWildAnimals());
        for (int i = 0; i < wildAnimalsSnapshot.size(); i++) {
            wildAnimalCombatService.processWildAnimalTurn(gameSession, wildAnimalsSnapshot.get(i));
        }
    }
    
    private void resetTribeTradeFlags(GameSession gameSession) {
        List<Tribe> tribes = gameSession.getTribes();
        for (int i = 0; i < tribes.size(); i++) {
            tribes.get(i).setTradedThisTurn(false);
        }
    }
    

    private void applyStarvationPenalty(GameSession gameSession) {
        List<Unit> units = gameSession.getUnits();
        for (int i = 0; i < units.size(); i++) {
            units.get(i).applyActionPointPenalty(Constants.STARVATION_ACTION_POINT_PENALTY);
        }
    }

    private String processProductionQueue(GameSession gameSession) {
        ProductionCommand completedCommand = gameSession.getTownHallProductionQueue().advanceOneTurn(gameSession);
        if (completedCommand == null) {
            return null;
        }

        if (completedCommand instanceof UnitProductionCommand) {
            UnitProductionCommand unitCommand = (UnitProductionCommand) completedCommand;
            return "New unit ready: " + unitCommand.getUnitType().name();
        }

        TownHallUpgradeCommand upgradeCommand = (TownHallUpgradeCommand) completedCommand;
        return "Upgrade completed: " + upgradeCommand.getUpgradeType().name();
    }
}