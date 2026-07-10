package civgame.model.game;

import civgame.model.unit.Unit;
import civgame.util.Constants;

import java.util.List;

public class TurnEngine {

    private final ResourceProductionService resourceProductionService;
    private final UpkeepService upkeepService;

    public TurnEngine() {
        this.resourceProductionService = new ResourceProductionService();
        this.upkeepService = new UpkeepService();
    }

    public TurnSummary executeTurn(GameSession gameSession) {
        refreshAllUnitActionPoints(gameSession);

        resourceProductionService.applyProduction(gameSession);

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

    // TODO : re-run FogOfWarCalculator.updateVisibility() is handled by TurnController,
    // not here, to keep this class focused only on the economic turn cycle
}