package civgame.model.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class GameSaveManager {

    private static final String SAVE_DIRECTORY = "saves";
    private static final String FILE_EXTENSION = ".sav";
    private static final String TEMP_EXTENSION = ".tmp";

    public GameSaveManager() {
        File directory = new File(SAVE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public SaveLoadResult save(SaveSlot slot, String saveName, GameSession gameSession) {
        SaveMetadata metadata = new SaveMetadata(saveName, gameSession.getCurrentTurnNumber(), gameSession.getCurrentSeason(), gameSession.getTownHall().getLevel());
        SaveFile saveFile = new SaveFile(metadata, gameSession);

        File targetFile = resolveFile(slot);
        File tempFile = new File(targetFile.getPath() + TEMP_EXTENSION);

        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(tempFile));
            outputStream.writeObject(saveFile);
            outputStream.close();
        } catch (IOException exception) {
            return SaveLoadResult.failure("Could not write save file: " + exception.getMessage());
        }

        if (targetFile.exists()) {
            targetFile.delete();
        }
        boolean replaced = tempFile.renameTo(targetFile);
        if (!replaced) {
            return SaveLoadResult.failure("Could not finalize the save file on disk.");
        }

        return SaveLoadResult.success("Saved to " + slot.name());
    }

    public boolean slotExists(SaveSlot slot) {
        return resolveFile(slot).exists();
    }

    public SaveMetadata peekMetadata(SaveSlot slot) {
        if (!slotExists(slot)) {
            return null;
        }
        try {
            SaveFile saveFile = readSaveFile(resolveFile(slot));
            return saveFile == null ? null : saveFile.getMetadata();
        } catch (Exception exception) {
            return null;
        }
    }

    public boolean deleteSlot(SaveSlot slot) {
        File file = resolveFile(slot);
        if (!file.exists()) {
            return false;
        }
        return file.delete();
    }

    public LoadOutcome load(SaveSlot slot) {
        File file = resolveFile(slot);
        if (!file.exists()) {
            return LoadOutcome.failure("No save found in this slot.");
        }

        SaveFile saveFile;
        try {
            saveFile = readSaveFile(file);
        } catch (InvalidClassException exception) {
            return LoadOutcome.failure("This save was made with an incompatible game version and cannot be loaded.");
        } catch (StreamCorruptedException exception) {
            return LoadOutcome.failure("This save file is corrupted.");
        } catch (Exception exception) {
            return LoadOutcome.failure("Could not read save file: " + exception.getMessage());
        }

        if (saveFile == null || saveFile.getFormatVersion() != SaveFile.getCurrentFormatVersion()) {
            return LoadOutcome.failure("This save file uses an incompatible format version.");
        }

        GameSession gameSession = saveFile.getGameSession();
        gameSession.reconnectAfterLoad();

        return LoadOutcome.success(gameSession);
    }

    private SaveFile readSaveFile(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
        Object loadedObject = inputStream.readObject();
        inputStream.close();
        return (SaveFile) loadedObject;
    }

    public File resolveFile(SaveSlot slot) {
        return new File(SAVE_DIRECTORY, slot.name().toLowerCase() + FILE_EXTENSION);
    }
}