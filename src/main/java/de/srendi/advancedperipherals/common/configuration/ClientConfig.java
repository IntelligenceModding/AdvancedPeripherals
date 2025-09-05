package de.srendi.advancedperipherals.common.configuration;

import net.minecraft.FieldsAreNonnullByDefault;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

@FieldsAreNonnullByDefault
public class ClientConfig implements IAPConfig {

    // ME Bridge Client Settings
    public final ModConfigSpec.BooleanValue meCraftingNotifications;
    
    private final ModConfigSpec configSpec;

    public ClientConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Client-side configuration for AdvancedPeripherals").push("Client");

        builder.push("MEBridge");
        
        meCraftingNotifications = builder
            .comment("Enable visual toast notifications when forceCompleteCraftingTasks() completes jobs.",
                     "This is a per-player client setting that works alongside the server setting.",
                     "Both the server setting AND this client setting must be enabled for notifications to appear.",
                     "ComputerCraft events are always sent regardless of this setting.")
            .define("meCraftingNotifications", true);
        
        builder.pop(); // MEBridge
        builder.pop(); // Client

        configSpec = builder.build();
    }

    @Override
    public ModConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public String getFileName() {
        return "client";
    }

    @Override
    public ModConfig.Type getType() {
        return ModConfig.Type.CLIENT;
    }
}