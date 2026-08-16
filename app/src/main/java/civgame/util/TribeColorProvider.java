package civgame.util;

import civgame.model.tribe.TribeType;

import java.awt.Color;

public class TribeColorProvider {

    private TribeColorProvider() {
    }

    public static Color getColorForTribeType(TribeType tribeType) {
        if (tribeType == TribeType.FARMER) {
            return new Color(210, 180, 60);
        }
        if (tribeType == TribeType.WARRIOR) {
            return new Color(160, 30, 30);
        }
        if (tribeType == TribeType.TRADER) {
            return new Color(90, 160, 200);
        }
        if (tribeType == TribeType.MOUNTAIN) {
            return new Color(120, 100, 90);
        }
        return new Color(40, 130, 170);
    }

    public static String getLetter(TribeType tribeType) {
        if (tribeType == TribeType.FARMER) {
            return "F";
        }
        if (tribeType == TribeType.WARRIOR) {
            return "W";
        }
        if (tribeType == TribeType.TRADER) {
            return "T";
        }
        if (tribeType == TribeType.MOUNTAIN) {
            return "M";
        }
        return "C";
    }
}