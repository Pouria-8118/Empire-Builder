package civgame.model.tribe;

public class FriendlyRelationState implements TribeRelationState {

    @Override
    public String getName() {
        return "Friendly";
    }

    @Override
    public boolean isGiftAllowed() {
        return true;
    }

    @Override
    public boolean isTradeAllowed() {
        return true;
    }

    @Override
    public boolean isMissionAllowed() {
        return true;
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