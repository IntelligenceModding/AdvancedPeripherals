package de.srendi.advancedperipherals.common.configuration;

import de.srendi.advancedperipherals.lib.misc.IConfigHandler;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public interface IAPConfig {

    ModConfigSpec getConfigSpec();

    String getFileName();

    ModConfig.Type getType();

    default void register(IConfigHandler[] data, final ModConfigSpec.Builder builder) {
        for (IConfigHandler handler : data) {
            handler.addToConfig(builder);
        }
    }

    default void pop(String name, ModConfigSpec.Builder builder) {
        builder.pop();
        builder.push(name);
    }
}
