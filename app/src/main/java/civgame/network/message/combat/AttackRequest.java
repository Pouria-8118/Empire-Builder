package civgame.network.message.combat;

import civgame.model.map.HexCoordinate;
import civgame.network.message.MessageType;
import civgame.network.message.Request;

public class AttackRequest extends Request {

    private int attackerQ;
    private int attackerR;
    private int defenderQ;
    private int defenderR;
    private String defenderUnitId;
    private String defenderClientId;

    public AttackRequest() {
        super();
        setType(MessageType.ATTACK_REQUEST);
    }

    public AttackRequest(String senderId,
                          HexCoordinate attackerCoordinate,
                          HexCoordinate defenderCoordinate,
                          String defenderUnitId,
                          String defenderClientId) {
        super(MessageType.ATTACK_REQUEST, senderId);
        this.attackerQ = attackerCoordinate.getQ();
        this.attackerR = attackerCoordinate.getR();
        this.defenderQ = defenderCoordinate.getQ();
        this.defenderR = defenderCoordinate.getR();
        this.defenderUnitId = defenderUnitId;
        this.defenderClientId = defenderClientId;
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

    public String getDefenderUnitId() {
        return defenderUnitId;
    }

    public void setDefenderUnitId(String defenderUnitId) {
        this.defenderUnitId = defenderUnitId;
    }

    public String getDefenderClientId() {
        return defenderClientId;
    }

    public void setDefenderClientId(String defenderClientId) {
        this.defenderClientId = defenderClientId;
    }
}