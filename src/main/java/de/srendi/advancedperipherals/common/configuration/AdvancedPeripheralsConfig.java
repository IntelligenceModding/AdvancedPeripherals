package de.srendi.advancedperipherals.common.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class AdvancedPeripheralsConfig {

    public static int chatBoxCooldown;
    public static boolean enableChatBox;
    public static boolean enableMeBridge;

    public static class CommonConfig {

        final ForgeConfigSpec.IntValue CHAT_BOX_COOLDOWN;
        final ForgeConfigSpec.BooleanValue ENABLE_CHAT_BOX;
        final ForgeConfigSpec.BooleanValue ENABLE_ME_BRIDGE;

        CommonConfig(final ForgeConfigSpec.Builder builder) {
            builder.comment("").push("Configuration");

            ENABLE_CHAT_BOX = builder.comment("Activate whether the message should be sent or not.").define("enableChatBox", true);
            CHAT_BOX_COOLDOWN = builder.comment("This deactivates the methods and the event of the chat box.").defineInRange("chatBoxCooldown", 10, 1, Integer.MAX_VALUE);
            ENABLE_ME_BRIDGE = builder.comment("Activate the me bridge or not.").define("enableMeBridge", true);

            builder.pop();
        }
    }
}