package de.srendi.advancedperipherals.common.configuration;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class APIConfig implements IAPConfig {
    //Entity
    public final ForgeConfigSpec.BooleanValue enableEntityAPI;
    public final ForgeConfigSpec.BooleanValue enableGetNBT;
    public final ForgeConfigSpec.BooleanValue enableGetBoundingBox;
    public final ForgeConfigSpec.BooleanValue enableGetPos;
    public final ForgeConfigSpec.BooleanValue enableGetData;
    public final ForgeConfigSpec.BooleanValue enableGetPersistentData;
    public final ForgeConfigSpec.BooleanValue enablePlayerAccess;
    private final ForgeConfigSpec configSpec;

    public APIConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("APIs config").push("APIs");

        builder.push("entity");

        enableEntityAPI = builder.comment("Enables the \"entity\" API").define("enableEntityAPI", true);
        enableGetNBT = builder.comment("Activates the \"getNBT\" function of the entity API").define("enableGetNBT", true);
        enableGetBoundingBox = builder.comment("Activates the \"getBoundingBox\" function of the entity API").define("enableGetBoundingBox", true);
        enableGetPos = builder.comment("Activates the \"getPos\" function of the entity API").define("enableGetPos", true);
        enableGetData = builder.comment("Activates the \"getData\" function of the entity API").define("enableGetData", true);
        enableGetPersistentData = builder.comment("Activates the \"getPersistentData\" function of the entity API").define("enableGetPersistentData", true);
        enablePlayerAccess = builder.comment("Allows player entities to be used with the EntityAPI.").define("enablePlayerAccess", true);

        builder.pop();

        configSpec = builder.build();
    }

    @Override
    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public String getFileName() {
        return "APIs";
    }

    @Override
    public ModConfig.Type getType() {
        return ModConfig.Type.COMMON;
    }
}
