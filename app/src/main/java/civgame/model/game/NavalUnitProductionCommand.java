package civgame.model.game;

import civgame.model.building.Dock;
import civgame.model.map.HexCoordinate;
import civgame.model.map.TerrainType;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitFactory;
import civgame.model.unit.UnitType;

import java.util.List;

public class NavalUnitProductionCommand implements ProductionCommand {

    private final UnitType unitType;
    private final Dock dock;
    private final int totalTurns;
    private final UnitFactory unitFactory;
    private int turnsRemaining;

    public NavalUnitProductionCommand(UnitType unitType, Dock dock, int totalTurns) {
        this.unitType = unitType;
        this.dock = dock;
        this.totalTurns = totalTurns;
        this.turnsRemaining = totalTurns;
        this.unitFactory = new UnitFactory();
    }

    @Override
    public void execute(GameSession gameSession) {
        HexCoordinate spawnCoordinate = findAdjacentSeaTile(gameSession);
        if (spawnCoordinate == null) {
            return;
        }
        Unit newUnit = unitFactory.createUnit(unitType, spawnCoordinate);
        gameSession.addUnit(newUnit);
    }

    private HexCoordinate findAdjacentSeaTile(GameSession gameSession) {
        List<HexCoordinate> neighbors = dock.getCoordinate().getNeighbors();
        for (int i = 0; i < neighbors.size(); i++) {
            HexCoordinate neighbor = neighbors.get(i);
            if (gameSession.getHexMap().containsCoordinate(neighbor) && gameSession.getHexMap().getTile(neighbor).getTerrainType() == TerrainType.SEA) {
                return neighbor;
            }
        }
        return null;
    }

    @Override
    public void cancel(GameSession gameSession) {
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
}