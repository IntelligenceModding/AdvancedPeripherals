package de.srendi.advancedperipherals.common.configuration;

import de.srendi.advancedperipherals.common.addons.computercraft.operations.SimpleFreeOperation;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class PeripheralsConfig implements IAPConfig {

    //Player Detector
    public final ForgeConfigSpec.IntValue playerDetMaxRange;
    public final ForgeConfigSpec.BooleanValue playerSpy;
    public final ForgeConfigSpec.BooleanValue morePlayerInformation;
    public final ForgeConfigSpec.BooleanValue enablePlayerDetector;
    //Energy Detector
    public final ForgeConfigSpec.IntValue energyDetectorMaxFlow;
    public final ForgeConfigSpec.BooleanValue enableEnergyDetector;
    //NBT Storage
    public final ForgeConfigSpec.IntValue nbtStorageMaxSize;
    public final ForgeConfigSpec.BooleanValue enableNBTStorage;
    //Chunky turtle
    public final ForgeConfigSpec.IntValue chunkLoadValidTime;
    public final ForgeConfigSpec.BooleanValue enableChunkyTurtle;
    //Chat box
    public final ForgeConfigSpec.BooleanValue enableChatBox;
    public final ForgeConfigSpec.ConfigValue<String> defaultChatBoxPrefix;
    //ME Bridge
    public final ForgeConfigSpec.BooleanValue enableMEBridge;
    public final ForgeConfigSpec.IntValue meConsumption;
    //Rs Bridge
    public final ForgeConfigSpec.BooleanValue enableRSBridge;
    public final ForgeConfigSpec.IntValue rsConsumption;
    //Environment Detector
    public final ForgeConfigSpec.BooleanValue enableEnvironmentDetector;
    //AR Controller
    public final ForgeConfigSpec.BooleanValue enableARGoggles;
    //Inventory Manager
    public final ForgeConfigSpec.BooleanValue enableInventoryManager;
    //Redstone Integrator
    public final ForgeConfigSpec.BooleanValue enableRedstoneIntegrator;
    //Block reader
    public final ForgeConfigSpec.BooleanValue enableBlockReader;
    //Geo Scanner
    public final ForgeConfigSpec.BooleanValue enableGeoScanner;
    //Colony integrator
    public final ForgeConfigSpec.BooleanValue enableColonyIntegrator;
    //Compass turtle
    public final ForgeConfigSpec.BooleanValue enableCompassTurtle;
    //Powered Peripherals
    public final ForgeConfigSpec.BooleanValue enablePoweredPeripherals;
    public final ForgeConfigSpec.IntValue poweredPeripheralMaxEnergyStorage;
    private final ForgeConfigSpec configSpec;

    public PeripheralsConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Peripherals config").push("Peripherals");

        builder.push("Player_Detector");

        enablePlayerDetector = builder.comment("Enable the Player Detector or not.").define("enablePlayerDetector", true);
        playerDetMaxRange = builder.comment("The max range of the player detector functions. " + "If anyone use a higher range, the detector will use this max range").defineInRange("playerDetMaxRange", 100000000, 0, 100000000);
        playerSpy = builder.comment("Activates the \"getPlayerPos\" function of the Player Detector").define("enablePlayerPosFunction", true);
        morePlayerInformation = builder.comment("Adds more information to `getPlayerPos` of the Player Detector. Like rotation and dimension").define("morePlayerInformation", true);

        pop("Energy_Detector", builder);

        enableEnergyDetector = builder.comment("Enable the Energy Detector or not.").define("enableEnergyDetector", true);
        energyDetectorMaxFlow = builder.comment("Defines the maximum energy flow of the energy detector.").defineInRange("energyDetectorMaxFlow", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);

        pop("NBT_Storage", builder);

        enableNBTStorage = builder.comment("Enable the nbt storage block or not").define("enableNBTStorage", true);
        nbtStorageMaxSize = builder.comment("Defines max nbt string length that can be stored in nbt storage").defineInRange("nbtStorageMaxSize", 1048576, 0, Integer.MAX_VALUE);

        pop("Chunky_Turtle", builder);

        enableChunkyTurtle = builder.comment("Enable the Chunky Turtle or not.").define("enableChunkyTurtle", true);
        chunkLoadValidTime = builder.comment("Time in seconds, while loaded chunk can be consider as valid without touch").defineInRange("chunkLoadValidTime", 600, 60, Integer.MAX_VALUE);

        pop("Chat_Box", builder);

        enableChatBox = builder.comment("Enable the Chat Box or not.").define("enableChatBox", true);
        defaultChatBoxPrefix = builder.comment("Defines default chatbox prefix").define("defaultChatBoxPrefix", "AP");

        pop("ME_Bridge", builder);

        enableMEBridge = builder.comment("Enable the Me Bridge or not.").define("enableMeBridge", true);
        meConsumption = builder.comment("Power consumption per tick.").defineInRange("mePowerConsumption", 10, 0, Integer.MAX_VALUE);

        pop("RS_Bridge", builder);
        enableRSBridge = builder.comment("Enable the Rs Bridge or not.").define("enableRsBridge", true);
        rsConsumption = builder.comment("Power consumption per tick.").defineInRange("rsPowerConsumption", 10, 0, Integer.MAX_VALUE);

        pop("Environment_Detector", builder);

        enableEnvironmentDetector = builder.comment("Enable the Environment Detector or not.").define("enableEnvironmentDetector", true);

        pop("AR_Controller", builder);

        enableARGoggles = builder.comment("Enable the AR goggles or not.").define("enableARGoggles", true);

        pop("Inventory_Manager", builder);

        enableInventoryManager = builder.comment("Enable the inventory manager or not.").define("enableInventoryManager", true);

        pop("Redstone_Integrator", builder);

        enableRedstoneIntegrator = builder.comment("Enable the redstone integrator or not.").define("enableRedstoneIntegrator", true);

        pop("Block_Reader", builder);

        enableBlockReader = builder.comment("Enable the block reader or not.").define("enableBlockReader", true);

        pop("Geo_Scanner", builder);

        enableGeoScanner = builder.comment("Enable the geo scanner or not.").define("enableGeoScanner", true);

        pop("Colony_Integrator", builder);

        enableColonyIntegrator = builder.comment("Enable the colony integrator or not.").define("enableColonyIntegrator", true);

        pop("Compass_Turtle", builder);

        enableCompassTurtle = builder.comment("Enable the compass turtle or not.").define("enableCompassTurtle", true);

        pop("Powered_Peripherals", builder);

        enablePoweredPeripherals = builder.comment("Enable RF storage for peripherals, that could use it").define("enablePoweredPeripherals", false);
        poweredPeripheralMaxEnergyStorage = builder.comment("Defines max energy storage in any powered peripheral").defineInRange("poweredPeripheralMaxEnergyStored", 100_000_000, 1_000_000, Integer.MAX_VALUE);

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
