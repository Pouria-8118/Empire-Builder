package civgame.model.tribe;

import civgame.util.Constants;

public class TribeRelationStateProvider {

    private TribeRelationStateProvider() {
    }

    public static TribeRelationState getState(int relationshipValue) {
        if (relationshipValue <= Constants.TRIBE_ENEMY_MAX_RELATIONSHIP) {
            return new EnemyRelationState();
        }
        if (relationshipValue <= Constants.TRIBE_DISPLEASED_MAX_RELATIONSHIP) {
            return new DispleasedRelationState();
        }
        if (relationshipValue <= Constants.TRIBE_NEUTRAL_MAX_RELATIONSHIP) {
            return new NeutralRelationState();
        }
        if (relationshipValue <= Constants.TRIBE_FRIENDLY_MAX_RELATIONSHIP) {
            return new FriendlyRelationState();
        }
        return new AlliedRelationState();
    }
}