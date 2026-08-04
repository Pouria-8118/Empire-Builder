package civgame.model.combat;

// TODO: military Unit subclasses (Swordsman, Archer, Cavalry, Catapult, ...)
// will implement this interface once actual combat units are introduced.
public interface Combatant extends Attackable {

    int getAttackPower();

    int getAttackRange();
}