package civgame.model.game;

import civgame.util.Constants;

public class HappinessService implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int happinessValue;

    public HappinessService() {
        this.happinessValue = 0;
    }

    public int getHappinessValue() {
        return happinessValue;
    }

    public HappinessLevel getCurrentLevel() {
        if (happinessValue >= Constants.GOLDEN_AGE_MIN_HAPPINESS) {
            return HappinessLevel.GOLDEN_AGE;
        }
        if (happinessValue <= Constants.REBELLION_MAX_HAPPINESS) {
            return HappinessLevel.REBELLION;
        }
        if (happinessValue <= Constants.DISCONTENT_MAX_HAPPINESS) {
            return HappinessLevel.DISCONTENT;
        }
        return HappinessLevel.NORMAL;
    }

    public void applyOneTimeDelta(int delta) {
        happinessValue = happinessValue + delta;
    }
}