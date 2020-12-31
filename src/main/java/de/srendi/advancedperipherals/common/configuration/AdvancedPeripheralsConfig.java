package de.srendi.advancedperipherals.common.configuration;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class AdvancedPeripheralsConfig {

    public static int chatBoxCooldown;
    public static boolean enableChatBox;

    public static class CommonConfig {

        final ForgeConfigSpec.IntValue CHAT_BOX_COOLDOWN;
        final ForgeConfigSpec.BooleanValue ENABLE_CHAT_BOX;

        CommonConfig(final ForgeConfigSpec.Builder builder) {
            builder.comment("").push("Join Stuff");

            ENABLE_CHAT_BOX = builder.comment("Activate whether the message should be sent or not.").define("enableChatBox", true);
            CHAT_BOX_COOLDOWN = builder.comment("This deactivates the methods and the event of the chat box.").defineInRange("chatBoxCooldown", 10, 1, Integer.MAX_VALUE);

            builder.pop();
        }
    }
}