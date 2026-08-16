package civgame.model.game;

public class SeasonService {

    private static final int SEASON_LENGTH_TURNS = 10;
    private static final int SEASONS_PER_YEAR = 4;

    public SeasonType getCurrentSeason(int turnNumber) {
        int turnsIntoCycle = (turnNumber - 1) % (SEASON_LENGTH_TURNS * SEASONS_PER_YEAR);
        int seasonIndex = turnsIntoCycle / SEASON_LENGTH_TURNS;

        if (seasonIndex == 0) {
            return SeasonType.SPRING;
        }
        if (seasonIndex == 1) {
            return SeasonType.SUMMER;
        }
        if (seasonIndex == 2) {
            return SeasonType.AUTUMN;
        }
        return SeasonType.WINTER;
    }
}