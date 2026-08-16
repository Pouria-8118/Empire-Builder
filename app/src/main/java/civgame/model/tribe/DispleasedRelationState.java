package civgame.model.tribe;

public class DispleasedRelationState implements TribeRelationState {

    @Override
    public String getName() {
        return "Displeased";
    }

    @Override
    public boolean isGiftAllowed() {
        return true;
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
        return false;
    }
}