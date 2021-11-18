package de.srendi.advancedperipherals.common.configuration;

import de.srendi.advancedperipherals.common.addons.computercraft.operations.SimpleFreeOperation;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class PeripheralsConfig implements IAPConfig {

    //Player Detector
    public final ForgeConfigSpec.IntValue PLAYER_DET_MAX_RANGE;
    public final ForgeConfigSpec.BooleanValue PLAYER_SPY;
    public final ForgeConfigSpec.BooleanValue MORE_PLAYER_INFORMATION;
    public final ForgeConfigSpec.BooleanValue ENABLE_PLAYER_DETECTOR;
    //Energy Detector
    public final ForgeConfigSpec.IntValue ENERGY_DETECTOR_MAX_FLOW;
    public final ForgeConfigSpec.BooleanValue ENABLE_ENERGY_DETECTOR;
    //NBT Storage
    public final ForgeConfigSpec.IntValue NBT_STORAGE_MAX_SIZE;
    public final ForgeConfigSpec.BooleanValue ENABLE_NBT_STORAGE;
    //Chunky turtle
    public final ForgeConfigSpec.IntValue CHUNK_LOAD_VALID_TIME;
    public final ForgeConfigSpec.BooleanValue ENABLE_CHUNKY_TURTLE;
    //Chat box
    public final ForgeConfigSpec.BooleanValue ENABLE_CHAT_BOX;
    public final ForgeConfigSpec.ConfigValue<String> DEFAULT_CHAT_BOX_PREFIX;
    //ME Bridge
    public final ForgeConfigSpec.BooleanValue ENABLE_ME_BRIDGE;
    public final ForgeConfigSpec.IntValue ME_CONSUMPTION;
    //RS Bridge
    public final ForgeConfigSpec.BooleanValue ENABLE_RS_BRIDGE;
    public final ForgeConfigSpec.IntValue RS_CONSUMPTION;
    //Environment Detector
    public final ForgeConfigSpec.BooleanValue ENABLE_ENVIRONMENT_DETECTOR;
    //AR Controller
    public final ForgeConfigSpec.BooleanValue ENABLE_AR_GOGGLES;
    //Inventory Manager
    public final ForgeConfigSpec.BooleanValue ENABLE_INVENTORY_MANAGER;
    //Redstone Integrator
    public final ForgeConfigSpec.BooleanValue ENABLE_REDSTONE_INTEGRATOR;
    //Block reader
    public final ForgeConfigSpec.BooleanValue ENABLE_BLOCK_READER;
    //Geo Scanner
    public final ForgeConfigSpec.BooleanValue ENABLE_GEO_SCANNER;
    //Colony integrator
    public final ForgeConfigSpec.BooleanValue ENABLE_COLONY_INTEGRATOR;
    //Powered Peripherals
    public final ForgeConfigSpec.BooleanValue ENABLE_POWERED_PERIPHERALS;
    public final ForgeConfigSpec.IntValue POWERED_PERIPHERAL_MAX_ENERGY_STORAGE;
    private final ForgeConfigSpec configSpec;

    public PeripheralsConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Peripherals config").push("Peripherals");

        builder.push("Player_Detector");

        ENABLE_PLAYER_DETECTOR = builder.comment("Enable the Player Detector or not.").define("enablePlayerDetector", true);
        PLAYER_DET_MAX_RANGE = builder.comment("The max range of the player detector functions. " +
                "If anyone use a higher range, the detector will use this max range").defineInRange("playerDetMaxRange", 100000000, 0, 100000000);
        PLAYER_SPY = builder.comment("Activates the \"getPlayerPos\" function of the Player Detector").define("enablePlayerPosFunction", true);
        MORE_PLAYER_INFORMATION = builder.comment("Adds more information to `getPlayerPos` of the Player Detector. Like rotation and dimension").define("morePlayerInformation", true);

        pop("Energy_Detector", builder);

        ENABLE_ENERGY_DETECTOR = builder.comment("Enable the Energy Detector or not.").define("enableEnergyDetector", true);
        ENERGY_DETECTOR_MAX_FLOW = builder.comment("Defines the maximum energy flow of the energy detector.").defineInRange("energyDetectorMaxFlow", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);

        pop("NBT_Storage", builder);

        ENABLE_NBT_STORAGE = builder.comment("Enable the nbt storage block or not").define("enableNBTStorage", true);
        NBT_STORAGE_MAX_SIZE = builder.comment("Defines max nbt string that can be stored in nbt storage").defineInRange("nbtStorageMaxSize", 1048576, 0, Integer.MAX_VALUE);

        pop("Chunky_Turtle", builder);

        ENABLE_CHUNKY_TURTLE = builder.comment("Enable the Chunky Turtle or not.").define("enableChunkyTurtle", true);
        CHUNK_LOAD_VALID_TIME = builder.comment("Time in seconds, while loaded chunk can be consider as valid without touch").defineInRange("chunkLoadValidTime", 600, 60, Integer.MAX_VALUE);

        pop("Chat_Box", builder);

        ENABLE_CHAT_BOX = builder.comment("Enable the Chat Box or not.").define("enableChatBox", true);
        DEFAULT_CHAT_BOX_PREFIX = builder.comment("Defines default chatbox prefix").define("defaultChatBoxPrefix", "AP");

        pop("ME_Bridge", builder);

        ENABLE_ME_BRIDGE = builder.comment("Enable the Me Bridge or not.").define("enableMeBridge", true);
        ME_CONSUMPTION = builder.comment("Power consumption per tick.").defineInRange("mePowerConsumption", 500, 0, Integer.MAX_VALUE);

        pop("RS_Bridge", builder);

        ENABLE_RS_BRIDGE = builder.comment("Enable the Rs Bridge or not.").define("enableRsBridge", true);
        RS_CONSUMPTION = builder.comment("Power consumption per tick.").defineInRange("rsPowerConsumption", 500, 0, Integer.MAX_VALUE);

        pop("Environment_Detector", builder);

        ENABLE_ENVIRONMENT_DETECTOR = builder.comment("Enable the Environment Detector or not.").define("enableEnvironmentDetector", true);

        pop("AR_Controller", builder);

        ENABLE_AR_GOGGLES = builder.comment("Enable the AR goggles or not.").define("enableARGoggles", true);

        pop("Inventory_Manager", builder);

        ENABLE_INVENTORY_MANAGER = builder.comment("Enable the inventory manager or not.").define("enableInventoryManager", true);

        pop("Redstone_Integrator", builder);

        ENABLE_REDSTONE_INTEGRATOR = builder.comment("Enable the redstone integrator or not.").define("enableRedstoneIntegrator", true);

        pop("Block_Reader", builder);

        ENABLE_BLOCK_READER = builder.comment("Enable the block reader or not.").define("enableBlockReader", true);

        pop("Geo_Scanner", builder);

        ENABLE_GEO_SCANNER = builder.comment("Enable the geo scanner or not.").define("enableGeoScanner", true);

        pop("Colony_Integrator", builder);

        ENABLE_COLONY_INTEGRATOR = builder.comment("Enable the colony integrator or not.").define("enableColonyIntegrator", true);

        pop("Powered_Peripherals", builder);

        ENABLE_POWERED_PERIPHERALS = builder.comment("Enable RF storage for peripherals, that could use it").define("enablePoweredPeripherals", false);
        POWERED_PERIPHERAL_MAX_ENERGY_STORAGE = builder.comment("Defines max energy storage in any powered peripheral").defineInRange("poweredPeripheralMaxEnergyStored", 100_000_000, 1_000_000, Integer.MAX_VALUE);

        pop("Operations", builder);

        register(SingleOperation.values(), builder);
        register(SphereOperation.values(), builder);
        register(SimpleFreeOperation.values(), builder);

        builder.pop();

        configSpec = builder.build();
    }

    @Override
    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public String getFileName() {
        return "peripherals";
    }

    @Override
    public ModConfig.Type getType() {
        return ModConfig.Type.COMMON;
    }
}
