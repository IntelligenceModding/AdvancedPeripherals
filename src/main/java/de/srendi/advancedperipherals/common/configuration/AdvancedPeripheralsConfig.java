package de.srendi.advancedperipherals.common.configuration;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class AdvancedPeripheralsConfig {

    public static ForgeConfigSpec.BooleanValue CHAT_BOX_ENABLED;

    public static ForgeConfigSpec.IntValue CHAT_BOX_COOLDOWN;

    public static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;

    static {

        COMMON_BUILDER.comment("").push("Features");

        CHAT_BOX_ENABLED = COMMON_BUILDER.comment("This deactivates the methods and the event of the chat box. You need to restart the Client/Server.").define("enableChatBox", true);

        CHAT_BOX_COOLDOWN = COMMON_BUILDER.comment("The time that has to be waited before a new message can be sent. In ticks. 20 ticks is one second.").defineInRange("chatBoxCooldown", 10,1,Integer.MAX_VALUE);

        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, final Path path) {

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync().autosave().writingMode(WritingMode.REPLACE).build();

        configData.load();
        spec.setConfig(configData);

    }

}
