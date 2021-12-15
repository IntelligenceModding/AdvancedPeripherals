package de.srendi.advancedperipherals.common.configuration;

import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCoreTier;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class MetaphysicsConfig implements IAPConfig {

    public final ForgeConfigSpec.IntValue ENERGY_TO_FUEL_RATE;
    public final ForgeConfigSpec.BooleanValue ENABLE_WEAK_AUTOMATA_CORE;
    public final ForgeConfigSpec.BooleanValue ENABLE_END_AUTOMATA_CORE;
    public final ForgeConfigSpec.BooleanValue ENABLE_HUSBANDRY_AUTOMATA_CORE;
    public final ForgeConfigSpec.IntValue END_AUTOMATA_CORE_WARP_POINT_LIMIT;
    public final ForgeConfigSpec.DoubleValue OVERPOWERED_AUTOMATA_BREAK_CHANCE;
    private final ForgeConfigSpec configSpec;

    public MetaphysicsConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Config for metaphysics").push("Metaphysics");

        ENERGY_TO_FUEL_RATE = builder.comment("Defines energy to fuel rate").defineInRange("energyToFuelRate", 575, 575, Integer.MAX_VALUE);
        ENABLE_WEAK_AUTOMATA_CORE = builder.define("enableWeakAutomataCore", true);
        ENABLE_END_AUTOMATA_CORE = builder.define("enableEndAutomataCore", true);
        ENABLE_HUSBANDRY_AUTOMATA_CORE = builder.define("enableHusbandryAutomataCore", true);
        END_AUTOMATA_CORE_WARP_POINT_LIMIT = builder.comment("Defines max warp point stored in warp core. Mostly need to not allow NBT overflow error").defineInRange("endAutomataCoreWarpPointLimit", 64, 1, Integer.MAX_VALUE);
        OVERPOWERED_AUTOMATA_BREAK_CHANCE = builder.comment("Chance that overpowered automata will break after rotation cycle").defineInRange("overpoweredAutomataBreakChance", 0.002, 0, 1);

        register(AutomataCoreTier.values(), builder);

        builder.pop();

        configSpec = builder.build();
    }

    @Override
    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public String getFileName() {
        return "metaphysics";
    }

    @Override
    public ModConfig.Type getType() {
        return ModConfig.Type.COMMON;
    }
}
