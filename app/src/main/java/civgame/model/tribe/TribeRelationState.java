package civgame.model.tribe;

public interface TribeRelationState {

    String getName();

    boolean isGiftAllowed();

    boolean isTradeAllowed();

    boolean isMissionAllowed();

    boolean isAllianceAllowed();

    boolean isAtWar();

}