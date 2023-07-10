package de.srendi.advancedperipherals.common.configuration;

import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

@FieldsAreNonnullByDefault
public class WorldConfig implements IAPConfig {

    public final ForgeConfigSpec.BooleanValue enableVillagerStructures;
    public final ForgeConfigSpec.BooleanValue givePlayerBookOnJoin;
    public final ForgeConfigSpec.IntValue villagerStructureWeight;
    private final ForgeConfigSpec configSpec;

    public WorldConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Config to adjust world settings").push("World");

        enableVillagerStructures = builder.comment("Enable the villager structures for the computer scientist.").define("enableVillagerStructures", true);
        givePlayerBookOnJoin = builder.comment("Gives the ap documentation to new players.").define("givePlayerBookOnJoin", true);
        villagerStructureWeight = builder.comment("The weight of the villager structures.").defineInRange("villagerStructureWeight", 10, 0, 16000);

        builder.pop();
        configSpec = builder.build();
    }

    @Override
    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public String getFileName() {
        return "world";
    }

    @Override
    public ModConfig.Type getType() {
        return ModConfig.Type.COMMON;
    }
    /*
    [
     */
}
