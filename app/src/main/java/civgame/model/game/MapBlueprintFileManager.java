package civgame.model.game;

import civgame.model.map.MapBlueprint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MapBlueprintFileManager {

    private static final String MAP_DIRECTORY = "maps";
    private static final String FILE_EXTENSION = ".map";
    private static final String TEMP_EXTENSION = ".tmp";

    public MapBlueprintFileManager() {
        File directory = new File(MAP_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public SaveLoadResult save(String mapName, MapBlueprint blueprint) {
        File targetFile = new File(MAP_DIRECTORY, mapName + FILE_EXTENSION);
        File tempFile = new File(targetFile.getPath() + TEMP_EXTENSION);

        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(tempFile));
            outputStream.writeObject(blueprint);
            outputStream.close();
        } catch (IOException exception) {
            return SaveLoadResult.failure("Could not write map file: " + exception.getMessage());
        }

        if (targetFile.exists()) {
            targetFile.delete();
        }
        boolean replaced = tempFile.renameTo(targetFile);
        if (!replaced) {
            return SaveLoadResult.failure("Could not finalize the map file on disk.");
        }

        return SaveLoadResult.success("Saved map: " + mapName);
    }

    public MapBlueprint load(String mapName) {
        File file = new File(MAP_DIRECTORY, mapName + FILE_EXTENSION);
        if (!file.exists()) {
            return null;
        }

        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            Object loadedObject = inputStream.readObject();
            inputStream.close();
            return (MapBlueprint) loadedObject;
        } catch (Exception exception) {
            return null;
        }
    }

    public String[] listMapNames() {
        File directory = new File(MAP_DIRECTORY);
        File[] files = directory.listFiles();
        if (files == null) {
            return new String[0];
        }

        List<String> names = new ArrayList<String>();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            if (fileName.endsWith(FILE_EXTENSION)) {
                names.add(fileName.substring(0, fileName.length() - FILE_EXTENSION.length()));
            }
        }
        return names.toArray(new String[0]);
    }
}