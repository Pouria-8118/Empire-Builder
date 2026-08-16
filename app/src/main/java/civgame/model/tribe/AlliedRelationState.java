package civgame.model.tribe;

public class AlliedRelationState implements TribeRelationState {

    @Override
    public String getName() {
        return "Allied";
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
        return true;
    }

    @Override
    public boolean isAtWar() {
        return false;
    }
}