package civgame.util;

public class BuildingIdGenerator {

    private static int counter = 0;

    private BuildingIdGenerator() {
    }

    public static String generateNextId(String prefix) {
        counter = counter + 1;
        return prefix + "-" + counter;
    }
}