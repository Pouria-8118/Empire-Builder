package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitFactory;
import civgame.model.unit.UnitType;

public class UnitProductionCommand implements ProductionCommand {

    private final UnitType unitType;
    private final int totalTurns;
    private final UnitFactory unitFactory;
    private final SpawnPositionFinder spawnPositionFinder;
    private int turnsRemaining;

    public UnitProductionCommand(UnitType unitType, int totalTurns) {
        this.unitType = unitType;
        this.totalTurns = totalTurns;
        this.turnsRemaining = totalTurns;
        this.unitFactory = new UnitFactory();
        this.spawnPositionFinder = new SpawnPositionFinder();
    }

    @Override
    public void execute(GameSession gameSession) {
        if (gameSession.hasReachedUnitCap()) {
            return;
        }

        HexCoordinate spawnCoordinate = spawnPositionFinder.findFreeNeighbor(gameSession, gameSession.getTownHall().getCoordinate());
        Unit newUnit = unitFactory.createUnit(unitType, spawnCoordinate);
        gameSession.addUnit(newUnit);
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