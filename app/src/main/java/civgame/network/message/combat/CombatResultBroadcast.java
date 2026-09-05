package civgame.network.message.combat;

import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;

import java.util.ArrayList;
import java.util.List;

public class CombatResultBroadcast extends Broadcast {

    private String attackerClientId;
    private String attackerUsername;
    private String defenderClientId;
    private String defenderUsername;
    private int attackerQ;
    private int attackerR;
    private int defenderQ;
    private int defenderR;
    private CombatResultDTO combatResult;
    private List<UnitLossDTO> attackerLosses;
    private List<UnitLossDTO> defenderLosses;
    private boolean autoCaptured;
    private int buildingDamage;
    private boolean buildingDestroyed;

    public CombatResultBroadcast() {
        super();
        setType(MessageType.COMBAT_RESULT_BROADCAST);
        this.attackerLosses = new ArrayList<>();
        this.defenderLosses = new ArrayList<>();
        this.buildingDamage = 0;
        this.buildingDestroyed = false;
    }

    public CombatResultBroadcast(String broadcasterId) {
        super(MessageType.COMBAT_RESULT_BROADCAST, broadcasterId);
        this.attackerLosses = new ArrayList<>();
        this.defenderLosses = new ArrayList<>();
        this.buildingDamage = 0;
        this.buildingDestroyed = false;
    }

    public String getAttackerClientId() {
        return attackerClientId;
    }

    public void setAttackerClientId(String attackerClientId) {
        this.attackerClientId = attackerClientId;
    }

    public String getAttackerUsername() {
        return attackerUsername;
    }

    public void setAttackerUsername(String attackerUsername) {
        this.attackerUsername = attackerUsername;
    }

    public String getDefenderClientId() {
        return defenderClientId;
    }

    public void setDefenderClientId(String defenderClientId) {
        this.defenderClientId = defenderClientId;
    }

    public String getDefenderUsername() {
        return defenderUsername;
    }

    public void setDefenderUsername(String defenderUsername) {
        this.defenderUsername = defenderUsername;
    }

    public int getAttackerQ() {
        return attackerQ;
    }

    public void setAttackerQ(int attackerQ) {
        this.attackerQ = attackerQ;
    }

    public int getAttackerR() {
        return attackerR;
    }

    public void setAttackerR(int attackerR) {
        this.attackerR = attackerR;
    }

    public int getDefenderQ() {
        return defenderQ;
    }

    public void setDefenderQ(int defenderQ) {
        this.defenderQ = defenderQ;
    }

    public int getDefenderR() {
        return defenderR;
    }

    public void setDefenderR(int defenderR) {
        this.defenderR = defenderR;
    }

    public CombatResultDTO getCombatResult() {
        return combatResult;
    }

    public void setCombatResult(CombatResultDTO combatResult) {
        this.combatResult = combatResult;
    }

    public List<UnitLossDTO> getAttackerLosses() {
        return attackerLosses;
    }

    public void setAttackerLosses(List<UnitLossDTO> attackerLosses) {
        this.attackerLosses = attackerLosses;
    }

    public List<UnitLossDTO> getDefenderLosses() {
        return defenderLosses;
    }

    public void setDefenderLosses(List<UnitLossDTO> defenderLosses) {
        this.defenderLosses = defenderLosses;
    }

    public boolean isAutoCaptured() {
        return autoCaptured;
    }

    public void setAutoCaptured(boolean autoCaptured) {
        this.autoCaptured = autoCaptured;
    }

    public int getBuildingDamage() {
        return buildingDamage;
    }

    public void setBuildingDamage(int buildingDamage) {
        this.buildingDamage = buildingDamage;
    }

    public boolean isBuildingDestroyed() {
        return buildingDestroyed;
    }

    public void setBuildingDestroyed(boolean buildingDestroyed) {
        this.buildingDestroyed = buildingDestroyed;
    }

    public static class CombatResultDTO {
        private List<Integer> attackerDice;
        private List<Integer> defenderDice;
        private int attackerHits;
        private int defenderHits;

        public CombatResultDTO() {
            this.attackerDice = new ArrayList<>();
            this.defenderDice = new ArrayList<>();
        }

        public List<Integer> getAttackerDice() {
            return attackerDice;
        }

        public void setAttackerDice(List<Integer> attackerDice) {
            this.attackerDice = attackerDice;
        }

        public List<Integer> getDefenderDice() {
            return defenderDice;
        }

        public void setDefenderDice(List<Integer> defenderDice) {
            this.defenderDice = defenderDice;
        }

        public int getAttackerHits() {
            return attackerHits;
        }

        public void setAttackerHits(int attackerHits) {
            this.attackerHits = attackerHits;
        }

        public int getDefenderHits() {
            return defenderHits;
        }

        public void setDefenderHits(int defenderHits) {
            this.defenderHits = defenderHits;
        }
    }

    public static class UnitLossDTO {
        private String unitId;
        private String ownerClientId;
        private String unitType;

        public UnitLossDTO() {
        }

        public UnitLossDTO(String unitId, String ownerClientId, String unitType) {
            this.unitId = unitId;
            this.ownerClientId = ownerClientId;
            this.unitType = unitType;
        }

        public String getUnitId() {
            return unitId;
        }

        public void setUnitId(String unitId) {
            this.unitId = unitId;
        }

        public String getOwnerClientId() {
            return ownerClientId;
        }

        public void setOwnerClientId(String ownerClientId) {
            this.ownerClientId = ownerClientId;
        }

        public String getUnitType() {
            return unitType;
        }

        public void setUnitType(String unitType) {
            this.unitType = unitType;
        }
    }
}