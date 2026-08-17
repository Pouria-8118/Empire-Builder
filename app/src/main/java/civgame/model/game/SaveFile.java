package civgame.model.game;

import java.io.Serializable;

public class SaveFile implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int CURRENT_FORMAT_VERSION = 1;

    private final int formatVersion;
    private final SaveMetadata metadata;
    private final GameSession gameSession;

    public SaveFile(SaveMetadata metadata, GameSession gameSession) {
        this.formatVersion = CURRENT_FORMAT_VERSION;
        this.metadata = metadata;
        this.gameSession = gameSession;
    }

    public int getFormatVersion() {
        return formatVersion;
    }

    public static int getCurrentFormatVersion() {
        return CURRENT_FORMAT_VERSION;
    }

    public SaveMetadata getMetadata() {
        return metadata;
    }

    public GameSession getGameSession() {
        return gameSession;
    }
}