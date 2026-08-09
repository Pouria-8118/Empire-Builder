package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.unit.MilitaryUnitCapProvider;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitFactory;
import civgame.model.unit.UnitType;
import civgame.util.Constants;

public class UnitProductionCommand implements ProductionCommand {

    private final UnitType unitType;
    private final int totalTurns;
    private final UnitFactory unitFactory;
    private final SpawnPositionFinder spawnPositionFinder;
    private final MilitarySpawnFinder militarySpawnFinder;
    private int turnsRemaining;

    public UnitProductionCommand(UnitType unitType, int totalTurns) {
        this.unitType = unitType;
        this.totalTurns = totalTurns;
        this.turnsRemaining = totalTurns;
        this.unitFactory = new UnitFactory();
        this.spawnPositionFinder = new SpawnPositionFinder();
        this.militarySpawnFinder = new MilitarySpawnFinder();
    }

    @Override
    public void execute(GameSession gameSession) {
        boolean isMilitary = MilitaryUnitCapProvider.isMilitaryUnitType(unitType);

        if (isMilitary) {
            if (gameSession.hasReachedMilitaryUnitCap()) {
                return;
            }
        } else if (gameSession.hasReachedUnitCap()) {
            return;
        }

        HexCoordinate spawnCoordinate;
        if (isMilitary) {
            spawnCoordinate = militarySpawnFinder.findSpawnCoordinate(gameSession, unitType);
        } else {
            spawnCoordinate = spawnPositionFinder.findFreeNeighbor(gameSession, gameSession.getTownHall().getCoordinate());
        }

        Unit newUnit = unitFactory.createUnit(unitType, spawnCoordinate);
        gameSession.addUnit(newUnit);

        if (isMilitary && gameSession.hasReachedMilitaryUnitCap()) {
            gameSession.getHappinessService().applyOneTimeDelta(Constants.UNIT_CAP_REACHED_HAPPINESS_PENALTY);
        }
    }

    @Override
    public void cancel(GameSession gameSession) {
        // Resources already spent when this command was queued are not refunded on cancellation.
    }

    @Override
    public void advanceOneTurn() {
        if (turnsRemaining > 0) {
            turnsRemaining = turnsRemaining - 1;
        }
    }

    @Override
    public boolean isFinished() {
        return turnsRemaining <= 0;
    }

    @Override
    public int getTotalTurns() {
        return totalTurns;
    }

    @Override
    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    @Override
    public String describeForHud() {
        return unitType.name() + " - " + turnsRemaining + " turns left";
    }

    public UnitType getUnitType() {
        return unitType;
    }
}