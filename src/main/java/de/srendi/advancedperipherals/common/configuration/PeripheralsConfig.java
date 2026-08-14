package de.srendi.advancedperipherals.common.configuration;

import de.srendi.advancedperipherals.common.addons.computercraft.operations.SimpleFreeOperation;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperation;
import net.minecraft.FieldsAreNonnullByDefault;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * #### ORDERING NOTE ####
 * 1. Block peripherals, keep the order as defined in {@link de.srendi.advancedperipherals.common.setup.APBlocks}
 * 2. Turtle/pocket upgrades, keep the order as defined in {@link de.srendi.advancedperipherals.common.setup.CCRegistration}
 *
 * This way configs are ordered same as what shown in our creative tab
 */
@FieldsAreNonnullByDefault
public class PeripheralsConfig implements IAPConfig {
    //// CONFIGS BEGIN ////

    // Chat box
    public final ModConfigSpec.BooleanValue enableChatBox;
    public final ModConfigSpec.ConfigValue<String> defaultChatBoxPrefix;
    public final ModConfigSpec.IntValue chatBoxMaxRange;
    public final ModConfigSpec.IntValue chatBoxMessageSize;
    public final ModConfigSpec.BooleanValue chatBoxMultiDimensional;
    public final ModConfigSpec.BooleanValue chatBoxBroadcast;
    public final ModConfigSpec.BooleanValue chatBoxPreventRunCommand;
    public final ModConfigSpec.BooleanValue chatBoxWrapCommand;
    private final ModConfigSpec.ConfigValue<List<? extends String>> chatBoxBannedCommands;
    private List<Predicate<String>> chatBoxCommandFilters = null;

    // Distance Detector
    public final ModConfigSpec.BooleanValue enableDistanceDetector;
    public final ModConfigSpec.DoubleValue distanceDetectorRange;
    public final ModConfigSpec.IntValue distanceDetectorUpdateRate;

    // Environment Detector
    public final ModConfigSpec.BooleanValue enableEnvironmentDetector;

    // Geo Scanner
    public final ModConfigSpec.BooleanValue enableGeoScanner;

    // Player Detector
    public final ModConfigSpec.BooleanValue enablePlayerDetector;
    public final ModConfigSpec.IntValue playerDetMaxRange;
    public final ModConfigSpec.BooleanValue enablePlayerEvents;
    public final ModConfigSpec.BooleanValue playerSpy;
    public final ModConfigSpec.BooleanValue showSpectators;
    public final ModConfigSpec.BooleanValue morePlayerInformation;
    public final ModConfigSpec.BooleanValue playerDetMultiDimensional;
    public final ModConfigSpec.BooleanValue playerSpyRandError;
    public final ModConfigSpec.IntValue playerSpyRandErrorAmount;
    public final ModConfigSpec.IntValue playerSpyPreciseMaxRange;
    public final ModConfigSpec.BooleanValue playerSpyStatistics;

    // Energy Detector
    public final ModConfigSpec.IntValue energyDetectorMaxFlow;
    public final ModConfigSpec.BooleanValue enableEnergyDetector;

    // Fluid Detector
    public final ModConfigSpec.IntValue fluidDetectorMaxFlow;
    public final ModConfigSpec.BooleanValue enableFluidDetector;

    // Gas Detector
    public final ModConfigSpec.IntValue gasDetectorMaxFlow;
    public final ModConfigSpec.BooleanValue enableGasDetector;

    // Block reader
    public final ModConfigSpec.BooleanValue enableBlockReader;

    // NBT Storage
    public final ModConfigSpec.IntValue nbtStorageMaxSize;
    public final ModConfigSpec.BooleanValue enableNBTStorage;

    // Inventory Manager
    public final ModConfigSpec.BooleanValue enableInventoryManager;
    public final ModConfigSpec.BooleanValue enableItemsTransfer;

    // Colony integrator
    public final ModConfigSpec.BooleanValue enableColonyIntegrator;

    // ME Bridge
    public final ModConfigSpec.BooleanValue enableMEBridge;
    public final ModConfigSpec.IntValue meConsumption;

    // Rs Bridge
    public final ModConfigSpec.BooleanValue enableRSBridge;
    public final ModConfigSpec.IntValue rsConsumption;

    // Compass turtle
    public final ModConfigSpec.BooleanValue enableCompassTurtle;
    public final ModConfigSpec.IntValue compassAccurePlaceRadius;
    public final ModConfigSpec.IntValue compassAccurePlaceFreeRadius;

