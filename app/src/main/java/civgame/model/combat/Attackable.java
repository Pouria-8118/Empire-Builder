package civgame.model.combat;

public interface Attackable {

    int getCurrentHitPoints();

    int getMaxHitPoints();

    boolean isDestroyedByCombat();

    void applyCombatDamage(int amount);
}