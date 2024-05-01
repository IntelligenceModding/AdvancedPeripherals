package de.srendi.advancedperipherals.common.configuration;

import de.srendi.advancedperipherals.common.addons.computercraft.operations.SimpleFreeOperation;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperation;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

@FieldsAreNonnullByDefault
public class PeripheralsConfig implements IAPConfig {

    //Player Detector
    public final ForgeConfigSpec.IntValue playerDetMaxRange;
    public final ForgeConfigSpec.BooleanValue playerSpy;
    public final ForgeConfigSpec.BooleanValue morePlayerInformation;
    public final ForgeConfigSpec.BooleanValue enablePlayerDetector;
    public final ForgeConfigSpec.BooleanValue playerDetMultiDimensional;
    public final ForgeConfigSpec.BooleanValue playerSpyRandError;
    public final ForgeConfigSpec.IntValue playerSpyRandErrorAmount;
    public final ForgeConfigSpec.IntValue playerSpyPreciseMaxRange;

    //Energy Detector
    public final ForgeConfigSpec.IntValue energyDetectorMaxFlow;
    public final ForgeConfigSpec.BooleanValue enableEnergyDetector;

    //Fluid Detector
    public final ForgeConfigSpec.IntValue fluidDetectorMaxFlow;
    public final ForgeConfigSpec.BooleanValue enableFluidDetector;

    //Gas Detector
    public final ForgeConfigSpec.IntValue gasDetectorMaxFlow;
    public final ForgeConfigSpec.BooleanValue enableGasDetector;
    //NBT Storage
    public final ForgeConfigSpec.IntValue nbtStorageMaxSize;
    public final ForgeConfigSpec.BooleanValue enableNBTStorage;
    //Chunky turtle
    public final ForgeConfigSpec.IntValue chunkLoadValidTime;

    public final ForgeConfigSpec.IntValue chunkyTurtleRadius;
    public final ForgeConfigSpec.BooleanValue enableChunkyTurtle;
    //Chat box
    public final ForgeConfigSpec.BooleanValue enableChatBox;
    public final ForgeConfigSpec.ConfigValue<String> defaultChatBoxPrefix;
    public final ForgeConfigSpec.IntValue chatBoxMaxRange;
    public final ForgeConfigSpec.BooleanValue chatBoxMultiDimensional;

    //ME Bridge
    public final ForgeConfigSpec.BooleanValue enableMEBridge;
    public final ForgeConfigSpec.IntValue meConsumption;

    //Rs Bridge
    public final ForgeConfigSpec.BooleanValue enableRSBridge;
    public final ForgeConfigSpec.IntValue rsConsumption;

    //Environment Detector
    public final ForgeConfigSpec.BooleanValue enableEnvironmentDetector;

    //AR Controller
    public final ForgeConfigSpec.BooleanValue enableSmartGlasses;
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

    //Compass turtle
    public final ForgeConfigSpec.BooleanValue enableDistanceDetector;
    public final ForgeConfigSpec.DoubleValue distanceDetectorRange;
    public final ForgeConfigSpec.IntValue distanceDetectorUpdateRate;

    //Powered Peripherals
    public final ForgeConfigSpec.BooleanValue enablePoweredPeripherals;
    public final ForgeConfigSpec.BooleanValue disablePocketFuelConsumption;
    public final ForgeConfigSpec.IntValue poweredPeripheralMaxEnergyStorage;
    private final ForgeConfigSpec configSpec;

    public PeripheralsConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Peripherals config").push("Peripherals");

        builder.push("Player_Detector");

        enablePlayerDetector = builder.comment("Enable the Player Detector or not.").define("enablePlayerDetector", true);
        playerDetMaxRange = builder.comment("The max range of the player detector functions. If anyone use a higher range, the detector will use this max range. -1 for unlimited").defineInRange("playerDetMaxRange", -1, -1, Integer.MAX_VALUE);
        playerSpy = builder.comment("Activates the \"getPlayerPos\" function of the Player Detector").define("enablePlayerPosFunction", true);
        morePlayerInformation = builder.comment("Adds more information to `getPlayerPos` of the Player Detector. Like rotation and dimension").define("morePlayerInformation", true);
        playerDetMultiDimensional = builder.comment("If true, the player detector can observe players which aren't in the same dimension as the detector itself. `playerDetMaxRange` needs to be infinite(-1) for it to work.").define("chatBoxMultiDimensional", true);
        playerSpyRandError = builder.comment("If true, add random error to `getPlayerPos` player position that varies based on how far the player is from the detector. Prevents getting the exact position of players far from the detector.").define("enablePlayerPosRandomError", false);
        playerSpyRandErrorAmount = builder.comment("The maximum amount of error (in blocks) that can be applied to each axis of the player's position.").defineInRange("playerPosRandomErrorAmount", 1000, 0, Integer.MAX_VALUE);
        playerSpyPreciseMaxRange = builder.comment("If random error is enabled: this is the maximum range at which an exact player position is returned, before random error starts to be applied.").defineInRange("playerPosPreciseMaxRange", 100, 0, Integer.MAX_VALUE);

