package civgame.model.game;

import civgame.model.building.TownHallLevel;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SaveMetadata implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String saveName;
    private final int turnNumber;
    private final SeasonType season;
    private final String realSaveTime;
    private final TownHallLevel townHallLevel;

    public SaveMetadata(String saveName, int turnNumber, SeasonType season, TownHallLevel townHallLevel) {
        this.saveName = saveName;
        this.turnNumber = turnNumber;
        this.season = season;
        this.townHallLevel = townHallLevel;
        this.realSaveTime = LocalDateTime.now().toString();
    }

    public String getSaveName() {
        return saveName;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public SeasonType getSeason() {
        return season;
    }

    public String getRealSaveTime() {
        return realSaveTime;
    }

    public TownHallLevel getTownHallLevel() {
        return townHallLevel;
    }

    public String buildSummaryText() {
        return saveName + "  |  Turn " + turnNumber + "  |  " + season.name() + "  |  " + townHallLevel.name() + "  |  " + realSaveTime;
    }
}