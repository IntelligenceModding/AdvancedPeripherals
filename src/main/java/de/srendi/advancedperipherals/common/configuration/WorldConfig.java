package de.srendi.advancedperipherals.common.configuration;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class WorldConfig implements IAPConfig {

    private final ForgeConfigSpec configSpec;

    public final ForgeConfigSpec.BooleanValue ENABLE_VILLAGER_STRUCTURES;
    public final ForgeConfigSpec.BooleanValue GIVE_PLAYER_BOOK_ON_JOIN;
    public final ForgeConfigSpec.IntValue VILLAGER_STRUCTURE_WEIGHT;

    public WorldConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Config to adjust world settings").push("World");

        ENABLE_VILLAGER_STRUCTURES = builder.comment("Enable the villager structures for the computer scientist.").define("enableVillagerStructures", true);
        GIVE_PLAYER_BOOK_ON_JOIN = builder.comment("Gives the ap documentation to new players on a world.").define("givePlayerBookOnJoin", true);
        VILLAGER_STRUCTURE_WEIGHT = builder.comment("Gives the ap documentation to new players on a world.").defineInRange("villagerStructureWeight", 10, 0, 16000);

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
}
