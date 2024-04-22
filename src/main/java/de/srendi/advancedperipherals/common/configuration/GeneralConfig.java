package de.srendi.advancedperipherals.common.configuration;

import de.srendi.advancedperipherals.lib.LibConfig;
import net.minecraft.FieldsAreNonnullByDefault;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeConfig;

@FieldsAreNonnullByDefault
public class GeneralConfig implements IAPConfig {

    public final ModConfigSpec.BooleanValue enableDebugMode;
    private final ModConfigSpec configSpec;

    GeneralConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Config to adjust general mod settings").push("General");

        enableDebugMode = builder.comment("Enables the debug mode. Only enable it if needed.").define("enableDebugMode", false);

        builder.pop();
        builder.push("Core");

        LibConfig.build(builder);

        builder.pop();

        configSpec = builder.build();
    }


    @Override
    public ModConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public String getFileName() {
        return "general";
    }

    @Override
    public ModConfig.Type getType() {
        return ModConfig.Type.COMMON;
    }
}
