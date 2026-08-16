package civgame.model.tribe;

public class EnemyRelationState implements TribeRelationState {

    @Override
    public String getName() {
        return "Enemy";
    }

    @Override
    public boolean isGiftAllowed() {
        return false;
    }

    @Override
    public boolean isTradeAllowed() {
        return false;
    }

    @Override
    public boolean isMissionAllowed() {
        return false;
    }

    @Override
    public boolean isAllianceAllowed() {
        return false;
    }

    @Override
    public boolean isAtWar() {
        return true;
    }
}