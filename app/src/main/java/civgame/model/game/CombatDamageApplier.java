package civgame.model.game;

import civgame.model.combat.Combatant;
import civgame.model.unit.Unit;
import civgame.model.unit.UnitType;

import java.util.ArrayList;
import java.util.List;

public class CombatDamageApplier {

    public List<Unit> applyHitsToUnits(GameSession gameSession, List<Unit> unitsOnHex, int hitCount) {
        List<Unit> orderedTargets = orderByPriority(unitsOnHex);
        List<Unit> killedUnits = new ArrayList<Unit>();
        int remainingHits = hitCount;

        for (int i = 0; i < orderedTargets.size() && remainingHits > 0; i++) {
            Unit unit = orderedTargets.get(i);
            if (!(unit instanceof Combatant)) {
                continue;
            }

            Combatant combatant = (Combatant) unit;
            combatant.applyCombatDamage(1);
            remainingHits = remainingHits - 1;

            if (combatant.isDestroyedByCombat()) {
                killedUnits.add(unit);
            }
        }

        for (int i = 0; i < killedUnits.size(); i++) {
            gameSession.removeUnit(killedUnits.get(i));
        }

        return killedUnits;
    }

    private List<Unit> orderByPriority(List<Unit> units) {
        List<Unit> ordered = new ArrayList<Unit>();
        addByType(units, ordered, UnitType.SWORDSMAN);
        addByType(units, ordered, UnitType.ARCHER);
        addByType(units, ordered, UnitType.CAVALRY);
        return ordered;
    }

    private void addByType(List<Unit> source, List<Unit> destination, UnitType unitType) {
        for (int i = 0; i < source.size(); i++) {
            if (source.get(i).getUnitType() == unitType) {
                destination.add(source.get(i));
            }
        }
    }
}