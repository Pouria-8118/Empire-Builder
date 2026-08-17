package civgame.model.tribe;

public class TribeMission implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final TribeType tribeType;
    private final String title;
    private final int deadlineTurn;
    private MissionStatus status;
    private int warriorKillCount;

    public TribeMission(TribeType tribeType, String title, int deadlineTurn) {
        this.tribeType = tribeType;
        this.title = title;
        this.deadlineTurn = deadlineTurn;
        this.status = MissionStatus.ACTIVE;
        this.warriorKillCount = 0;
    }

    public TribeType getTribeType() {
        return tribeType;
    }

    public String getTitle() {
        return title;
    }

    public int getDeadlineTurn() {
        return deadlineTurn;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public int getWarriorKillCount() {
        return warriorKillCount;
    }

    public void setWarriorKillCount(int warriorKillCount) {
        this.warriorKillCount = warriorKillCount;
    }
}