        pop("Energy_Detector", builder);

        enableEnergyDetector = builder.comment("Enable the Energy Detector or not.").define("enableEnergyDetector", true);
        energyDetectorMaxFlow = builder.comment("Defines the maximum energy flow of the energy detector.").defineInRange("energyDetectorMaxFlow", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

        pop("Fluid_Detector", builder);

        enableFluidDetector = builder.comment("Enable the Fluid Detector or not.").define("enableFluidDetector", true);
        fluidDetectorMaxFlow = builder.comment("Defines the maximum fluid flow of the fluid detector.").defineInRange("energyDetectorMaxFlow", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

        pop("Gas_Detector", builder);

        enableGasDetector = builder.comment("Enable the Gas Detector or not.").define("enableGasDetector", true);
        gasDetectorMaxFlow = builder.comment("Defines the maximum gas flow of the gas detector.").defineInRange("gasDetectorMaxFlow", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

        pop("NBT_Storage", builder);

        enableNBTStorage = builder.comment("Enable the nbt storage block or not").define("enableNBTStorage", true);
        nbtStorageMaxSize = builder.comment("Defines max nbt string length that can be stored in nbt storage").defineInRange("nbtStorageMaxSize", 1048576, 0, Integer.MAX_VALUE);

        pop("Chunky_Turtle", builder);

        enableChunkyTurtle = builder.comment("Enable the Chunky Turtle or not.").define("enableChunkyTurtle", true);
        chunkLoadValidTime = builder.comment("Time in seconds, while loaded chunk can be consider as valid without touch").defineInRange("chunkLoadValidTime", 600, 60, Integer.MAX_VALUE);
        chunkyTurtleRadius = builder.comment("Radius in chunks a single chunky turtle will load. The default value (0) only loads the chunk the turtle is in, 1 would also load the 8 surrounding chunks (9 in total) and so on").defineInRange("chunkyTurtleRadius", 0, 0, 16);

        pop("Chat_Box", builder);

        enableChatBox = builder.comment("Enable the Chat Box or not.").define("enableChatBox", true);
        defaultChatBoxPrefix = builder.comment("Defines default chatbox prefix").define("defaultChatBoxPrefix", "AP");
        chatBoxMaxRange = builder.comment("Defines the maximal range of the chat box in blocks. -1 for infinite. If the range is not -1, players in other dimensions won't able to receive messages").defineInRange("chatBoxMaxRange", -1, -1, 30000000);
        chatBoxMultiDimensional = builder.comment("If true, the chat box is able to send messages to other dimensions than its own").define("chatBoxMultiDimensional", true);

        pop("ME_Bridge", builder);

        enableMEBridge = builder.comment("Enable the Me Bridge or not.").define("enableMeBridge", true);
        meConsumption = builder.comment("Power consumption per tick.").defineInRange("mePowerConsumption", 10, 0, Integer.MAX_VALUE);

        pop("RS_Bridge", builder);
        enableRSBridge = builder.comment("Enable the Rs Bridge or not.").define("enableRsBridge", true);
        rsConsumption = builder.comment("Power consumption per tick.").defineInRange("rsPowerConsumption", 10, 0, Integer.MAX_VALUE);

        pop("Environment_Detector", builder);

        enableEnvironmentDetector = builder.comment("Enable the Environment Detector or not.").define("enableEnvironmentDetector", true);

        pop("AR_Controller", builder);

        enableSmartGlasses = builder.comment("Enable the smart glasses or not.").define("enableSmartGlasses", true);

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

        pop("Distance_Detector", builder);

        enableDistanceDetector = builder.comment("Enable the distance detector or not.").define("enableDistanceDetector", true);
        distanceDetectorRange = builder.comment("Maximum range of the distance detector").defineInRange("distanceDetectorRange", 64D, 0D, Integer.MAX_VALUE);
        distanceDetectorUpdateRate = builder.comment("Defines how often the distance detector updates it's distance if periodically updates are enabled. \n" +
                "Periodically updates exists so we do not need to run \"getDistance\" on the main thread which eliminates the 1 tick yield of the lua function").defineInRange("maxUpdateRate", 2, 1, 100);

        pop("Powered_Peripherals", builder);

        enablePoweredPeripherals = builder.comment("Enable RF storage for peripherals, that could use it").define("enablePoweredPeripherals", false);
        poweredPeripheralMaxEnergyStorage = builder.comment("Defines max energy storage in any powered peripheral").defineInRange("poweredPeripheralMaxEnergyStored", 100_000_000, 1_000_000, Integer.MAX_VALUE);

        pop("Pocket_Peripherals", builder);

        disablePocketFuelConsumption = builder.comment("If true, pockets will have infinite fuel").define("disablePocketFuelConsumption", true);

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
