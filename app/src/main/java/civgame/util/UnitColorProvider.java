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
        if (unitType == UnitType.SWORDSMAN) {
            return new Color(190, 40, 40);
        }
        if (unitType == UnitType.ARCHER) {
            return new Color(60, 150, 70);
        }
        if (unitType == UnitType.CAVALRY) {
            return new Color(120, 80, 30);
        }
        if (unitType == UnitType.CATAPULT) {
            return new Color(90, 90, 90);
        }
        if (unitType == UnitType.FISHING_BOAT) {
            return new Color(200, 170, 90);
        }
        if (unitType == UnitType.GALLEY) {
            return new Color(80, 60, 40);
        }
        return new Color(180, 60, 200);
    }

}