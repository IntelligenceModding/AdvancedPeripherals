package de.srendi.advancedperipherals.common.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class AdvancedPeripheralsConfig {

    public static int chatBoxCooldown;
    public static boolean enableChatBox;
    public static boolean enableMeBridge;
    public static boolean enableRsBridge;
    public static boolean enablePlayerDetector;
    public static boolean enableEnvironmentDetector;

    public static class CommonConfig {

        final ForgeConfigSpec.IntValue CHAT_BOX_COOLDOWN;
        final ForgeConfigSpec.BooleanValue ENABLE_CHAT_BOX;
        final ForgeConfigSpec.BooleanValue ENABLE_ME_BRIDGE;
        final ForgeConfigSpec.BooleanValue ENABLE_RS_BRIDGE;
        final ForgeConfigSpec.BooleanValue ENABLE_PLAYER_DETECTOR;
        final ForgeConfigSpec.BooleanValue ENABLE_ENVIRONMENT_DETECTOR;

        CommonConfig(final ForgeConfigSpec.Builder builder) {
            builder.comment("").push("Configuration");

            ENABLE_CHAT_BOX = builder.comment("Enable the chat box or not.").define("enableChatBox", true);
            CHAT_BOX_COOLDOWN = builder.comment("This deactivates the methods and the event of the chat box.").defineInRange("chatBoxCooldown", 10, 1, Integer.MAX_VALUE);
            ENABLE_ME_BRIDGE = builder.comment("Enable the me bridge or not.").define("enableMeBridge", true);
            ENABLE_RS_BRIDGE = builder.comment("Enable the me bridge or not.").define("enableRsBridge", true);
            ENABLE_PLAYER_DETECTOR = builder.comment("Enable the me bridge or not.").define("enablePlayerDetector", true);
            ENABLE_ENVIRONMENT_DETECTOR = builder.comment("Enable the me bridge or not.").define("enableEnvironmentDetector", true);

            builder.pop();
        }
    }
}