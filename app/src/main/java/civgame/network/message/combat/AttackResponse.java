package civgame.network.message.combat;

import civgame.network.message.MessageType;
import civgame.network.message.Response;

public class AttackResponse extends Response {

    private String reason;

    public AttackResponse() {
        super();
        setType(MessageType.ATTACK_RESPONSE);
    }

    public AttackResponse(String requestId, boolean success, String reason) {
        super(MessageType.ATTACK_RESPONSE, requestId, success);
        this.reason = reason;
    }

    public AttackResponse(String requestId, String errorMessage) {
        super(MessageType.ATTACK_RESPONSE, requestId, errorMessage);
        this.reason = errorMessage;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}