package civgame.model.combat;

// TODO: military Unit subclasses will implement this interface
// once actual combat units are introduced.
public interface Attackable {

    int getCurrentHitPoints();

    int getMaxHitPoints();

    boolean isDestroyedByCombat();

    void applyCombatDamage(int amount);
}