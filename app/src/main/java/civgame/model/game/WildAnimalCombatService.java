package civgame.model.game;

import civgame.model.animal.WildAnimal;
import civgame.model.map.HexCoordinate;
import civgame.model.unit.MilitaryUnitCapProvider;
import civgame.model.unit.Unit;
import civgame.util.Constants;

import java.util.ArrayList;
import java.util.List;


public class WildAnimalCombatService {

    private final CombatResolutionService combatResolutionService;
    private final CombatDamageApplier combatDamageApplier;

    public WildAnimalCombatService() {
        this.combatResolutionService = new CombatResolutionService();
        this.combatDamageApplier = new CombatDamageApplier();
    }

    public void processWildAnimalTurn(GameSession gameSession, WildAnimal wildAnimal) {
        processWildAnimalAttack(gameSession, wildAnimal, wildAnimal.getCoordinate());
    }

    public void processWildAnimalAttack(GameSession gameSession, WildAnimal wildAnimal, HexCoordinate targetCoordinate) {
        List<Unit> militaryUnits = findMilitaryUnitsAt(gameSession, targetCoordinate);

        if (!militaryUnits.isEmpty()) {
            resolveMilitaryDefense(gameSession, wildAnimal, militaryUnits);
            return;
        }

        List<Unit> civilianUnits = findCivilianUnitsAt(gameSession, targetCoordinate);
        attackCivilianUnits(gameSession, civilianUnits);
    }

    private void resolveMilitaryDefense(GameSession gameSession, WildAnimal wildAnimal, List<Unit> militaryUnits) {
        CombatResult combatResult = combatResolutionService.resolveCombat(Constants.WILD_ANIMAL_DICE_COUNT, Constants.WILD_ANIMAL_DEFENSE_DICE_COUNT, 0);

        wildAnimal.applyCombatDamage(combatResult.getAttackerHits());
        combatDamageApplier.applyHitsToUnits(gameSession, militaryUnits, combatResult.getDefenderHits());

        if (wildAnimal.isDestroyedByCombat()) {
            gameSession.removeWildAnimal(wildAnimal);
        }
    }

    private void attackCivilianUnits(GameSession gameSession, List<Unit> civilianUnits) {
        for (int i = 0; i < civilianUnits.size(); i++) {
            gameSession.removeUnit(civilianUnits.get(i));
        }
    }

    private List<Unit> findMilitaryUnitsAt(GameSession gameSession, HexCoordinate coordinate) {
        List<Unit> result = new ArrayList<Unit>();
        List<Unit> allUnits = gameSession.getUnits();
        for (int i = 0; i < allUnits.size(); i++) {
            Unit unit = allUnits.get(i);
            if (unit.getCoordinate().equals(coordinate) && MilitaryUnitCapProvider.isMilitaryUnitType(unit.getUnitType())) {
                result.add(unit);
            }
        }
        return result;
    }

    private List<Unit> findCivilianUnitsAt(GameSession gameSession, HexCoordinate coordinate) {
        List<Unit> result = new ArrayList<Unit>();
        List<Unit> allUnits = gameSession.getUnits();
        for (int i = 0; i < allUnits.size(); i++) {
            Unit unit = allUnits.get(i);
            if (unit.getCoordinate().equals(coordinate) && !MilitaryUnitCapProvider.isMilitaryUnitType(unit.getUnitType())) {
                result.add(unit);
            }
        }
        return result;
    }
}