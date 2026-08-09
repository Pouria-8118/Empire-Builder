package civgame.model.game;

import civgame.model.map.HexCoordinate;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;
import civgame.util.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: when the defender hex has a Building but no defending units,
// apply direct structure damage here (sum of attacker structure-attack power,
// no dice) instead of falling through to the unit-vs-unit dice path.

// TODO: add dedicated entry points for wild-animal and barbarian
// defenders, which reuse CombatResolutionService/CombatDamageApplier but pass a
// fixed defender dice count (1 or 2) instead of countDistinctTypes(...).
public class AttackExecutionService {

    private final CombatTargetService combatTargetService;
    private final CombatResolutionService combatResolutionService;
    private final CombatDamageApplier combatDamageApplier;

    public AttackExecutionService() {
        this.combatTargetService = new CombatTargetService();
        this.combatResolutionService = new CombatResolutionService();
        this.combatDamageApplier = new CombatDamageApplier();
    }

    public AttackOutcome executeAttack(GameSession gameSession, HexCoordinate attackerCoordinate, HexCoordinate defenderCoordinate) {
        if (!combatTargetService.isValidAttack(gameSession, attackerCoordinate, defenderCoordinate)) {
            return null;
        }

        List<Unit> attackerParticipants = combatTargetService.resolveParticipatingUnits(gameSession, attackerCoordinate, defenderCoordinate);

        for (int i = 0; i < attackerParticipants.size(); i++) {
            attackerParticipants.get(i).consumeActionPoints(Constants.ATTACK_ACTION_POINT_COST);
        }

        List<Unit> defenderUnits = findUnitsAt(gameSession, defenderCoordinate);
        boolean defenderHasBuilding = gameSession.getBuildingAt(defenderCoordinate) != null;

        if (defenderUnits.isEmpty() && !defenderHasBuilding) {
            gameSession.claimBorderTile(defenderCoordinate);
            return new AttackOutcome(null, true, new ArrayList<Unit>(), new ArrayList<Unit>());
        }

        int attackerDiceCount = countDistinctTypes(attackerParticipants);
        int defenderDiceCount = countDistinctTypes(defenderUnits);

        CombatResult combatResult = combatResolutionService.resolveCombat(attackerDiceCount, defenderDiceCount);

        List<Unit> attackerLosses = combatDamageApplier.applyHitsToUnits(gameSession, attackerParticipants, combatResult.getAttackerHits());
        List<Unit> defenderLosses = combatDamageApplier.applyHitsToUnits(gameSession, defenderUnits, combatResult.getDefenderHits());

        return new AttackOutcome(combatResult, false, attackerLosses, defenderLosses);
    }

    private List<Unit> findUnitsAt(GameSession gameSession, HexCoordinate coordinate) {
        List<Unit> result = new ArrayList<Unit>();
        List<Unit> allUnits = gameSession.getUnits();
        for (int i = 0; i < allUnits.size(); i++) {
            if (allUnits.get(i).getCoordinate().equals(coordinate)) {
                result.add(allUnits.get(i));
            }
        }
        return result;
    }

    private int countDistinctTypes(List<Unit> units) {
        Set<UnitType> types = new HashSet<UnitType>();
        for (int i = 0; i < units.size(); i++) {
            types.add(units.get(i).getUnitType());
        }
        return types.size();
    }
}