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

@FieldsAreNonnullByDefault
public class PeripheralsConfig implements IAPConfig {

    // Player Detector
    public final ModConfigSpec.IntValue playerDetMaxRange;
    public final ModConfigSpec.BooleanValue playerSpy;
    public final ModConfigSpec.BooleanValue showSpectators;
    public final ModConfigSpec.BooleanValue morePlayerInformation;
    public final ModConfigSpec.BooleanValue enablePlayerDetector;
    public final ModConfigSpec.BooleanValue playerDetMultiDimensional;
    public final ModConfigSpec.BooleanValue playerSpyRandError;
    public final ModConfigSpec.IntValue playerSpyRandErrorAmount;
    public final ModConfigSpec.IntValue playerSpyPreciseMaxRange;

    // Energy Detector
    public final ModConfigSpec.IntValue energyDetectorMaxFlow;
    public final ModConfigSpec.BooleanValue enableEnergyDetector;

    // NBT Storage
    public final ModConfigSpec.IntValue nbtStorageMaxSize;
    public final ModConfigSpec.BooleanValue enableNBTStorage;

    // Chunky turtle
    public final ModConfigSpec.IntValue chunkLoadValidTime;
    public final ModConfigSpec.IntValue chunkyTurtleRadius;
    public final ModConfigSpec.BooleanValue enableChunkyTurtle;

    // Chat box
    public final ModConfigSpec.BooleanValue enableChatBox;
    public final ModConfigSpec.ConfigValue<String> defaultChatBoxPrefix;
    public final ModConfigSpec.IntValue chatBoxMaxRange;
    public final ModConfigSpec.IntValue chatBoxMessageSize;
    public final ModConfigSpec.BooleanValue chatBoxMultiDimensional;
    public final ModConfigSpec.BooleanValue chatBoxPreventRunCommand;
    public final ModConfigSpec.BooleanValue chatBoxWrapCommand;
    public final ModConfigSpec.ConfigValue<List<? extends String>> chatBoxBannedCommands;
    private List<Predicate<String>> chatBoxCommandFilters = null;

    // ME Bridge
    public final ModConfigSpec.BooleanValue enableMEBridge;
    public final ModConfigSpec.BooleanValue enableMEBridgePatternCreator;
    public final ModConfigSpec.BooleanValue mePatternCreationRequiresPatternEncoder;
    public final ModConfigSpec.IntValue meConsumption;

    // Rs Bridge
    public final ModConfigSpec.BooleanValue enableRSBridge;
    public final ModConfigSpec.IntValue rsConsumption;

    // Environment Detector
    public final ModConfigSpec.BooleanValue enableEnvironmentDetector;

    // AR Controller
    public final ModConfigSpec.BooleanValue enableARGoggles;

    // Inventory Manager
    public final ModConfigSpec.BooleanValue enableInventoryManager;

    // Redstone Integrator
    public final ModConfigSpec.BooleanValue enableRedstoneIntegrator;

    // Block reader
    public final ModConfigSpec.BooleanValue enableBlockReader;

    // Geo Scanner
    public final ModConfigSpec.BooleanValue enableGeoScanner;

    // Colony integrator
    public final ModConfigSpec.BooleanValue enableColonyIntegrator;

    // Compass turtle
    public final ModConfigSpec.BooleanValue enableCompassTurtle;
    public final ModConfigSpec.IntValue compassAccurePlaceRadius;
    public final ModConfigSpec.IntValue compassAccurePlaceFreeRadius;

    // Powered Peripherals
    public final ModConfigSpec.BooleanValue enablePoweredPeripherals;
    public final ModConfigSpec.BooleanValue disablePocketFuelConsumption;
    public final ModConfigSpec.IntValue poweredPeripheralMaxEnergyStorage;
    private final ModConfigSpec configSpec;

    private static final List<String> chatBoxDefaultBannedCommands = Arrays.asList(
        "/execute",
        "/op",
        "/deop",
        "/gamemode",
        "/gamerule",
        "/stop",

        "/give",
        "/fill",
        "/setblock",
        "/summon",

        "/whitelist",
        "^/ban-(?:ip)?\\s*",
        "^/pardon-(?:ip)?\\s*",

        "^/save-(?:on|off)\\s*"
    );

    public PeripheralsConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Peripherals config").push("Peripherals");

