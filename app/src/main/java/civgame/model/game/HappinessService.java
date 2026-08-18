package civgame.model.game;

import civgame.model.building.Building;
import civgame.model.building.BuildingType;
import civgame.model.map.HexCoordinate;
import civgame.model.unit.MilitaryUnitCapProvider;
import civgame.model.unit.Unit;
import civgame.util.Constants;

import java.util.List;

public class HappinessService implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int happinessValue;

    public HappinessService() {
        this.happinessValue = 0;
    }

    public int getHappinessValue() {
        return happinessValue;
    }

    public HappinessLevel getCurrentLevel() {
        if (happinessValue >= Constants.GOLDEN_AGE_MIN_HAPPINESS) {
            return HappinessLevel.GOLDEN_AGE;
        }
        if (happinessValue <= Constants.REBELLION_MAX_HAPPINESS) {
            return HappinessLevel.REBELLION;
        }
        if (happinessValue <= Constants.DISCONTENT_MAX_HAPPINESS) {
            return HappinessLevel.DISCONTENT;
        }
        return HappinessLevel.NORMAL;
    }

    public void applyOneTimeDelta(int delta) {
        happinessValue = happinessValue + delta;
    }

    public void applyPerTurnContributions(GameSession gameSession) {
        int activeMonumentCount = countActiveMonuments(gameSession);
        happinessValue = happinessValue + (activeMonumentCount * Constants.MONUMENT_HAPPINESS_BONUS);

        if (isMilitaryUnitGarrisonedAtTownHall(gameSession)) {
            happinessValue = happinessValue + Constants.TOWN_HALL_GARRISON_HAPPINESS_BONUS;
        }
    }

    private int countActiveMonuments(GameSession gameSession) {
        List<Building> allBuildings = gameSession.getAllBuildings();
        int count = 0;

        for (int i = 0; i < allBuildings.size(); i++) {
            Building building = allBuildings.get(i);
            if (!building.isDestroyed() && building.getBuildingType() == BuildingType.MONUMENT) {
                count = count + 1;
            }
        }

        return count;
    }

    private boolean isMilitaryUnitGarrisonedAtTownHall(GameSession gameSession) {
        HexCoordinate townHallCoordinate = gameSession.getTownHall().getCoordinate();
        List<Unit> units = gameSession.getUnits();

        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (unit.getCoordinate().equals(townHallCoordinate) && MilitaryUnitCapProvider.isMilitaryUnitType(unit.getUnitType())) {
                return true;
            }
        }

        return false;
    }
}