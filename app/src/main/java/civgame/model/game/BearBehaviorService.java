package civgame.model.game;

import civgame.model.animal.Bear;
import civgame.model.animal.WildAnimal;
import civgame.model.map.HexCoordinate;
import civgame.model.map.HexMap;
import civgame.model.map.TerrainType;
import civgame.model.unit.MilitaryUnitCapProvider;
import civgame.model.unit.Unit;
import civgame.util.Constants;
import civgame.util.UnitIdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BearBehaviorService {

    private final Random random;
    private final WildAnimalCombatService wildAnimalCombatService;
    private int lastAttackTurn;

    public BearBehaviorService() {
        this.random = new Random();
        this.wildAnimalCombatService = new WildAnimalCombatService();
        this.lastAttackTurn = -Constants.BEAR_ATTACK_COOLDOWN_TURNS;
    }

    public boolean isEligible(GameSession gameSession) {
        if (gameSession.getCurrentTurnNumber() - lastAttackTurn < Constants.BEAR_ATTACK_COOLDOWN_TURNS) {
            return false;
        }
        if (hasActiveBear(gameSession)) {
            return false;
        }
        return findForestDen(gameSession) != null;
    }

    private boolean hasActiveBear(GameSession gameSession) {
        List<WildAnimal> animals = gameSession.getWildAnimals();
        for (int i = 0; i < animals.size(); i++) {
            if (animals.get(i) instanceof Bear) {
                return true;
            }
        }
        return false;
    }

    public void triggerBearAttack(GameSession gameSession) {
        HexCoordinate denCoordinate = findForestDen(gameSession);
        if (denCoordinate == null) {
            return;
        }

        int nearbyPlayerUnitCount = countPlayerUnitsNear(gameSession, denCoordinate);
        int bearCount = nearbyPlayerUnitCount >= Constants.BEAR_TRIGGER_UNIT_COUNT ? 2 : 1;

        for (int i = 0; i < bearCount; i++) {
            HexCoordinate spawnCoordinate = findFreeForestSpot(gameSession, denCoordinate);
            String id = UnitIdGenerator.generateNextId("BEAR");
            Bear bear = new Bear(id, spawnCoordinate, denCoordinate);
            gameSession.addWildAnimal(bear);
        }

        lastAttackTurn = gameSession.getCurrentTurnNumber();
        gameSession.addTribeNotification("Bears have emerged from the forest near (" + denCoordinate.getQ() + ", " + denCoordinate.getR() + ")!");
    }

    public void processAllBearTurns(GameSession gameSession) {
        List<WildAnimal> allAnimals = new ArrayList<WildAnimal>(gameSession.getWildAnimals());

        for (int i = 0; i < allAnimals.size(); i++) {
            if (!(allAnimals.get(i) instanceof Bear)) {
                continue;
            }
            Bear bear = (Bear) allAnimals.get(i);
            bear.refreshActionPoints();
            processSingleBearTurn(gameSession, bear);
        }
    }

    private void processSingleBearTurn(GameSession gameSession, Bear bear) {
        Unit target = findNearestTarget(gameSession, bear);

        if (target == null) {
            gameSession.removeWildAnimal(bear);
            return;
        }

        int distance = bear.getCoordinate().distanceTo(target.getCoordinate());
        if (distance <= bear.getAttackRange()) {
            wildAnimalCombatService.processWildAnimalAttack(gameSession, bear, target.getCoordinate());
            return;
        }

        moveTowardTarget(gameSession, bear, target.getCoordinate());
    }

    private Unit findNearestTarget(GameSession gameSession, Bear bear) {
        Unit nearestCivilian = null;
        Unit nearestMilitary = null;
        int nearestCivilianDistance = Integer.MAX_VALUE;
        int nearestMilitaryDistance = Integer.MAX_VALUE;

        List<Unit> units = gameSession.getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            int distanceFromDen = bear.getDenCoordinate().distanceTo(unit.getCoordinate());
            if (distanceFromDen > Constants.BEAR_TARGET_RADIUS) {
                continue;
            }

            int distanceFromBear = bear.getCoordinate().distanceTo(unit.getCoordinate());
            if (MilitaryUnitCapProvider.isMilitaryUnitType(unit.getUnitType())) {
                if (distanceFromBear < nearestMilitaryDistance) {
                    nearestMilitaryDistance = distanceFromBear;
                    nearestMilitary = unit;
                }
            } else {
                if (distanceFromBear < nearestCivilianDistance) {
                    nearestCivilianDistance = distanceFromBear;
                    nearestCivilian = unit;
                }
            }
        }

        return nearestCivilian != null ? nearestCivilian : nearestMilitary;
    }

    private void moveTowardTarget(GameSession gameSession, Bear bear, HexCoordinate targetCoordinate) {
        HexMap hexMap = gameSession.getHexMap();

        while (bear.hasEnoughActionPoints(1)) {
            List<HexCoordinate> neighbors = bear.getCoordinate().getNeighbors();
            HexCoordinate bestStep = null;
            int bestDistance = bear.getCoordinate().distanceTo(targetCoordinate);

            for (int i = 0; i < neighbors.size(); i++) {
                HexCoordinate neighbor = neighbors.get(i);
                if (!hexMap.containsCoordinate(neighbor)) {
                    continue;
                }
                TerrainType terrain = hexMap.getTile(neighbor).getTerrainType();
                if (terrain == TerrainType.SEA || terrain == TerrainType.MOUNTAIN_RANGE) {
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

            bear.setCoordinate(bestStep);
            bear.consumeActionPoints(1);

            if (bear.getCoordinate().distanceTo(targetCoordinate) <= bear.getAttackRange()) {
                return;
            }
        }
    }

    private HexCoordinate findForestDen(GameSession gameSession) {
        List<HexCoordinate> forestCoordinates = new ArrayList<HexCoordinate>();
        for (HexCoordinate coordinate : gameSession.getHexMap().getAllTiles().keySet()) {
            if (gameSession.getHexMap().getTile(coordinate).getTerrainType() == TerrainType.FOREST) {
                forestCoordinates.add(coordinate);
            }
        }

        if (forestCoordinates.isEmpty()) {
            return null;
        }

        return forestCoordinates.get(random.nextInt(forestCoordinates.size()));
    }

    private HexCoordinate findFreeForestSpot(GameSession gameSession, HexCoordinate denCoordinate) {
        if (isHexFreeOfLife(gameSession, denCoordinate)) {
            return denCoordinate;
        }

        List<HexCoordinate> neighbors = denCoordinate.getNeighbors();
        for (int i = 0; i < neighbors.size(); i++) {
            HexCoordinate neighbor = neighbors.get(i);
            if (!gameSession.getHexMap().containsCoordinate(neighbor)) {
                continue;
            }
            if (gameSession.getHexMap().getTile(neighbor).getTerrainType() == TerrainType.FOREST && isHexFreeOfLife(gameSession, neighbor)) {
                return neighbor;
            }
        }

        return denCoordinate;
    }

    private boolean isHexFreeOfLife(GameSession gameSession, HexCoordinate coordinate) {
        List<Unit> units = gameSession.getUnits();
        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getCoordinate().equals(coordinate)) {
                return false;
            }
        }

        List<WildAnimal> animals = gameSession.getWildAnimals();
        for (int i = 0; i < animals.size(); i++) {
            if (animals.get(i).getCoordinate().equals(coordinate)) {
                return false;
            }
        }

        return true;
    }

    private int countPlayerUnitsNear(GameSession gameSession, HexCoordinate denCoordinate) {
        int count = 0;
        List<Unit> units = gameSession.getUnits();
        for (int i = 0; i < units.size(); i++) {
            if (denCoordinate.distanceTo(units.get(i).getCoordinate()) <= Constants.BEAR_TARGET_RADIUS) {
                count = count + 1;
            }
        }
        return count;
    }
}