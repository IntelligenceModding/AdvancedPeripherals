package de.srendi.advancedperipherals.common.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class AdvancedPeripheralsConfig {

    //Restrictions
    public static int chatBoxCooldown;
    public static int playerDetMaxRange;
    public static int energyDetectorMaxFlow;

    //Features
    public static boolean enableChatBox;
    public static boolean enableMeBridge;
    public static boolean enableRsBridge;
    public static boolean enablePlayerDetector;
    public static boolean enableEnvironmentDetector;
    public static boolean enableChunkyTurtle;
    public static boolean enableDebugMode;
    public static boolean enableEnergyDetector;

    public static class CommonConfig {

        //Restrictions
        final ForgeConfigSpec.IntValue CHAT_BOX_COOLDOWN;
        final ForgeConfigSpec.IntValue PLAYER_DET_MAX_RANGE;
        final ForgeConfigSpec.IntValue ENERGY_DETECTOR_MAX_FLOW;

        //Features
        final ForgeConfigSpec.BooleanValue ENABLE_CHAT_BOX;
        final ForgeConfigSpec.BooleanValue ENABLE_ME_BRIDGE;
        final ForgeConfigSpec.BooleanValue ENABLE_RS_BRIDGE;
        final ForgeConfigSpec.BooleanValue ENABLE_PLAYER_DETECTOR;
        final ForgeConfigSpec.BooleanValue ENABLE_ENVIRONMENT_DETECTOR;
        final ForgeConfigSpec.BooleanValue ENABLE_CHUNKY_TURTLE;
        final ForgeConfigSpec.BooleanValue ENABLE_DEBUG_MODE;
        final ForgeConfigSpec.BooleanValue ENABLE_ENERGY_DETECTOR;

        CommonConfig(final ForgeConfigSpec.Builder builder) {
            builder.comment("").push("Restrictions");

            CHAT_BOX_COOLDOWN = builder.comment("Defines the chat box cooldown for message sending.").defineInRange("chatBoxCooldown", 10, 1, 100000);
            PLAYER_DET_MAX_RANGE = builder.comment("The max range of the player detector functions. " +
                    "If anyone use a higher range, the detector will use this max range").defineInRange("playerDetMaxRange", 2147483646, 0, 2147483646);
            ENERGY_DETECTOR_MAX_FLOW = builder.comment("Defines the maximum energy flow of the energy detector. " +
                    "The energy detector acts as an energy storage, do not increase this value too much.").defineInRange("energyDetectorMaxFlow", 64000, 1, Integer.MAX_VALUE);

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

            builder.pop();
        }
    }
}