    // Saddle turtle (it's tamed)
    public final ModConfigSpec.BooleanValue enableSaddleTurtle;
    public final ModConfigSpec.BooleanValue allowSaddleTurtleCapturePlayer;

    // Chunky turtle
    public final ModConfigSpec.IntValue chunkLoadValidTime;
    public final ModConfigSpec.IntValue chunkyTurtleRadius;
    public final ModConfigSpec.BooleanValue enableChunkyTurtle;

    // Powered Peripherals
    public final ModConfigSpec.BooleanValue enablePoweredPeripherals;
    public final ModConfigSpec.IntValue poweredPeripheralMaxEnergyStorage;

    // Pocket Peripherals
    public final ModConfigSpec.BooleanValue disablePocketFuelConsumption;

    //// CONFIGS END ////

    private final ModConfigSpec configSpec;

    private static final List<String> chatBoxDefaultBannedCommands = Arrays.asList(
        // TODO: these commands all requires permissions, which are banned by chatBoxWrapCommand already,
        //       so we in fact do not need them.
        // Instead, we should find some common used non-permission commands provided by other mods to replace the list.
        "execute",
        "op",
        "deop",
        "gamemode",
        "gamerule",
        "stop",

        "give",
        "fill",
        "setblock",
        "summon",

        "whitelist",
        "^ban(?:-ip)?\\s*",
        "^pardon(?:-ip)?\\s*",

        "save-"
    );

    public PeripheralsConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Peripherals config").push("Peripherals");

        builder.push("Chat_Box");

        enableChatBox = builder
            .comment("Enable the Chat Box or not.")
            .define("enableChatBox", true);
        defaultChatBoxPrefix = builder
            .comment("Defines default chatbox prefix")
            .define("defaultChatBoxPrefix", "AP");
        chatBoxMaxRange = builder
            .comment("Defines the maximal range of the chat box in blocks. -1 for infinite. If the range is not -1, players in other dimensions won't able to receive messages")
            .defineInRange("chatBoxMaxRange", -1, -1, 30000000);
        chatBoxMessageSize = builder
            .comment("Defines the maximal number of characters in a message. Depending on the modpack and server, large message may unexpectedly disconnect players")
            .defineInRange("chatBoxMessageSize", 1024, -0, 8192);
        chatBoxMultiDimensional = builder
            .comment("If true, the chat box is able to send messages to other dimensions than its own")
            .define("chatBoxMultiDimensional", true);
        chatBoxBroadcast = builder
            .comment("If true, chat box will 'broadcast' the messages instead of sending one individually to each player. This option does not affect anything directly, and relies on other mods to utilise this behavior (for example, mods that bridge minecraft chat to other platforms can intercept broadcasted messages and relay them, unlike individually sent ones). This option will only go in effect if `chatBoxMaxRange` is set to `-1` and `chatBoxMultiDimensional` is also set to `true`.")
            .define("chatBoxBroadcast", true);
        chatBoxPreventRunCommand = builder
            .comment("If true, the chat box cannot use 'run_command' action")
            .define("chatBoxPreventRunCommand", false);
        chatBoxWrapCommand = builder
            .comment("If true, the chat box will wrap and execute 'run_command' or 'suggest_command' action with zero permission, in order to prevent operators accidently run dangerous commands.")
            .define("chatBoxWrapCommand", true);
        chatBoxBannedCommands = builder
            .comment("These commands are not be able to send via 'run_command' or 'suggest_command' action. It will match as a regex pattern if starts with '^', otherwise will match as prefix. '/' prefix should not present.")
            .defineList("chatBoxBannedCommands", chatBoxDefaultBannedCommands, (o) -> o instanceof String value && value.length() > 0);

        pop("Distance_Detector", builder);

        enableDistanceDetector = builder
            .comment("Enable the distance detector or not.")
            .define("enableDistanceDetector", true);
        distanceDetectorRange = builder
            .comment("Maximum range of the distance detector")
            .defineInRange("distanceDetectorRange", 64D, 0D, Integer.MAX_VALUE);
        distanceDetectorUpdateRate = builder
            .comment("Defines how often the distance detector updates it's distance if periodically updates are enabled. \n" +
                "Periodically updates exists so we do not need to run \"getDistance\" on the main thread which eliminates the 1 tick yield of the lua function")
            .defineInRange("maxUpdateRate", 2, 1, 100);

