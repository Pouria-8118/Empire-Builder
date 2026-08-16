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

    public static final int HEX_SIZE = 80;
    public static final double[] ZOOM_LEVELS = {0.5, 0.75, 1.0, 1.25, 1.5, 2.0};
    public static final int DEFAULT_ZOOM_INDEX = 2;
    public static final int DEFAULT_MAP_RADIUS = 14;

    public static final int STARTING_FOOD = 70;
    public static final int STARTING_WOOD = 60;
    public static final int STARTING_STONE = 20;
    public static final int STARTING_IRON = 0;

    public static final int INITIAL_FOOD_CAPACITY = 200;
    public static final int INITIAL_WOOD_CAPACITY = 200;
    public static final int INITIAL_STONE_CAPACITY = 150;
    public static final int INITIAL_IRON_CAPACITY = 100;

    public static final int INITIAL_UNIT_CAP = 12;

    public static final int EXPLORER_MAX_ACTION_POINTS = 5;
    public static final int BUILDER_MAX_ACTION_POINTS = 4;
    public static final int WORKER_MAX_ACTION_POINTS = 3;
    public static final int BORDER_EXPANDER_MAX_ACTION_POINTS = 4;

    public static final int EXPLORER_VISION_RADIUS = 3;
    public static final int BUILDER_VISION_RADIUS = 1;
    public static final int WORKER_VISION_RADIUS = 1;
    public static final int BORDER_EXPANDER_VISION_RADIUS = 1;
    public static final int TOWN_HALL_VISION_RADIUS = 2;
    public static final int BUILDING_VISION_RADIUS = 1;

    public static final int SWORDSMAN_MAX_ACTION_POINTS = 2;
    public static final int SWORDSMAN_VISION_RADIUS = 1;
    public static final int SWORDSMAN_MAX_HIT_POINTS = 1;
    public static final int SWORDSMAN_ATTACK_RANGE = 1;
    public static final int SWORDSMAN_STRUCTURE_ATTACK_POWER = 10;

    public static final int ARCHER_MAX_ACTION_POINTS = 2;
    public static final int ARCHER_VISION_RADIUS = 2;
    public static final int ARCHER_MAX_HIT_POINTS = 1;
    public static final int ARCHER_ATTACK_RANGE = 2;
    public static final int ARCHER_STRUCTURE_ATTACK_POWER = 6;

    public static final int CAVALRY_MAX_ACTION_POINTS = 4;
    public static final int CAVALRY_VISION_RADIUS = 2;
    public static final int CAVALRY_MAX_HIT_POINTS = 2;
    public static final int CAVALRY_ATTACK_RANGE = 1;
    public static final int CAVALRY_STRUCTURE_ATTACK_POWER = 8;

    public static final int BUILDER_STARTING_CHARGES = 3;

    public static final int STARTING_BUILDER_COUNT = 1;
    public static final int STARTING_WORKER_COUNT = 2;
    public static final int STARTING_EXPLORER_COUNT = 2;

    public static final int LUMBER_MILL_WOOD_COST = 12;
    public static final int STONE_MINE_WOOD_COST = 18;
    public static final int IRON_MINE_WOOD_COST = 20;
    public static final int FARM_WOOD_COST = 10;
    public static final int STABLE_WOOD_COST = 15;
    public static final int DOCK_WOOD_COST = 25;
    public static final int MONUMENT_WOOD_COST = 30;

    public static final int MONUMENT_STONE_COST = 40;

    public static final int LUMBER_MILL_CONSTRUCTION_AP_COST = 1;
    public static final int STONE_MINE_CONSTRUCTION_AP_COST = 2;
    public static final int IRON_MINE_CONSTRUCTION_AP_COST = 2;
    public static final int FARM_CONSTRUCTION_AP_COST = 1;
    public static final int STABLE_CONSTRUCTION_AP_COST = 2;
    public static final int DOCK_CONSTRUCTION_AP_COST = 2;
    public static final int MONUMENT_CONSTRUCTION_AP_COST = 2;

    public static final int LUMBER_MILL_WORKER_CAPACITY = 3;
    public static final int STONE_MINE_WORKER_CAPACITY = 3;
    public static final int IRON_MINE_WORKER_CAPACITY = 2;
    public static final int FARM_WORKER_CAPACITY = 3;
    public static final int STABLE_WORKER_CAPACITY = 2;
    public static final int DOCK_WORKER_CAPACITY = 2;
    public static final int MONUMENT_WORKER_CAPACITY = 0;

    public static final int LUMBER_MILL_PRODUCTION_PER_WORKER = 10;
    public static final int STONE_MINE_PRODUCTION_PER_WORKER = 2;
    public static final int IRON_MINE_PRODUCTION_PER_WORKER = 1;
    public static final int FARM_PRODUCTION_PER_WORKER = 3;
    public static final int STABLE_PRODUCTION_PER_WORKER = 2;
    public static final int DOCK_PRODUCTION_PER_WORKER = 3;

    public static final int LUMBER_MILL_MAX_HIT_POINTS = 40;
    public static final int STONE_MINE_MAX_HIT_POINTS = 50;
    public static final int IRON_MINE_MAX_HIT_POINTS = 50;
    public static final int FARM_MAX_HIT_POINTS = 40;
    public static final int STABLE_MAX_HIT_POINTS = 60;
    public static final int DEFAULT_BUILDING_MAX_HIT_POINTS = 40;

    public static final int ROAD_CONSTRUCTION_WOOD_COST = 15;
    public static final int ROAD_CONSTRUCTION_AP_COST = 1;
    public static final int ROAD_MOVEMENT_COST = 1;
    public static final int RIVER_CROSSING_EXTRA_COST = 1;
    public static final int RIVER_CROSSING_COST_WITH_ROAD = 0;

    public static final int WALL_MAX_HIT_POINTS = 80;
    public static final int WALL_STONE_COST = 40;
    public static final int WALL_WOOD_COST = 20;
    public static final int WALL_CONSTRUCTION_AP_COST = 1;
    public static final int WALL_UPKEEP_WOOD_AMOUNT = 1;
    public static final int WALL_DEFENSE_DICE_BONUS = 2;

    public static final int DEMOLITION_AP_COST = 1;

    public static final int DOCK_MAX_HIT_POINTS = 45;
    public static final int MONUMENT_MAX_HIT_POINTS = 30;

    public static final int FARM_SYNERGY_BONUS_PER_PAIR = 1;
    public static final int COASTAL_LUMBER_MILL_BONUS = 2;
    public static final int DEEP_MINE_MIN_MOUNTAIN_NEIGHBORS = 2;
    public static final int DEEP_MINE_BONUS = 1;

    public static final int MONUMENT_HAPPINESS_BONUS = 2;
    public static final int TOWN_HALL_GARRISON_HAPPINESS_BONUS = 1;
    public static final int NEW_SETTLEMENT_HAPPINESS_PENALTY = -1;

    public static final int GOLDEN_AGE_MIN_HAPPINESS = 3;
    public static final int DISCONTENT_MAX_HAPPINESS = -3;
    public static final int REBELLION_MAX_HAPPINESS = -5;
    public static final double GOLDEN_AGE_PRODUCTION_MULTIPLIER = 1.1;
    public static final int WORKER_STRIKE_PRODUCTION_PENALTY = 1;
    public static final int REBELLION_ACTION_POINT_PENALTY = 1;

    public static final int WORKER_STATION_AP_COST = 1;

    public static final int BUILDING_UPKEEP_WOOD_AMOUNT = 1;
    public static final int MAX_MISSED_UPKEEP_TURNS_BEFORE_COLLAPSE = 3;

    public static final int UNIT_FOOD_CONSUMPTION_PER_TURN = 1;
    public static final int STARVATION_ACTION_POINT_PENALTY = 1;
    public static final int TOWN_HALL_SAFEGUARD_FOOD = 2;
    public static final int TOWN_HALL_SAFEGUARD_WOOD = 1;

    public static final int BORDER_EXPAND_ACTION_POINT_COST = 1;

    public static final int STORAGE_UPGRADE_WOOD_COST = 20;
    public static final int STORAGE_UPGRADE_STONE_COST = 30;
    public static final int STORAGE_UPGRADE_TURNS = 3;
    public static final int STORAGE_UPGRADE_CAPACITY_BONUS = 100;
    public static final int STORAGE_UPGRADE_MAX_COUNT = 2;

    public static final int STONE_MINE_TECH_WOOD_COST = 25;
    public static final int STONE_MINE_TECH_STONE_COST = 15;
    public static final int STONE_MINE_TECH_TURNS = 3;

    public static final int IRON_MINE_TECH_WOOD_COST = 30;
    public static final int IRON_MINE_TECH_STONE_COST = 20;
    public static final int IRON_MINE_TECH_TURNS = 3;

    public static final int PROFESSIONAL_TOOLS_TECH_STONE_COST = 0;
    public static final int PROFESSIONAL_TOOLS_TECH_IRON_COST = 40;
    public static final int PROFESSIONAL_TOOLS_TECH_TURNS = 3;
    public static final double PROFESSIONAL_TOOLS_PRODUCTION_MULTIPLIER = 1.5;

    public static final int SETTLEMENT_TECH_WOOD_COST = 40;
    public static final int SETTLEMENT_TECH_STONE_COST = 30;
    public static final int SETTLEMENT_TECH_IRON_COST = 15;
    public static final int SETTLEMENT_TECH_TURNS = 4;
    public static final int SETTLEMENT_UNIT_CAP_BONUS = 5;

    public static final int TOWN_HALL_BASE_HIT_POINTS = 200;

    public static final int TOWN_HALL_LEVEL_2_WOOD_COST = 50;
    public static final int TOWN_HALL_LEVEL_2_STONE_COST = 50;
    public static final int TOWN_HALL_LEVEL_2_TURNS = 3;
    public static final int TOWN_HALL_LEVEL_2_STORAGE_CAPACITY_BONUS = 100;
    public static final int TOWN_HALL_LEVEL_2_HEAL_AMOUNT = 50;

    public static final int TOWN_HALL_LEVEL_3_STONE_COST = 100;
    public static final int TOWN_HALL_LEVEL_3_IRON_COST = 50;
    public static final int TOWN_HALL_LEVEL_3_TURNS = 5;
    public static final int TOWN_HALL_LEVEL_3_STORAGE_CAPACITY_BONUS = 150;

    public static final int TOWN_HALL_BASE_DEFENSE = 10;

    public static final int NAVAL_TECHNIQUES_WOOD_COST = 80;
    public static final int NAVAL_TECHNIQUES_TURNS = 4;

    public static final int DEFENSIVE_ARCHITECTURE_STONE_COST = 100;
    public static final int DEFENSIVE_ARCHITECTURE_TURNS = 4;
    public static final int DEFENSIVE_ARCHITECTURE_DEFENSE_VALUE = 30;
    public static final int DEFENSIVE_ARCHITECTURE_MAX_HIT_POINTS = 350;

    public static final int EXPLORER_PRODUCTION_WOOD_COST = 10;
    public static final int EXPLORER_PRODUCTION_FOOD_COST = 5;
    public static final int EXPLORER_PRODUCTION_TURNS = 2;

    public static final int BUILDER_PRODUCTION_WOOD_COST = 15;
    public static final int BUILDER_PRODUCTION_FOOD_COST = 5;
    public static final int BUILDER_PRODUCTION_TURNS = 3;

    public static final int WORKER_PRODUCTION_WOOD_COST = 8;
    public static final int WORKER_PRODUCTION_FOOD_COST = 4;
    public static final int WORKER_PRODUCTION_TURNS = 1;

    public static final int BORDER_EXPANDER_PRODUCTION_WOOD_COST = 15;
    public static final int BORDER_EXPANDER_PRODUCTION_FOOD_COST = 8;
    public static final int BORDER_EXPANDER_PRODUCTION_TURNS = 2;

    public static final int SWORDSMAN_PRODUCTION_WOOD_COST = 10;
    public static final int SWORDSMAN_PRODUCTION_FOOD_COST = 20;
    public static final int SWORDSMAN_PRODUCTION_TURNS = 2;

    public static final int ARCHER_PRODUCTION_WOOD_COST = 15;
    public static final int ARCHER_PRODUCTION_FOOD_COST = 15;
    public static final int ARCHER_PRODUCTION_TURNS = 2;

    public static final int CAVALRY_PRODUCTION_WOOD_COST = 20;
    public static final int CAVALRY_PRODUCTION_FOOD_COST = 25;
    public static final int CAVALRY_PRODUCTION_TURNS = 3;

    public static final int MILITARY_UNIT_CAP_LEVEL_1 = 5;
    public static final int MILITARY_UNIT_CAP_LEVEL_2 = 10;
    public static final int MILITARY_UNIT_CAP_LEVEL_3 = 15;
    public static final int UNIT_CAP_REACHED_HAPPINESS_PENALTY = -1;

    public static final int HEX_MAX_SWORDSMEN = 2;
    public static final int HEX_MAX_ARCHERS = 2;
    public static final int HEX_MAX_CAVALRY = 1;
    public static final int ATTACK_ACTION_POINT_COST = 1;
    public static final int WILD_ANIMAL_DEFENSE_DICE_COUNT = 2;

    public static final int CATAPULT_MAX_ACTION_POINTS = 2;
    public static final int CATAPULT_VISION_RADIUS = 2;
    public static final int CATAPULT_MAX_HIT_POINTS = 50;
    public static final int CATAPULT_ATTACK_RANGE = 2;
    public static final int CATAPULT_STRUCTURE_ATTACK_POWER = 20;
    public static final int CATAPULT_PRODUCTION_WOOD_COST = 40;
    public static final int CATAPULT_PRODUCTION_FOOD_COST = 10;
    public static final int CATAPULT_PRODUCTION_TURNS = 4;
    public static final int HEX_MAX_CATAPULTS = 1;

    public static final int BAZAAR_WOOD_COST = 30;
    public static final int BAZAAR_CONSTRUCTION_AP_COST = 2;
    public static final int BAZAAR_WORKER_CAPACITY = 0;
    public static final int BAZAAR_MAX_HIT_POINTS = 40;

    public static final int BAZAAR_TIER_1_AMOUNT = 10;
    public static final double BAZAAR_TIER_1_RATE = 0.5;
    public static final int BAZAAR_TIER_2_AMOUNT = 100;
    public static final double BAZAAR_TIER_2_RATE = 0.6;
    public static final int BAZAAR_TIER_3_AMOUNT = 500;
    public static final double BAZAAR_TIER_3_RATE = 0.7;

    public static final int RESOURCE_TILE_CAPACITY = 400;

    public static final String EXPLORER_ICON_PATH = "/images/units/explorer.png";
    public static final String BUILDER_ICON_PATH = "/images/units/builder.png";
    public static final String WORKER_ICON_PATH = "/images/units/worker.png";
    public static final String BORDER_EXPANDER_ICON_PATH = "/images/units/border_expander.png";
    public static final String SWORDSMAN_ICON_PATH = "/images/units/swordsman.png";
    public static final String ARCHER_ICON_PATH = "/images/units/archer.png";
    public static final String CAVALRY_ICON_PATH = "/images/units/cavalry.png";
    public static final String CATAPULT_ICON_PATH = "/images/units/catapult.png";
    public static final String BAZAAR_ICON_PATH = "/images/buildings/bazaar.png";

    public static final double TRADING_POST_RATE = 0.8;

    public static final int TRIBE_ENEMY_MAX_RELATIONSHIP = -50;
    public static final int TRIBE_DISPLEASED_MAX_RELATIONSHIP = -20;
    public static final int TRIBE_NEUTRAL_MAX_RELATIONSHIP = 19;
    public static final int TRIBE_FRIENDLY_MAX_RELATIONSHIP = 69;
    public static final int TRIBE_ALLIANCE_MIN_RELATIONSHIP = 70;

    public static final int TRIBE_CAMP_FARMER_MAX_HIT_POINTS = 40;
    public static final int TRIBE_CAMP_WARRIOR_MAX_HIT_POINTS = 70;
    public static final int TRIBE_CAMP_DEFAULT_MAX_HIT_POINTS = 50;

    public static final int TRIBE_GIFT_FOOD_WOOD_RELATIONSHIP_PER_UNIT = 2;
    public static final int TRIBE_GIFT_STONE_RELATIONSHIP_PER_UNIT = 3;
    public static final int TRIBE_GIFT_IRON_RELATIONSHIP_PER_UNIT = 3;

    public static final int TRIBE_TRADE_MIN_RELATIONSHIP = 20;
    public static final double FARMER_TRIBE_TRADE_RATE = 0.75;
    public static final double MOUNTAIN_TRIBE_TRADE_RATE = 0.75;
    public static final double COASTAL_TRIBE_TRADE_RATE = 0.75;
    public static final double TRADER_TRIBE_TRADE_RATE = 0.80;

    public static final int TRIBE_WAR_ON_ALLY_HAPPINESS_PENALTY = -15;
    public static final int TRIBE_WAR_ON_FRIENDLY_HAPPINESS_PENALTY = -5;
    public static final int TRIBE_PEACE_FOOD_COST = 30;
    public static final int TRIBE_PEACE_WOOD_COST = 30;
    public static final int TRIBE_PEACE_IRON_COST = 30;
    public static final int TRIBE_PEACE_RESULTING_RELATIONSHIP = -10;

    public static final int TRIBE_MISSION_MIN_RELATIONSHIP = 20;
    public static final int MISSION_CANCEL_RELATIONSHIP_PENALTY = -5;
    public static final int MISSION_FAILURE_RELATIONSHIP_PENALTY = -10;
    public static final int MISSION_FAILURE_COOLDOWN_TURNS = 5;

    public static final int FARMER_MISSION_WOOD_COST = 20;
    public static final int FARMER_MISSION_STONE_COST = 10;
    public static final int FARMER_MISSION_DEADLINE_TURNS = 5;
    public static final int FARMER_MISSION_REWARD_FOOD = 30;
    public static final int FARMER_MISSION_REWARD_RELATIONSHIP = 15;

    public static final int TRADER_MISSION_DEADLINE_TURNS = 10;
    public static final double TRADER_MISSION_TRADE_RATE_BONUS = 0.1;
    public static final int TRADER_MISSION_REWARD_RELATIONSHIP = 20;

    public static final int WARRIOR_MISSION_DEADLINE_TURNS = 8;
    public static final int WARRIOR_MISSION_REQUIRED_KILLS = 2;
    public static final int WARRIOR_MISSION_RADIUS = 5;
    public static final int WARRIOR_MISSION_REWARD_SWORDSMEN_COUNT = 3;
    public static final int WARRIOR_MISSION_REWARD_RELATIONSHIP = 20;

    public static final int MOUNTAIN_MISSION_WOOD_COST = 15;
    public static final int MOUNTAIN_MISSION_IRON_COST = 10;
    public static final int MOUNTAIN_MISSION_DEADLINE_TURNS = 6;
    public static final int MOUNTAIN_MISSION_REWARD_STONE = 20;
    public static final int MOUNTAIN_MISSION_REWARD_RELATIONSHIP = 15;

    public static final int COASTAL_MISSION_DEADLINE_TURNS = 10;
    public static final int COASTAL_MISSION_DOCK_RADIUS = 4;
    public static final int COASTAL_MISSION_REWARD_FOOD = 30;
    public static final int COASTAL_MISSION_DOCK_DISCOUNT = 10;

    public static final int TRIBE_GUARD_SWORDSMAN_HIT_POINTS = 1;
    public static final int TRIBE_GUARD_ARCHER_HIT_POINTS = 1;
    public static final int TRIBE_DEFEAT_LOOT_AMOUNT = 40;

    public static final int TRIBE_ZONE_INTRUSION_NEUTRAL_PENALTY = -5;
    public static final int TRIBE_ZONE_INTRUSION_DISPLEASED_PENALTY = -8;
    public static final int TRIBE_MISSION_OFFER_INTERVAL_TURNS = 5;
    public static final int TRIBE_ENEMY_GUARD_SPAWN_INTERVAL_TURNS = 3;
    public static final int TRIBE_ENEMY_DEFAULT_GUARD_CAP = 3;
    public static final int TRIBE_ENEMY_WARRIOR_GUARD_CAP = 5;

    public static final int FARMER_ALLY_FOOD_BONUS_PER_TURN = 2;
    public static final int MOUNTAIN_ALLY_STONE_BONUS_PER_TURN = 1;
    public static final int MOUNTAIN_ALLY_IRON_BONUS_PER_TURN = 1;
    public static final double TRADER_ALLY_TRADE_RATE_BONUS = 0.1;
    public static final int COASTAL_ALLY_DOCK_DISCOUNT = 10;

    public static final int SPRING_FARM_STABLE_BONUS = 1;
    public static final int WINTER_FARM_PENALTY = 1;

    public static final String FLOATING_UNIT_ICON_PATH = "/images/units/floating_unit.png";
    public static final String TOWN_HALL_GAME_ICON_PATH = "/images/buildings/town_hall.png";
    public static final String LUMBER_MILL_ICON_PATH = "/images/buildings/lumber_mill.png";
    public static final String STONE_MINE_ICON_PATH = "/images/buildings/stone_mine.png";
    public static final String IRON_MINE_ICON_PATH = "/images/buildings/iron_mine.png";
    public static final String FARM_ICON_PATH = "/images/buildings/farm.png";
    public static final String STABLE_ICON_PATH = "/images/buildings/stable.png";
    public static final String DOCK_ICON_PATH = "/images/buildings/dock.png";
    public static final String MONUMENT_ICON_PATH = "/images/buildings/monument.png";
    public static final String SETTLEMENT_ICON_PATH = "/images/buildings/settlement.png";

    public static final String FOOD_ICON_PATH = "/images/resources/food.png";
    public static final String WOOD_ICON_PATH = "/images/resources/wood.png";
    public static final String STONE_ICON_PATH = "/images/resources/stone.png";
    public static final String IRON_ICON_PATH = "/images/resources/iron.png";
    public static final String WHEAT_ICON_PATH = "/images/resources/wheat.png";
    public static final String RICE_ICON_PATH = "/images/resources/rice.png";
    public static final String CATTLE_ICON_PATH = "/images/resources/cattle.png";
    public static final String SHEEP_ICON_PATH = "/images/resources/sheep.png";
    public static final String FISH_ICON_PATH = "/images/resources/fish.png";

    private Constants() {
    }
}