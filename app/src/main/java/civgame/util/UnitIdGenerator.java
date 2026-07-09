package civgame.util;

public class UnitIdGenerator {

    private static int counter = 0;

    private UnitIdGenerator() {
    }

    public static String generateNextId(String prefix) {
        counter = counter + 1;
        return prefix + "-" + counter;
    }
}