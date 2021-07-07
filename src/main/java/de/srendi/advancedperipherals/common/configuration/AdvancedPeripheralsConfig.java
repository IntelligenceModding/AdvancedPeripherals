package de.srendi.advancedperipherals.common.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class AdvancedPeripheralsConfig {

    //Restrictions
    public static int chatBoxCooldown;
    public static int playerDetMaxRange;
    public static int energyDetectorMaxFlow;
    public static int geoScannerMaxFreeRadius;
    public static int geoScannerMaxCostRadius;
    public static double geoScannerExtraBlockCost;
    public static int geoScannerMaxEnergyStorage;
    public static int geoScannerMinScanPeriod;
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

    // mechanic soul configuration
    public static int energyToFuelRate;
    public static int suckItemCost;
    public static int suckItemCooldown;
    public static int digBlockCost;
    public static int digBlockCooldown;
    public static int clickBlockCost;
    public static int useOnBlockCooldown;
    public static int warpCooldown;
    public static boolean enableWeakMechanicSoul;
    public static int weakMechanicSoulSuckRange;
    public static int weakMechanicSoulMaxFuelConsumptionLevel;
    public static boolean enableEndMechanicSoul;
    public static int endMechanicSoulSuckRange;
    public static int endMechanicSoulMaxFueldConsumptionLevel;

    //World Features
    public static boolean enableVillagerStructures;
    public static boolean enableGeoScanner;

    public static class CommonConfig {

        //Restrictions
        final ForgeConfigSpec.IntValue CHAT_BOX_COOLDOWN;
        final ForgeConfigSpec.IntValue PLAYER_DET_MAX_RANGE;
        final ForgeConfigSpec.IntValue ENERGY_DETECTOR_MAX_FLOW;
        final ForgeConfigSpec.IntValue GEO_SCANNER_MAX_FREE_RADIUS;
        final ForgeConfigSpec.IntValue GEO_SCANNER_MAX_COST_RADIUS;
        final ForgeConfigSpec.DoubleValue GEO_SCANNER_EXTRA_BLOCK_COST;
        final ForgeConfigSpec.IntValue GEO_SCANNER_MAX_ENERGY_STORAGE;
        final ForgeConfigSpec.IntValue GEO_SCANNER_MIN_SCAN_PERIOD;
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

        // Mechanical soul
        final ForgeConfigSpec.IntValue ENERGY_TO_FUEL_RATE;
        final ForgeConfigSpec.IntValue SUCK_ITEM_COST;
        final ForgeConfigSpec.IntValue SUCK_ITEM_COOLDOWN;
        final ForgeConfigSpec.IntValue DIG_BLOCK_COST;
        final ForgeConfigSpec.IntValue DIG_BLOCK_COOLDOWN;
        final ForgeConfigSpec.IntValue USE_ON_BLOCK_COST;
        final ForgeConfigSpec.IntValue USE_ON_BLOCK_COOLDOWN;
        final ForgeConfigSpec.IntValue WARP_COOLDOWN;
        final ForgeConfigSpec.IntValue WEAK_MECHANIC_SOUL_TURTLE_SUCK_RANGE;
        final ForgeConfigSpec.IntValue WEAK_MECHANIC_SOUL_TURTLE_MAX_FUEL_CONSUMPTION_LEVEL;
        final ForgeConfigSpec.IntValue END_MECHANIC_SOUL_TURTLE_SUCK_RANGE;
        final ForgeConfigSpec.IntValue END_MECHANIC_SOUL_TURTLE_MAX_FUEL_CONSUMPTION_LEVEL;
        final ForgeConfigSpec.BooleanValue ENABLE_WEAK_MECHANIC_SOUL;
        final ForgeConfigSpec.BooleanValue ENABLE_END_MECHANIC_SOUL;


        //World Features
        final ForgeConfigSpec.BooleanValue ENABLE_VILLAGER_STRUCTURES;

        CommonConfig(final ForgeConfigSpec.Builder builder) {
            builder.comment("").push("Restrictions");

            CHAT_BOX_COOLDOWN = builder.comment("Defines the chat box cooldown for message sending.").defineInRange("chatBoxCooldown", 10, 1, Integer.MAX_VALUE);
            PLAYER_DET_MAX_RANGE = builder.comment("The max range of the player detector functions. " +
                    "If anyone use a higher range, the detector will use this max range").defineInRange("playerDetMaxRange", 100000000, 0, 100000000);
            ENERGY_DETECTOR_MAX_FLOW = builder.comment("Defines the maximum energy flow of the energy detector.").defineInRange("energyDetectorMaxFlow", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
            GEO_SCANNER_MAX_FREE_RADIUS = builder.comment("Defines max cost-free radius for geo scanner").defineInRange("geoScannerMaxFreeRadius", 8, 1, 64);
            GEO_SCANNER_MAX_COST_RADIUS = builder.comment("Defines max cost radius for geo scanner").defineInRange("geoScannerMaxCostRadius", 16, 1, 64);
            GEO_SCANNER_EXTRA_BLOCK_COST = builder.comment("Defines block cost in RF for any extra block out of cost-free radius").defineInRange("geoScannerExtraBlockCost", 0.17, 0.17, 1000);
            GEO_SCANNER_MAX_ENERGY_STORAGE = builder.comment("Defines max energy stored in geo scanner").defineInRange("geoScannerMaxEnergyStorage", 100_000_000, 1_000_000, Integer.MAX_VALUE);
            GEO_SCANNER_MIN_SCAN_PERIOD = builder.comment("Defines min period between scans in milliseconds").defineInRange("geoScannerMinScanPeriod", 2_000, 2_000, Integer.MAX_VALUE);
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

            builder.comment("").push("Mechanical soul");
            ENERGY_TO_FUEL_RATE = builder.comment("Defines energy to fuel rate").defineInRange("energyToFuelRate", 575, 575, Integer.MAX_VALUE);
            SUCK_ITEM_COST = builder.comment("Defines cost of suck single item").defineInRange("suckItemCost", 1, 1, Integer.MAX_VALUE);
            SUCK_ITEM_COOLDOWN = builder.comment("Defines cooldown of suck single item").defineInRange("suckItemCooldown", 1_000, 0, Integer.MAX_VALUE);
            DIG_BLOCK_COST = builder.comment("Defines cost of dig block action").defineInRange("digBlockCost", 1, 1, Integer.MAX_VALUE);
            DIG_BLOCK_COOLDOWN = builder.comment("Defines cooldown of dig block action").defineInRange("digBlockCooldown", 100, 0, Integer.MAX_VALUE);
            USE_ON_BLOCK_COST = builder.comment("Defines cost of use on  block action").defineInRange("useOnBlockCost", 1, 1, Integer.MAX_VALUE);
            USE_ON_BLOCK_COOLDOWN = builder.comment("Defines cooldown of use on block action").defineInRange("useOnBlockCooldown", 5_000, 0, Integer.MAX_VALUE);
            WARP_COOLDOWN = builder.comment("Defines cooldown of warp action").defineInRange("warpCooldown", 1_000, 0, Integer.MAX_VALUE);
            ENABLE_WEAK_MECHANIC_SOUL = builder.comment("Enable the weak mechanic soul or not").define("enableWeakMechanicSoul", true);
            WEAK_MECHANIC_SOUL_TURTLE_SUCK_RANGE = builder.comment("Defines weak mechanic soul turtle suck range").defineInRange("weakMechanicSoulSuckRange", 2, 1, 10);
            WEAK_MECHANIC_SOUL_TURTLE_MAX_FUEL_CONSUMPTION_LEVEL = builder.comment("Defines weak mechanic soul turtle max fuel consumption level").defineInRange("weakMechanicSoulMaxFuelConsumptionLevel", 2, 1, 10);
            ENABLE_END_MECHANIC_SOUL = builder.comment("Enable the end mechanic soul or not").define("enableEndMechanicSoul", true);
            END_MECHANIC_SOUL_TURTLE_SUCK_RANGE = builder.comment("Defines weak mechanic soul turtle suck range").defineInRange("endMechanicSoulSuckRange", 4, 1, 10);
            END_MECHANIC_SOUL_TURTLE_MAX_FUEL_CONSUMPTION_LEVEL = builder.comment("Defines end mechanic soul turtle max fuel consumption level").defineInRange("endMechanicSoulMaxFuelConsumptionLevel", 4, 1, 10);

            ENABLE_VILLAGER_STRUCTURES = builder.comment("Enable the villager structures for the computer scientist.").define("enableVillagerStructures", true);
            builder.pop();
        }
    }
}