package de.srendi.advancedperipherals.common.configuration;


import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCoreTier;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SimpleFreeOperation;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperation;
import de.srendi.advancedperipherals.lib.LibConfig;
import de.srendi.advancedperipherals.lib.misc.IConfigHandler;
import net.minecraftforge.common.ForgeConfigSpec;

public class AdvancedPeripheralsConfig {

    //Defaults

    public static String defaultChatBoxPrefix;

    //Restrictions
    public static int playerDetMaxRange;
    public static int energyDetectorMaxFlow;
    public static int poweredPeripheralMaxEnergyStored;
    public static int nbtStorageMaxSize;
    public static int chunkLoadValidTime;

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
    public static boolean enableGeoScanner;

    // automata cores configuration
    public static int energyToFuelRate;
    public static boolean enableWeakAutomataCore;
    public static boolean enableEndAutomataCore;
    public static boolean enableHusbandryAutomataCore;
    public static int endAutomataCoreWarpPointLimit;
    public static double overpoweredAutomataBreakChance;

    //World Features
    public static boolean enableVillagerStructures;
    public static boolean givePlayerBookOnJoin;

    public static class CommonConfig {

        //Defaults
        final ForgeConfigSpec.ConfigValue<String> DEFAULT_CHAT_BOX_PREFIX;

        //Restrictions
        final ForgeConfigSpec.IntValue PLAYER_DET_MAX_RANGE;
        final ForgeConfigSpec.IntValue ENERGY_DETECTOR_MAX_FLOW;
        final ForgeConfigSpec.IntValue POWERED_PERIPHERAL_MAX_ENERGY_STORED;
        final ForgeConfigSpec.IntValue NBT_STORAGE_MAX_SIZE;
        final ForgeConfigSpec.IntValue CHUNK_LOAD_VALID_TIME;

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

        // Automata Core
        final ForgeConfigSpec.IntValue ENERGY_TO_FUEL_RATE;
        final ForgeConfigSpec.BooleanValue ENABLE_WEAK_AUTOMATA_CORE;
        final ForgeConfigSpec.BooleanValue ENABLE_END_AUTOMATA_CORE;
        final ForgeConfigSpec.BooleanValue ENABLE_HUSBANDRY_AUTOMATA_CORE;
        final ForgeConfigSpec.IntValue END_AUTOMATA_CORE_WARP_POINT_LIMIT;
        final ForgeConfigSpec.DoubleValue OVERPOWERED_AUTOMATA_BREAK_CHANCE;

        //World Features
        final ForgeConfigSpec.BooleanValue ENABLE_VILLAGER_STRUCTURES;
        final ForgeConfigSpec.BooleanValue GIVE_PLAYER_BOOK_ON_JOIN;

