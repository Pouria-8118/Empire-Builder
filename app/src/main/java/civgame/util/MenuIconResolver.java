package civgame.util;

import civgame.model.building.BuildingType;
import civgame.model.unit.UnitType;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Image;

public class MenuIconResolver {

    private static final int ICON_SIZE = 22;

    private MenuIconResolver() {
    }

    public static Icon resolveForUnit(UnitType unitType) {
        Image image = IconProvider.loadImage(UnitIconProvider.getIconPath(unitType));
        if (image != null) {
            return new ImageIcon(image.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
        }
        return new LetterIcon(UnitIconProvider.getFallbackLetter(unitType), UnitColorProvider.getColorForUnitType(unitType), ICON_SIZE);
    }

    public static Icon resolveForBuilding(BuildingType buildingType) {
        Image image = IconProvider.loadImage(BuildingIconProvider.getIconPath(buildingType));
        if (image != null) {
            return new ImageIcon(image.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
        }
        String letter = BuildingIconProvider.getFallbackLabel(buildingType).substring(0, 1);
        return new LetterIcon(letter, BuildingColorProvider.getColorForBuildingType(buildingType), ICON_SIZE);
    }
}