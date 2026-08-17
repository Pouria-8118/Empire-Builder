package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.building.BuildingType;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.TerrainType;
import civgame.model.pirate.Pirate;
import civgame.model.unit.MilitaryUnitCapProvider;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;
import civgame.util.Constants;
import civgame.util.UnitIdGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PirateBehaviorService {

    private final Random random;
    private final CombatResolutionService combatResolutionService;
    private final CombatDamageApplier combatDamageApplier;

    public PirateBehaviorService() {
        this.random = new Random();
        this.combatResolutionService = new CombatResolutionService();
        this.combatDamageApplier = new CombatDamageApplier();
    }

    public void maybeSpawnPirate(GameSession gameSession) {
        if (gameSession.getPirates().size() >= Constants.MAX_ACTIVE_PIRATES) {
            return;
        }
        if (random.nextDouble() >= Constants.PIRATE_SPAWN_CHANCE_PER_TURN) {
            return;
        }

        HexCoordinate spawnCoordinate = pickRandomSeaTile(gameSession);
        if (spawnCoordinate == null) {
            return;
        }

        String id = UnitIdGenerator.generateNextId("PIRATE");
        gameSession.addPirate(new Pirate(id, spawnCoordinate, Constants.PIRATE_MAX_HIT_POINTS, Constants.PIRATE_MAX_ACTION_POINTS));
    }

    public void processAllPirateTurns(GameSession gameSession) {
        List<Pirate> pirates = new ArrayList<Pirate>(gameSession.getPirates());
        for (int i = 0; i < pirates.size(); i++) {
            processSinglePirateTurn(gameSession, pirates.get(i));
        }
    }

    private void processSinglePirateTurn(GameSession gameSession, Pirate pirate) {
        pirate.refreshActionPoints();

        Object target = findNearestTarget(gameSession, pirate);
        if (target == null) {
            wander(gameSession, pirate);
            return;
        }

        HexCoordinate targetCoordinate = resolveTargetCoordinate(target);
        int distance = pirate.getCoordinate().distanceTo(targetCoordinate);

        if (distance <= 1) {
            attackTarget(gameSession, pirate, target);
            return;
        }

        moveToward(gameSession, pirate, targetCoordinate);
    }

    private Object findNearestTarget(GameSession gameSession, Pirate pirate) {
        Unit nearestNaval = null;
        int nearestNavalDistance = Integer.MAX_VALUE;

        List<Unit> units = gameSession.getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            boolean isNavalOrFloating = unit.getUnitType() == UnitType.GALLEY || unit.getUnitType() == UnitType.FISHING_BOAT || unit.isFloating();
            if (!isNavalOrFloating) {
                continue;
            }
            int distance = pirate.getCoordinate().distanceTo(unit.getCoordinate());
            if (distance < nearestNavalDistance) {
                nearestNavalDistance = distance;
                nearestNaval = unit;
            }
        }

        if (nearestNaval != null) {
            return nearestNaval;
        }

        Building nearestDock = null;
        int nearestDockDistance = Integer.MAX_VALUE;
        List<Building> buildings = gameSession.getAllBuildings();
        for (int i = 0; i < buildings.size(); i++) {
            Building building = buildings.get(i);
            if (building.isDestroyed() || building.getBuildingType() != BuildingType.DOCK) {
                continue;
            }
            int distance = pirate.getCoordinate().distanceTo(building.getCoordinate());
            if (distance <= Constants.PIRATE_DOCK_ATTACK_RANGE && distance < nearestDockDistance) {
                nearestDockDistance = distance;
                nearestDock = building;
            }
        }

        return nearestDock;
    }

    private HexCoordinate resolveTargetCoordinate(Object target) {
        if (target instanceof Unit) {
            return ((Unit) target).getCoordinate();
        }
        return ((Building) target).getCoordinate();
    }

    private void attackTarget(GameSession gameSession, Pirate pirate, Object target) {
        if (target instanceof Building) {
            Building dock = (Building) target;
            dock.applyCombatDamage(Constants.PIRATE_DOCK_DAMAGE);
            if (dock.isDestroyedByCombat()) {
                gameSession.removeBuildingAt(dock.getCoordinate());
            }
            gameSession.addTribeNotification("Pirates attacked your Dock!");
            return;
        }

        Unit unit = (Unit) target;
        List<Unit> defenders = new ArrayList<Unit>();
        defenders.add(unit);

        int defenderDiceCount = MilitaryUnitCapProvider.isDiceParticipant(unit.getUnitType()) || unit.getUnitType() == UnitType.GALLEY ? 1 : 0;
        CombatResult combatResult = combatResolutionService.resolveCombat(Constants.PIRATE_DICE_COUNT, defenderDiceCount, 0);

        combatDamageApplier.applyHitsToUnits(gameSession, defenders, combatResult.getDefenderHits());
        pirate.applyCombatDamage(combatResult.getAttackerHits());

        if (pirate.isDestroyedByCombat()) {
            gameSession.removePirate(pirate);
        }

        gameSession.addTribeNotification("Pirates attacked one of your ships!");
    }

    private void moveToward(GameSession gameSession, Pirate pirate, HexCoordinate targetCoordinate) {
        HexMap hexMap = gameSession.getHexMap();

        while (pirate.hasEnoughActionPoints(1)) {
            List<HexCoordinate> neighbors = pirate.getCoordinate().getNeighbors();
            HexCoordinate bestStep = null;
            int bestDistance = pirate.getCoordinate().distanceTo(targetCoordinate);

            for (int i = 0; i < neighbors.size(); i++) {
                HexCoordinate neighbor = neighbors.get(i);
                if (!hexMap.containsCoordinate(neighbor) || hexMap.getTile(neighbor).getTerrainType() != TerrainType.SEA) {
                    continue;
                }
                int distance = neighbor.distanceTo(targetCoordinate);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestStep = neighbor;
                }
            }

            if (bestStep == null) {
                return;
            }

            pirate.setCoordinate(bestStep);
            pirate.consumeActionPoints(1);

            if (pirate.getCoordinate().distanceTo(targetCoordinate) <= 1) {
                return;
            }
        }
    }

    private void wander(GameSession gameSession, Pirate pirate) {
        HexMap hexMap = gameSession.getHexMap();
        List<HexCoordinate> neighbors = pirate.getCoordinate().getNeighbors();
        Collections.shuffle(neighbors, random);

        for (int i = 0; i < neighbors.size(); i++) {
            HexCoordinate neighbor = neighbors.get(i);
            if (hexMap.containsCoordinate(neighbor) && hexMap.getTile(neighbor).getTerrainType() == TerrainType.SEA) {
                pirate.setCoordinate(neighbor);
                pirate.consumeActionPoints(1);
                return;
            }
        }
    }

    private HexCoordinate pickRandomSeaTile(GameSession gameSession) {
        List<HexCoordinate> seaCoordinates = new ArrayList<HexCoordinate>();
        for (HexCoordinate coordinate : gameSession.getHexMap().getAllTiles().keySet()) {
            if (gameSession.getHexMap().getTile(coordinate).getTerrainType() == TerrainType.SEA) {
                seaCoordinates.add(coordinate);
            }
        }
        if (seaCoordinates.isEmpty()) {
            return null;
        }
        return seaCoordinates.get(random.nextInt(seaCoordinates.size()));
    }
}