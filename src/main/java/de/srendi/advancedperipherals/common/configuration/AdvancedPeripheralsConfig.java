package de.srendi.advancedperipherals.common.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class AdvancedPeripheralsConfig {

    //Defaults

    public static String defaultChatBoxPrefix;

    //Restrictions
    public static int chatBoxCooldown;
    public static int playerDetMaxRange;
    public static int energyDetectorMaxFlow;
    public static int geoScannerMaxFreeRadius;
    public static int geoScannerMaxCostRadius;
    public static double geoScannerExtraBlockCost;
    public static int geoScannerMaxEnergyStored;
    public static int geoScannerMinScanPeriod;
    public static int environmentDetectorMaxEnergyStored;
    public static int environmentDetectorMaxFreeRadius;
    public static int environmentDetectorMaxCostRadius;
    public static double environmentDetectorExtraBlockCost;
    public static int environmentDetectorMinScanPeriod;
    public static int nbtStorageMaxSize;

    //Features
    public static boolean enableChatBox;
    public static boolean enableMeBridge;
    public static boolean enableRsBridge;
    public static boolean enablePlayerDetector;
    public static boolean enableEnvironmentDetector;
    public static boolean enableChunkyTurtle;
    public static boolean enableDebugMode;
    public static boolean enableEnergyDetector;
    public static boolean enableARGoggles;
    public static boolean enableInventoryManager;
    public static boolean enableRedstoneIntegrator;
    public static boolean enableBlockReader;
    public static boolean enableColonyIntegrator;
    public static boolean enableNBTStorage;
    public static boolean enablePoweredPeripherals;

    // automata cores configuration
    public static int energyToFuelRate;
    public static int suckItemCost;
    public static int suckItemCooldown;
    public static int digBlockCost;
    public static int digBlockCooldown;
    public static int clickBlockCost;
    public static int useOnBlockCooldown;
    public static int warpCooldown;
    public static int useOnAnimalCost;
    public static int useOnAnimalCooldown;
    public static int captureAnimalCost;
    public static int captureAnimalCooldown;
    public static boolean enableWeakAutomataCore;
    public static int weakAutomataCoreInteractionRadius;
    public static int weakAutomataCoreMaxFuelConsumptionLevel;
    public static boolean enableEndAutomataCore;
    public static int endAutomataCoreInteractionRadius;
    public static int endAutomataCoreMaxFuelConsumptionLevel;
    public static boolean enableHusbandryAutomataCore;
    public static int husbandryAutomataCoreInteractionRadius;
    public static int husbandryAutomataCoreMaxFuelConsumptionLevel;

    //World Features
    public static boolean enableVillagerStructures;
    public static boolean enableGeoScanner;

    public static class CommonConfig {

        //Defaults
        final ForgeConfigSpec.ConfigValue<String> DEFAULT_CHAT_BOX_PREFIX;

        //Restrictions
        final ForgeConfigSpec.IntValue CHAT_BOX_COOLDOWN;
        final ForgeConfigSpec.IntValue PLAYER_DET_MAX_RANGE;
        final ForgeConfigSpec.IntValue ENERGY_DETECTOR_MAX_FLOW;
        final ForgeConfigSpec.IntValue GEO_SCANNER_MAX_FREE_RADIUS;
        final ForgeConfigSpec.IntValue GEO_SCANNER_MAX_COST_RADIUS;
        final ForgeConfigSpec.DoubleValue GEO_SCANNER_EXTRA_BLOCK_COST;
        final ForgeConfigSpec.IntValue GEO_SCANNER_MAX_ENERGY_STORED;
        final ForgeConfigSpec.IntValue GEO_SCANNER_MIN_SCAN_PERIOD;
        final ForgeConfigSpec.IntValue ENVIRONMENT_DETECTOR_MAX_ENERGY_STORED;
        final ForgeConfigSpec.IntValue ENVIRONMENT_DETECTOR_MAX_FREE_RADIUS;
        final ForgeConfigSpec.IntValue ENVIRONMENT_DETECTOR_MAX_COST_RADIUS;
        final ForgeConfigSpec.DoubleValue ENVIRONMENT_DETECTOR_EXTRA_BLOCK_COST;
        final ForgeConfigSpec.IntValue ENVIRONMENT_DETECTOR_MIN_SCAN_PERIOD;
        final ForgeConfigSpec.IntValue NBT_STORAGE_MAX_SIZE;

        //Features
        final ForgeConfigSpec.BooleanValue ENABLE_CHAT_BOX;
        final ForgeConfigSpec.BooleanValue ENABLE_ME_BRIDGE;
        final ForgeConfigSpec.BooleanValue ENABLE_RS_BRIDGE;
        final ForgeConfigSpec.BooleanValue ENABLE_PLAYER_DETECTOR;
        final ForgeConfigSpec.BooleanValue ENABLE_ENVIRONMENT_DETECTOR;
        final ForgeConfigSpec.BooleanValue ENABLE_CHUNKY_TURTLE;
        final ForgeConfigSpec.BooleanValue ENABLE_DEBUG_MODE;
        final ForgeConfigSpec.BooleanValue ENABLE_ENERGY_DETECTOR;
        final ForgeConfigSpec.BooleanValue ENABLE_AR_GOGGLES;
        final ForgeConfigSpec.BooleanValue ENABLE_INVENTORY_MANAGER;
        final ForgeConfigSpec.BooleanValue ENABLE_REDSTONE_INTEGRATOR;
        final ForgeConfigSpec.BooleanValue ENABLE_BLOCK_READER;
        final ForgeConfigSpec.BooleanValue ENABLE_GEO_SCANNER;
        final ForgeConfigSpec.BooleanValue ENABLE_COLONY_INTEGRATOR;
        final ForgeConfigSpec.BooleanValue ENABLE_NBT_STORAGE;
        final ForgeConfigSpec.BooleanValue ENABLE_POWERED_PERIPHERALS;

        // Mechanical soul
        final ForgeConfigSpec.IntValue ENERGY_TO_FUEL_RATE;
        final ForgeConfigSpec.IntValue SUCK_ITEM_COST;
        final ForgeConfigSpec.IntValue SUCK_ITEM_COOLDOWN;
        final ForgeConfigSpec.IntValue DIG_BLOCK_COST;
        final ForgeConfigSpec.IntValue DIG_BLOCK_COOLDOWN;
        final ForgeConfigSpec.IntValue USE_ON_BLOCK_COST;
        final ForgeConfigSpec.IntValue USE_ON_BLOCK_COOLDOWN;
        final ForgeConfigSpec.IntValue WARP_COOLDOWN;
        final ForgeConfigSpec.IntValue USE_ON_ANIMAL_COST;
        final ForgeConfigSpec.IntValue USE_ON_ANIMAL_COOLDOWN;
        final ForgeConfigSpec.IntValue CAPTURE_ANIMAL_COST;
        final ForgeConfigSpec.IntValue CAPTURE_ANIMAL_COOLDOWN;
        final ForgeConfigSpec.IntValue WEAK_AUTOMATA_CORE_TURTLE_INTERACTION_RANGE;
        final ForgeConfigSpec.IntValue WEAK_AUTOMATA_CORE_TURTLE_MAX_FUEL_CONSUMPTION_LEVEL;
        final ForgeConfigSpec.IntValue END_AUTOMATA_CORE_TURTLE_INTERACTION_RANGE;
        final ForgeConfigSpec.IntValue END_AUTOMATA_CORE_TURTLE_MAX_FUEL_CONSUMPTION_LEVEL;
        final ForgeConfigSpec.IntValue HUSBANDRY_AUTOMATA_CORE_TURTLE_INTERACTION_RANGE;
        final ForgeConfigSpec.IntValue HUSBANDRY_AUTOMATA_CORE_TURTLE_MAX_FUEL_CONSUMPTION_LEVEL;
        final ForgeConfigSpec.BooleanValue ENABLE_WEAK_AUTOMATA_CORE;
        final ForgeConfigSpec.BooleanValue ENABLE_END_AUTOMATA_CORE;
        final ForgeConfigSpec.BooleanValue ENABLE_HUSBANDRY_AUTOMATA_CORE;


        //World Features
        final ForgeConfigSpec.BooleanValue ENABLE_VILLAGER_STRUCTURES;

        CommonConfig(final ForgeConfigSpec.Builder builder) {
            builder.comment("").push("Defaults");

            DEFAULT_CHAT_BOX_PREFIX = builder.comment("Defines default chatbox prefix").define("defaultChatBoxPrefix", "AP");

            builder.pop();
            builder.comment("").push("Restrictions");

            CHAT_BOX_COOLDOWN = builder.comment("Defines the chat box cooldown in seconds for message sending.").defineInRange("chatBoxCooldown", 10, 1, Integer.MAX_VALUE);
            PLAYER_DET_MAX_RANGE = builder.comment("The max range of the player detector functions. " +
                    "If anyone use a higher range, the detector will use this max range").defineInRange("playerDetMaxRange", 100000000, 0, 100000000);
            ENERGY_DETECTOR_MAX_FLOW = builder.comment("Defines the maximum energy flow of the energy detector.").defineInRange("energyDetectorMaxFlow", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
            GEO_SCANNER_MAX_FREE_RADIUS = builder.comment("Defines max cost-free radius for geo scanner").defineInRange("geoScannerMaxFreeRadius", 8, 1, 64);
            GEO_SCANNER_MAX_COST_RADIUS = builder.comment("Defines max cost radius for geo scanner").defineInRange("geoScannerMaxCostRadius", 16, 1, 64);
            GEO_SCANNER_EXTRA_BLOCK_COST = builder.comment("Defines block cost in RF for any extra block out of cost-free radius for geo scanner").defineInRange("geoScannerExtraBlockCost", 0.17, 0.17, 1000);
            GEO_SCANNER_MAX_ENERGY_STORED = builder.comment("Defines max energy stored in geo scanner").defineInRange("geoScannerMaxEnergyStored", 100_000_000, 1_000_000, Integer.MAX_VALUE);
            GEO_SCANNER_MIN_SCAN_PERIOD = builder.comment("Defines min period between scans in milliseconds for geo scanner").defineInRange("geoScannerMinScanPeriod", 2_000, 2_000, Integer.MAX_VALUE);
            ENVIRONMENT_DETECTOR_MAX_FREE_RADIUS = builder.comment("Defines max cost-free radius for environment detector").defineInRange("environmentDetectorMaxFreeRadius", 8, 1, 64);
            ENVIRONMENT_DETECTOR_MAX_COST_RADIUS = builder.comment("Defines max cost radius for environment detector").defineInRange("environmentDetectorMaxCostRadius", 16, 1, 64);
            ENVIRONMENT_DETECTOR_EXTRA_BLOCK_COST = builder.comment("Defines block cost in RF for any extra block out of cost-free radius for environment detector").defineInRange("environmentDetectorExtraBlockCost", 0.17, 0.17, 1000);
            ENVIRONMENT_DETECTOR_MAX_ENERGY_STORED = builder.comment("Defines max energy stored in environment detector").defineInRange("environmentDetectorMaxEnergyStored", 100_000_000, 1_000_000, Integer.MAX_VALUE);
            ENVIRONMENT_DETECTOR_MIN_SCAN_PERIOD = builder.comment("Defines min period between scans in milliseconds for environment detector").defineInRange("environmentDetectorMinScanPeriod", 2_000, 2_000, Integer.MAX_VALUE);
            NBT_STORAGE_MAX_SIZE = builder.comment("Defines max nbt string that can be stored in nbt storage").defineInRange("nbtStorageMaxSize", 1048576, 0, Integer.MAX_VALUE);

            builder.pop();

            builder.comment("").push("Features");

            ENABLE_CHAT_BOX = builder.comment("Enable the Chat Box or not.").define("enableChatBox", true);
            ENABLE_ME_BRIDGE = builder.comment("Enable the Me Bridge or not.").define("enableMeBridge", true);
            ENABLE_RS_BRIDGE = builder.comment("Enable the Rs Bridge or not.").define("enableRsBridge", true);
            ENABLE_PLAYER_DETECTOR = builder.comment("Enable the Player Detector or not.").define("enablePlayerDetector", true);
            ENABLE_ENVIRONMENT_DETECTOR = builder.comment("Enable the Environment Detector or not.").define("enableEnvironmentDetector", true);
            ENABLE_CHUNKY_TURTLE = builder.comment("Enable the Chunky Turtle or not.").define("enableChunkyTurtle", true);
            ENABLE_DEBUG_MODE = builder.comment("Enable the debug mode, you should only enable it, if a developer say it or something does not work.").define("enableDebugMode", false);
            ENABLE_ENERGY_DETECTOR = builder.comment("Enable the Energy Detector or not.").define("enableEnergyDetector", true);
            ENABLE_AR_GOGGLES = builder.comment("Enable the AR goggles or not.").define("enableARGoggles", true);
            ENABLE_INVENTORY_MANAGER = builder.comment("Enable the inventory manager or not.").define("enableInventoryManager", true);
            ENABLE_REDSTONE_INTEGRATOR = builder.comment("Enable the redstone integrator or not.").define("enableRedstoneIntegrator", true);
            ENABLE_BLOCK_READER = builder.comment("Enable the block reader or not.").define("enableBlockReader", true);
            ENABLE_GEO_SCANNER = builder.comment("Enable the geo scanner or not.").define("enableGeoScanner", true);
            ENABLE_COLONY_INTEGRATOR = builder.comment("Enable the colony integrator or not.").define("enableColonyIntegrator", true);
            ENABLE_NBT_STORAGE = builder.comment("Enable the nbt storage block or not").define("enableNBTStorage", true);
            ENABLE_POWERED_PERIPHERALS = builder.comment("Enable RF storage for peripherals, that could use it").define("enablePoweredPeripherals", true);

            builder.comment("").push("metaphysics");
            ENERGY_TO_FUEL_RATE = builder.comment("Defines energy to fuel rate").defineInRange("energyToFuelRate", 575, 575, Integer.MAX_VALUE);
            SUCK_ITEM_COST = builder.comment("Defines cost of suck single item").defineInRange("suckItemCost", 1, 1, Integer.MAX_VALUE);
            SUCK_ITEM_COOLDOWN = builder.comment("Defines cooldown of suck single item").defineInRange("suckItemCooldown", 1_000, 0, Integer.MAX_VALUE);
            DIG_BLOCK_COST = builder.comment("Defines cost of dig block action").defineInRange("digBlockCost", 1, 1, Integer.MAX_VALUE);
            DIG_BLOCK_COOLDOWN = builder.comment("Defines cooldown of dig block action").defineInRange("digBlockCooldown", 100, 0, Integer.MAX_VALUE);
            USE_ON_BLOCK_COST = builder.comment("Defines cost of use on block action").defineInRange("useOnBlockCost", 1, 1, Integer.MAX_VALUE);
            USE_ON_BLOCK_COOLDOWN = builder.comment("Defines cooldown of use on block action").defineInRange("useOnBlockCooldown", 5_000, 0, Integer.MAX_VALUE);
            WARP_COOLDOWN = builder.comment("Defines cooldown of warp action").defineInRange("warpCooldown", 1_000, 0, Integer.MAX_VALUE);
            USE_ON_ANIMAL_COST = builder.comment("Defines cost of use on animal action").defineInRange("useOnAnimalCost", 10, 1, Integer.MAX_VALUE);
            USE_ON_ANIMAL_COOLDOWN = builder.comment("Defines cooldown of use on animal action").defineInRange("useOnAnimalCooldown", 2_500, 0, Integer.MAX_VALUE);
            CAPTURE_ANIMAL_COST = builder.comment("Defines cost of capture animal action").defineInRange("captureAnimalCost", 100, 1, Integer.MAX_VALUE);
            CAPTURE_ANIMAL_COOLDOWN = builder.comment("Defines cooldown of capture animal action").defineInRange("captureAnimalCooldown", 50_000, 0, Integer.MAX_VALUE);
            ENABLE_WEAK_AUTOMATA_CORE = builder.define("enableWeakAutomataCore", true);
            WEAK_AUTOMATA_CORE_TURTLE_INTERACTION_RANGE = builder.defineInRange("weakAutomataCoreInteractionRange", 2, 1, 10);
            WEAK_AUTOMATA_CORE_TURTLE_MAX_FUEL_CONSUMPTION_LEVEL = builder.defineInRange("weakAutomataCoreMaxFuelConsumptionLevel", 2, 1, 10);
            ENABLE_END_AUTOMATA_CORE = builder.define("enableEndAutomataCore", true);
            END_AUTOMATA_CORE_TURTLE_INTERACTION_RANGE = builder.defineInRange("endAutomataCoreInteractionRange", 4, 1, 10);
            END_AUTOMATA_CORE_TURTLE_MAX_FUEL_CONSUMPTION_LEVEL = builder.defineInRange("endAutomataCoreMaxFuelConsumptionLevel", 4, 1, 10);
            ENABLE_HUSBANDRY_AUTOMATA_CORE = builder.define("enableHusbandryAutomataCore", true);
            HUSBANDRY_AUTOMATA_CORE_TURTLE_INTERACTION_RANGE = builder.defineInRange("husbandryAutomataCoreInteractionRange", 4, 1, 10);
            HUSBANDRY_AUTOMATA_CORE_TURTLE_MAX_FUEL_CONSUMPTION_LEVEL = builder.defineInRange("husbandryAutomataCoreMaxFuelConsumptionLevel", 4, 1, 10);
            builder.pop();

            builder.comment("").push("world");

            ENABLE_VILLAGER_STRUCTURES = builder.comment("Enable the villager structures for the computer scientist.").define("enableVillagerStructures", true);
            builder.pop();
        }
    }
}