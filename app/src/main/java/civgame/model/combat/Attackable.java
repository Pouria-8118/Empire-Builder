package civgame.model.combat;

// TODO: military Unit subclasses and Building (including TownHall and Wall)
// will implement this interface once hit points and combat damage are introduced.
public interface Attackable {

    int getCurrentHitPoints();

    int getMaxHitPoints();

    boolean isDestroyedByCombat();

    void applyCombatDamage(int amount);
}