        builder.push("Player_Detector");

        enablePlayerDetector = builder.comment("Enable the Player Detector or not.").define("enablePlayerDetector", true);
        playerDetMaxRange = builder.comment("The max range of the player detector functions. If anyone use a higher range, the detector will use this max range. -1 for unlimited").defineInRange("playerDetMaxRange", -1, -1, Integer.MAX_VALUE);
        playerSpy = builder.comment("Activates the \"getPlayerPos\" function of the Player Detector").define("enablePlayerPosFunction", true);
        showSpectators = builder.comment("Returns a play in any function even when they are in spectator").define("showSpectators", true);
        morePlayerInformation = builder.comment("Adds more information to `getPlayerPos` of the Player Detector. Like rotation and dimension").define("morePlayerInformation", true);
        playerDetMultiDimensional = builder.comment("If true, the player detector can observe players which aren't in the same dimension as the detector itself. `playerDetMaxRange` needs to be infinite(-1) for it to work.").define("chatBoxMultiDimensional", true);
        playerSpyRandError = builder.comment("If true, add random error to `getPlayerPos` player position that varies based on how far the player is from the detector. Prevents getting the exact position of players far from the detector.").define("enablePlayerPosRandomError", false);
        playerSpyRandErrorAmount = builder.comment("The maximum amount of error (in blocks) that can be applied to each axis of the player's position.").defineInRange("playerPosRandomErrorAmount", 1000, 0, Integer.MAX_VALUE);
        playerSpyPreciseMaxRange = builder.comment("If random error is enabled: this is the maximum range at which an exact player position is returned, before random error starts to be applied.").defineInRange("playerPosPreciseMaxRange", 100, 0, Integer.MAX_VALUE);

        pop("Energy_Detector", builder);

        enableEnergyDetector = builder.comment("Enable the Energy Detector or not.").define("enableEnergyDetector", true);
        energyDetectorMaxFlow = builder.comment("Defines the maximum energy flow of the energy detector.").defineInRange("energyDetectorMaxFlow", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);

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
        chatBoxMessageSize = builder.comment("Defines the maximal number of characters in a message. Depending on the modpack and server, too large messages can't unexpectedly disconnect players").defineInRange("chatBoxMessageSize", 1024, -0, 8192);
        chatBoxMultiDimensional = builder.comment("If true, the chat box is able to send messages to other dimensions than its own").define("chatBoxMultiDimensional", true);
        chatBoxPreventRunCommand = builder.comment("If true, the chat box cannot use 'run_command' action").define("chatBoxPreventRunCommand", false);
        chatBoxWrapCommand = builder.comment("If true, the chat box will wrap and execute 'run_command' or 'suggest_command' action with zero permission, in order to prevent operators accidently run dangerous commands.").define("chatBoxWrapCommand", true);
        chatBoxBannedCommands = builder.comment("These commands below will not be able to send by 'run_command' or 'suggest_command' action. It will match as prefix if starts with '/', other wise use regex pattern").defineList("chatBoxBannedCommands", chatBoxDefaultBannedCommands, (o) -> o instanceof String value && value.length() > 0);

        pop("ME_Bridge", builder);

        enableMEBridge = builder.comment("Enable the Me Bridge or not.").define("enableMeBridge", true);
        enableMEBridgePatternCreator = builder.comment("Allow the me bridge to create ae2 crafting patterns.").define("enableMEBridgePatternCreator", true);
        mePatternCreationRequiresPatternEncoder = builder.comment("If true, the me bridge pattern creation only works if a pattern encoder is placed within the active network.").define("mePatternCreationRequiresPatternEncoder", true);
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
        compassAccurePlaceRadius = builder.comment("The maximum distance the compass can locate accurately with in each axis.").defineInRange("compassAccurePlaceRadius", 3, 0, 8);
        compassAccurePlaceFreeRadius = builder.comment("The free distance the compass can locate accurately with in each axis.").defineInRange("compassAccurePlaceFreeRadius", 1, 0, 4);

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
    public ModConfigSpec getConfigSpec() {
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

    private List<Predicate<String>> parseChatBoxCommandFilters() {
        List<Predicate<String>> filters = new ArrayList<>();
        for (final String s : chatBoxBannedCommands.get()) {
            String p = s;
            if (p.charAt(0) == '/') {
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
