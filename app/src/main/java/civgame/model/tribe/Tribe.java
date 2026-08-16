package civgame.model.tribe;

import civgame.model.combat.Attackable;
import civgame.model.map.HexCoordinate;
import civgame.util.Constants;

public class Tribe implements Attackable {

    private final String id;
    private final String name;
    private final TribeType tribeType;
    private final HexCoordinate campCoordinate;
    private int currentHitPoints;
    private final int maxHitPoints;
    private int relationshipValue;
    private boolean discovered;
    private boolean allied;
    private boolean tradedThisTurn;
    private TribeMission activeMission;
    private int missionCooldownUntilTurn;
    private double tradeRateBonus;

    public Tribe(String id, String name, TribeType tribeType, HexCoordinate campCoordinate) {
        this.id = id;
        this.name = name;
        this.tribeType = tribeType;
        this.campCoordinate = campCoordinate;
        this.maxHitPoints = TribeCampHitPointsProvider.getMaxHitPoints(tribeType);
        this.currentHitPoints = maxHitPoints;
        this.relationshipValue = 0;
        this.discovered = false;
        this.allied = false;
        this.tradedThisTurn = false;
        this.activeMission = null;
        this.missionCooldownUntilTurn = 0;
        this.tradeRateBonus = 0.0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TribeType getTribeType() {
        return tribeType;
    }

    public HexCoordinate getCampCoordinate() {
        return campCoordinate;
    }

    public boolean isDiscovered() {
        return discovered;
    }

    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }

    public int getRelationshipValue() {
        return relationshipValue;
    }

    public void changeRelationship(int delta) {
        relationshipValue = relationshipValue + delta;
        if (relationshipValue > 100) {
            relationshipValue = 100;
        }
        if (relationshipValue < -100) {
            relationshipValue = -100;
        }
        if (allied && relationshipValue < Constants.TRIBE_ALLIANCE_MIN_RELATIONSHIP) {
            allied = false;
        }
    }

    public void setRelationshipValue(int relationshipValue) {
        this.relationshipValue = relationshipValue;
    }

    public TribeRelationState getRelationState() {
        return TribeRelationStateProvider.getState(relationshipValue);
    }

    public boolean isAllied() {
        return allied;
    }

    public boolean hasTradedThisTurn() {
        return tradedThisTurn;
    }

    public void setTradedThisTurn(boolean tradedThisTurn) {
        this.tradedThisTurn = tradedThisTurn;
    }

    public TribeMission getActiveMission() {
        return activeMission;
    }

    public void setActiveMission(TribeMission activeMission) {
        this.activeMission = activeMission;
    }

    public int getMissionCooldownUntilTurn() {
        return missionCooldownUntilTurn;
    }

    public void setMissionCooldownUntilTurn(int missionCooldownUntilTurn) {
        this.missionCooldownUntilTurn = missionCooldownUntilTurn;
    }

    public double getTradeRateBonus() {
        return tradeRateBonus;
    }

    public void setTradeRateBonus(double tradeRateBonus) {
        this.tradeRateBonus = tradeRateBonus;
    }

    public void setAllied(boolean allied) {
        this.allied = allied;
    }

    @Override
    public int getCurrentHitPoints() {
        return currentHitPoints;
    }

    @Override
    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    @Override
    public boolean isDestroyedByCombat() {
        return currentHitPoints <= 0;
    }

    @Override
    public void applyCombatDamage(int amount) {
        currentHitPoints = currentHitPoints - amount;
        if (currentHitPoints < 0) {
            currentHitPoints = 0;
        }
    }
}