        pop("Environment_Detector", builder);

        enableEnvironmentDetector = builder
            .comment("Enable the Environment Detector or not.")
            .define("enableEnvironmentDetector", true);

        pop("Geo_Scanner", builder);

        enableGeoScanner = builder
            .comment("Enable the geo scanner or not.")
            .define("enableGeoScanner", true);

        // pop("Redstone_Integrator", builder);

        // enableRedstoneIntegrator = builder
        //     .comment("Enable the redstone integrator or not.")
        //     .define("enableRedstoneIntegrator", true);

        pop("Player_Detector", builder);

        enablePlayerDetector = builder
            .comment("Enable the Player Detector or not.")
            .define("enablePlayerDetector", true);
        playerDetMaxRange = builder
            .comment("The max range of the player detector functions. If anyone use a higher range, the detector will use this max range. -1 for unlimited")
            .defineInRange("playerDetMaxRange", -1, -1, Integer.MAX_VALUE);
        enablePlayerEvents = builder
            .comment("Let player detector fire events for certain player activities. Such as `player_join`, `player_leave`.")
            .define("enablePlayerEvents", true);
        playerSpy = builder
            .comment("Activates the `getPlayer` function of the Player Detector")
            .define("enablePlayerPosFunction", true);
        showSpectators = builder
            .comment("Returns a player in any function even when they are in spectator")
            .define("showSpectators", true);
        morePlayerInformation = builder
            .comment("Adds more information to `getPlayer` of the Player Detector. Like rotation and dimension")
            .define("morePlayerInformation", true);
        playerDetMultiDimensional = builder
            .comment("If true, the player detector can observe players which aren't in the same dimension as the detector itself. `playerDetMaxRange` needs to be infinite(-1) for it to work.")
            .define("playerDetMultiDimensional", true);
        playerSpyRandError = builder
            .comment("If true, add random error to `getPlayer` player position that varies based on how far the player is from the detector. Prevents getting the exact position of players far from the detector.")
            .define("enablePlayerPosRandomError", false);
        playerSpyRandErrorAmount = builder
            .comment("The maximum amount of error (in blocks) that can be applied to each axis of the player's position.")
            .defineInRange("playerPosRandomErrorAmount", 1000, 0, Integer.MAX_VALUE);
        playerSpyPreciseMaxRange = builder
            .comment("If random error is enabled: this is the maximum range at which an exact player position is returned, before random error starts to be applied.")
            .defineInRange("playerPosPreciseMaxRange", 100, 0, Integer.MAX_VALUE);
        playerSpyStatistics = builder
            .comment("Allows `getPlayer` return a player statistics queryer")
            .define("playerSpyStatistics", true);

        pop("Energy_Detector", builder);

