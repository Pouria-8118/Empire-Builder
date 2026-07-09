package civgame.util;

import civgame.model.unit.UnitType;

import java.awt.Color;

public class UnitColorProvider {

    private UnitColorProvider() {
    }

    public static Color getColorForUnitType(UnitType unitType) {
        if (unitType == UnitType.EXPLORER) {
            return new Color(80, 160, 240);
        }
        if (unitType == UnitType.BUILDER) {
            return new Color(240, 160, 40);
        }
        if (unitType == UnitType.WORKER) {
            return new Color(230, 230, 230);
        }
        return new Color(180, 60, 200);
    }

    // TODO  (bonus - visual polish): replace these colored circles with per-unit-type icons
}