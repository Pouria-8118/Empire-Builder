package civgame.model.game;

public class SaveLoadResult {

    private final boolean success;
    private final String message;

    private SaveLoadResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static SaveLoadResult success(String message) {
        return new SaveLoadResult(true, message);
    }

    public static SaveLoadResult failure(String message) {
        return new SaveLoadResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}