        enableEnergyDetector = builder
            .comment("Enable the Energy Detector or not.")
            .define("enableEnergyDetector", true);
        energyDetectorMaxFlow = builder
            .comment("Defines the maximum energy flow of the energy detector.")
            .defineInRange("energyDetectorMaxFlow", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

        pop("Fluid_Detector", builder);

        enableFluidDetector = builder
            .comment("Enable the Fluid Detector or not.")
            .define("enableFluidDetector", true);
        fluidDetectorMaxFlow = builder
            .comment("Defines the maximum fluid flow of the fluid detector.")
            .defineInRange("fluidDetectorMaxFlow", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

        pop("Gas_Detector", builder);

        enableGasDetector = builder
            .comment("Enable the Gas Detector or not.")
            .define("enableGasDetector", true);
        gasDetectorMaxFlow = builder
            .comment("Defines the maximum gas flow of the gas detector.")
            .defineInRange("gasDetectorMaxFlow", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

        pop("Block_Reader", builder);

        enableBlockReader = builder
            .comment("Enable the block reader or not.")
            .define("enableBlockReader", true);

        pop("NBT_Storage", builder);

        enableNBTStorage = builder
            .comment("Enable the nbt storage block or not")
            .define("enableNBTStorage", true);
        nbtStorageMaxSize = builder
            .comment("Defines max nbt string length that can be stored in nbt storage")
            .defineInRange("nbtStorageMaxSize", 1048576, 0, Integer.MAX_VALUE);

        pop("Inventory_Manager", builder);

        enableInventoryManager = builder
            .comment("Enable the inventory manager or not.")
            .define("enableInventoryManager", true);
        enableItemsTransfer = builder
            .comment("Enable items transfer methods for inventory manager")
            .define("enableItemsTransfer", true);

        pop("Colony_Integrator", builder);

        enableColonyIntegrator = builder
            .comment("Enable the colony integrator or not.")
            .define("enableColonyIntegrator", true);

        pop("ME_Bridge", builder);

        enableMEBridge = builder
            .comment("Enable the Me Bridge or not.")
            .define("enableMeBridge", true);
        meConsumption = builder
            .comment("Power consumption per tick.")
            .defineInRange("mePowerConsumption", 10, 0, Integer.MAX_VALUE);

        pop("RS_Bridge", builder);
        enableRSBridge = builder
            .comment("Enable the Rs Bridge or not.")
            .define("enableRsBridge", true);
        rsConsumption = builder
            .comment("Power consumption per tick.")
            .defineInRange("rsPowerConsumption", 10, 0, Integer.MAX_VALUE);

        pop("Compass_Turtle", builder);

        enableCompassTurtle = builder
            .comment("Enable the compass turtle or not.")
            .define("enableCompassTurtle", true);
        compassAccurePlaceRadius = builder
            .comment("The maximum distance the compass can locate accurately with in each axis.")
            .defineInRange("compassAccurePlaceRadius", 3, 0, 8);
        compassAccurePlaceFreeRadius = builder
            .comment("The free distance the compass can locate accurately with in each axis.")
            .defineInRange("compassAccurePlaceFreeRadius", 1, 0, 4);

        pop("Saddle_Turtle", builder);

        enableSaddleTurtle = builder
            .comment("Enable saddle turtle")
            .define("enableSaddleTurtle", true);
        allowSaddleTurtleCapturePlayer = builder
            .comment("Allow saddle turtle to capture player")
            .define("allowSaddleTurtleCapturePlayer", true);

        pop("Chunky_Turtle", builder);

        enableChunkyTurtle = builder
            .comment("Enable the Chunky Turtle or not.")
            .define("enableChunkyTurtle", true);
        chunkLoadValidTime = builder
            .comment("Time in seconds, while loaded chunk can be consider as valid without touch")
            .defineInRange("chunkLoadValidTime", 600, 60, Integer.MAX_VALUE);
        chunkyTurtleRadius = builder
            .comment("Radius in chunks a single chunky turtle will load. The default value (0) only loads the chunk the turtle is in, 1 would also load the 8 surrounding chunks (9 in total) and so on")
            .defineInRange("chunkyTurtleRadius", 0, 0, 16);

        pop("Powered_Peripherals", builder);

        enablePoweredPeripherals = builder
            .comment("Enable RF storage for peripherals, that could use it")
            .define("enablePoweredPeripherals", false);
        poweredPeripheralMaxEnergyStorage = builder
            .comment("Defines max energy storage in any powered peripheral")
            .defineInRange("poweredPeripheralMaxEnergyStored", 100_000_000, 1_000_000, Integer.MAX_VALUE);

        pop("Pocket_Peripherals", builder);

        disablePocketFuelConsumption = builder
            .comment("If true, pockets will have infinite fuel")
            .define("disablePocketFuelConsumption", true);

        pop("Operations", builder);

        register(SingleOperation.values(), builder);
        register(SphereOperation.values(), builder);
        register(SimpleFreeOperation.values(), builder);

        builder.pop();

        configSpec = builder.build();
    }

    @Override
    public ModConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public String getFileName() {
        return "peripherals";
    }

    @Override
    public ModConfig.Type getType() {
        return ModConfig.Type.SERVER;
    }

    private List<Predicate<String>> parseChatBoxCommandFilters() {
        List<Predicate<String>> filters = new ArrayList<>();
        for (final String s : chatBoxBannedCommands.get()) {
            String p = s;
            if (p.charAt(0) != '^') {
                p = p.replaceAll("\\s+", "\\\\s+");
                if (p.equals(s)) {
                    final String prefix = s;
                    filters.add((v) -> v.startsWith(prefix) && (v.length() == prefix.length() || " \t".indexOf(v.charAt(prefix.length())) != -1));
                    continue;
                }
                p = "^" + p + "\\s*";
            }
            filters.add(Pattern.compile(p).asPredicate());
        }
        return filters;
    }

    public List<Predicate<String>> getChatBoxCommandFilters() {
        if (chatBoxCommandFilters == null) {
            chatBoxCommandFilters = parseChatBoxCommandFilters();
        }
        return chatBoxCommandFilters;
    }
}