        CommonConfig(final ForgeConfigSpec.Builder builder) {
            builder.comment("").push("Defaults");

            DEFAULT_CHAT_BOX_PREFIX = builder.comment("Defines default chatbox prefix").define("defaultChatBoxPrefix", "AP");

            builder.pop();
            builder.push("Core");

            LibConfig.build(builder);

            builder.pop();
            builder.push("Restrictions");

            PLAYER_DET_MAX_RANGE = builder.comment("The max range of the player detector functions. " +
                    "If anyone use a higher range, the detector will use this max range").defineInRange("playerDetMaxRange", 100000000, 0, 100000000);
            ENERGY_DETECTOR_MAX_FLOW = builder.comment("Defines the maximum energy flow of the energy detector.").defineInRange("energyDetectorMaxFlow", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
            POWERED_PERIPHERAL_MAX_ENERGY_STORED = builder.comment("Defines max energy stored in any powered peripheral").defineInRange("poweredPeripheralMaxEnergyStored", 100_000_000, 1_000_000, Integer.MAX_VALUE);
            NBT_STORAGE_MAX_SIZE = builder.comment("Defines max nbt string that can be stored in nbt storage").defineInRange("nbtStorageMaxSize", 1048576, 0, Integer.MAX_VALUE);
            CHUNK_LOAD_VALID_TIME = builder.comment("Time in seconds, while loaded chunk can be consider as valid without touch").defineInRange("chunkLoadValidTime", 600, 60, Integer.MAX_VALUE);

            builder.pop();

            builder.push("Features");

            ENABLE_CHAT_BOX = builder.comment("Enable the Chat Box or not.").define("enableChatBox", true);
            ENABLE_ME_BRIDGE = builder.comment("Enable the Me Bridge or not.").define("enableMeBridge", true);
            ENABLE_RS_BRIDGE = builder.comment("Enable the Rs Bridge or not.").define("enableRsBridge", true);
            ENABLE_PLAYER_DETECTOR = builder.comment("Enable the Player Detector or not.").define("enablePlayerDetector", true);
            ENABLE_ENVIRONMENT_DETECTOR = builder.comment("Enable the Environment Detector or not.").define("enableEnvironmentDetector", true);
            ENABLE_CHUNKY_TURTLE = builder.comment("Enable the Chunky Turtle or not.").define("enableChunkyTurtle", true);
            ENABLE_DEBUG_MODE = builder.comment("Enable the debug mode. You should only enable it, if a developer say it or something does not work.").define("enableDebugMode", false);
            ENABLE_ENERGY_DETECTOR = builder.comment("Enable the Energy Detector or not.").define("enableEnergyDetector", true);
            ENABLE_AR_GOGGLES = builder.comment("Enable the AR goggles or not.").define("enableARGoggles", true);
            ENABLE_INVENTORY_MANAGER = builder.comment("Enable the inventory manager or not.").define("enableInventoryManager", true);
            ENABLE_REDSTONE_INTEGRATOR = builder.comment("Enable the redstone integrator or not.").define("enableRedstoneIntegrator", true);
            ENABLE_BLOCK_READER = builder.comment("Enable the block reader or not.").define("enableBlockReader", true);
            ENABLE_GEO_SCANNER = builder.comment("Enable the geo scanner or not.").define("enableGeoScanner", true);
            ENABLE_COLONY_INTEGRATOR = builder.comment("Enable the colony integrator or not.").define("enableColonyIntegrator", true);
            ENABLE_NBT_STORAGE = builder.comment("Enable the nbt storage block or not").define("enableNBTStorage", true);
            ENABLE_POWERED_PERIPHERALS = builder.comment("Enable RF storage for peripherals, that could use it").define("enablePoweredPeripherals", false);

            builder.pop();
            builder.push("operations");
            register(SingleOperation.values(), builder);
            register(SphereOperation.values(), builder);
            register(SimpleFreeOperation.values(), builder);

            builder.pop();
            builder.push("metaphysics");
            ENERGY_TO_FUEL_RATE = builder.comment("Defines energy to fuel rate").defineInRange("energyToFuelRate", 575, 575, Integer.MAX_VALUE);
            ENABLE_WEAK_AUTOMATA_CORE = builder.define("enableWeakAutomataCore", true);
            ENABLE_END_AUTOMATA_CORE = builder.define("enableEndAutomataCore", true);
            ENABLE_HUSBANDRY_AUTOMATA_CORE = builder.define("enableHusbandryAutomataCore", true);
            END_AUTOMATA_CORE_WARP_POINT_LIMIT = builder.comment("Defines max warp point stored in warp core. Mostly need to not allow NBT overflow error").defineInRange("endAutomataCoreWarpPointLimit", 64, 1, Integer.MAX_VALUE);
            OVERPOWERED_AUTOMATA_BREAK_CHANCE = builder.comment("Chance that overpowered automata will break after rotation cycle").defineInRange("overpoweredAutomataBreakChance", 0.002, 0, 1);

            // automata core tiers registration
            register(AutomataCoreTier.values(), builder);
            builder.pop();

            builder.push("world");

            ENABLE_VILLAGER_STRUCTURES = builder.comment("Enable the villager structures for the computer scientist.").define("enableVillagerStructures", true);
            GIVE_PLAYER_BOOK_ON_JOIN = builder.comment("Gives the ap documentation to new players on a world.").define("givePlayerBookOnJoin", true);
            builder.pop();
        }

        protected void register(IConfigHandler[] data, final ForgeConfigSpec.Builder builder) {
            for (IConfigHandler handler: data) {
                handler.addToConfig(builder);
            }
        }
    }
}