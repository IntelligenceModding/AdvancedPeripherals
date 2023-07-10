package de.srendi.advancedperipherals.common.configuration;

import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCoreTier;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

@FieldsAreNonnullByDefault
public class MetaphysicsConfig implements IAPConfig {

    public final ForgeConfigSpec.IntValue energyToFuelRate;
    public final ForgeConfigSpec.BooleanValue enableWeakAutomataCore;
    public final ForgeConfigSpec.BooleanValue enableEndAutomataCore;
    public final ForgeConfigSpec.BooleanValue enableHusbandryAutomataCore;
    public final ForgeConfigSpec.IntValue endAutomataCoreWarpPointLimit;
    public final ForgeConfigSpec.DoubleValue overpoweredAutomataBreakChance;
    private final ForgeConfigSpec configSpec;

    public MetaphysicsConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Config for metaphysics").push("Metaphysics");

        energyToFuelRate = builder.comment("Defines energy to fuel rate").defineInRange("energyToFuelRate", 575, 575, Integer.MAX_VALUE);
        enableWeakAutomataCore = builder.define("enableWeakAutomataCore", true);
        enableEndAutomataCore = builder.define("enableEndAutomataCore", true);
        enableHusbandryAutomataCore = builder.define("enableHusbandryAutomataCore", true);
        endAutomataCoreWarpPointLimit = builder.comment("Defines max warp point stored in warp core. Mostly need to not allow NBT overflow error").defineInRange("endAutomataCoreWarpPointLimit", 64, 1, Integer.MAX_VALUE);
        overpoweredAutomataBreakChance = builder.comment("Chance that overpowered automata will break after rotation cycle").defineInRange("overpoweredAutomataBreakChance", 0.002, 0, 1);

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
