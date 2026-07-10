package civgame.util;

public class Constants {

    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    public static final String WINDOW_TITLE = "Empire Builder";

    public static final String MENU_BACKGROUND_IMAGE_PATH = "/images/menu/menu_background.jpg";
    public static final String APP_ICON_PATH = "/images/menu/app_icon.png";
    public static final String START_ICON_PATH = "/images/menu/start_icon.png";
    public static final String SETTINGS_ICON_PATH = "/images/menu/settings_icon.png";
    public static final String EXIT_ICON_PATH = "/images/menu/exit_icon.png";

    public static final String BACKGROUND_MUSIC_PATH = "/audio/background_music.wav";

    public static final int DEFAULT_VOLUME_PERCENT = 70;

    public static final int HEX_SIZE = 40;
    public static final double[] ZOOM_LEVELS = {0.5, 0.75, 1.0, 1.25, 1.5, 2.0};
    public static final int DEFAULT_ZOOM_INDEX = 2;
    public static final int DEFAULT_MAP_RADIUS = 10;

    public static final int STARTING_FOOD = 50;
    public static final int STARTING_WOOD = 40;
    public static final int STARTING_STONE = 20;
    public static final int STARTING_IRON = 0;

    public static final int INITIAL_FOOD_CAPACITY = 200;
    public static final int INITIAL_WOOD_CAPACITY = 200;
    public static final int INITIAL_STONE_CAPACITY = 150;
    public static final int INITIAL_IRON_CAPACITY = 100;

    public static final int INITIAL_UNIT_CAP = 10;

    public static final int EXPLORER_MAX_ACTION_POINTS = 6;
    public static final int BUILDER_MAX_ACTION_POINTS = 4;
    public static final int WORKER_MAX_ACTION_POINTS = 3;
    public static final int BORDER_EXPANDER_MAX_ACTION_POINTS = 3;

    public static final int EXPLORER_VISION_RADIUS = 3;
    public static final int BUILDER_VISION_RADIUS = 1;
    public static final int WORKER_VISION_RADIUS = 1;
    public static final int BORDER_EXPANDER_VISION_RADIUS = 1;
    public static final int TOWN_HALL_VISION_RADIUS = 2;

    public static final int BUILDER_STARTING_CHARGES = 3;

    public static final int STARTING_BUILDER_COUNT = 1;
    public static final int STARTING_WORKER_COUNT = 2;
    public static final int STARTING_EXPLORER_COUNT = 2;

    public static final int LUMBER_MILL_WOOD_COST = 15;
    public static final int STONE_MINE_WOOD_COST = 20;
    public static final int IRON_MINE_WOOD_COST = 25;
    public static final int FARM_WOOD_COST = 15;
    public static final int STABLE_WOOD_COST = 20;

    public static final int LUMBER_MILL_CONSTRUCTION_AP_COST = 1;
    public static final int STONE_MINE_CONSTRUCTION_AP_COST = 2;
    public static final int IRON_MINE_CONSTRUCTION_AP_COST = 2;
    public static final int FARM_CONSTRUCTION_AP_COST = 1;
    public static final int STABLE_CONSTRUCTION_AP_COST = 2;

    public static final int LUMBER_MILL_WORKER_CAPACITY = 3;
    public static final int STONE_MINE_WORKER_CAPACITY = 3;
    public static final int IRON_MINE_WORKER_CAPACITY = 2;
    public static final int FARM_WORKER_CAPACITY = 3;
    public static final int STABLE_WORKER_CAPACITY = 2;

    public static final int LUMBER_MILL_PRODUCTION_PER_WORKER = 2;
    public static final int STONE_MINE_PRODUCTION_PER_WORKER = 2;
    public static final int IRON_MINE_PRODUCTION_PER_WORKER = 1;
    public static final int FARM_PRODUCTION_PER_WORKER = 3;
    public static final int STABLE_PRODUCTION_PER_WORKER = 2;

    public static final int WORKER_STATION_AP_COST = 1;

    public static final int BUILDING_UPKEEP_WOOD_AMOUNT = 1;
    public static final int MAX_MISSED_UPKEEP_TURNS_BEFORE_COLLAPSE = 3;

    // TODO : add Town Hall upgrade costs and tech-unlock constants here

    private Constants() {
    }
}