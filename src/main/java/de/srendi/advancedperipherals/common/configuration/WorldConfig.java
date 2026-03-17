package de.srendi.advancedperipherals.common.configuration;

import net.minecraft.FieldsAreNonnullByDefault;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

@FieldsAreNonnullByDefault
public class WorldConfig implements IAPConfig {

    public final ModConfigSpec.BooleanValue enableVillagerStructures;
    public final ModConfigSpec.BooleanValue givePlayerBookOnJoin;
    public final ModConfigSpec.IntValue villagerStructureWeight;
    public final ModConfigSpec.BooleanValue enableWanderingTraderTrades;
    public final ModConfigSpec.BooleanValue enableComputerScientistTrades;
    private final ModConfigSpec configSpec;

    public WorldConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Config to adjust world settings").push("World");

        enableVillagerStructures = builder.comment("Enable the villager structures for the computer scientist.").define("enableVillagerStructures", true);
        givePlayerBookOnJoin = builder.comment("Gives the ap documentation to new players.").define("givePlayerBookOnJoin", true);
        villagerStructureWeight = builder.comment("The weight of the villager structures.").defineInRange("villagerStructureWeight", 10, 0, 16000);
        enableWanderingTraderTrades = builder.comment("Enable new wandering trader trades.").define("enableWanderingTraderTrades", true);
        enableComputerScientistTrades = builder.comment("Enable computer scientist trades.").define("enableComputerScientistTrades", true);

        builder.pop();
        configSpec = builder.build();
    }

    @Override
    public ModConfigSpec getConfigSpec() {
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
