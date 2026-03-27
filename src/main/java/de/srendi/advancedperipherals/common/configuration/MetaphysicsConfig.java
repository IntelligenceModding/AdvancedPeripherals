package de.srendi.advancedperipherals.common.configuration;

import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCoreTier;
import net.minecraft.FieldsAreNonnullByDefault;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

@FieldsAreNonnullByDefault
public class MetaphysicsConfig implements IAPConfig {

    public final ModConfigSpec.IntValue energyToFuelRate;
    public final ModConfigSpec.BooleanValue enableWeakAutomataCore;
    public final ModConfigSpec.BooleanValue enableEndAutomataCore;
    public final ModConfigSpec.BooleanValue enableHusbandryAutomataCore;
    public final ModConfigSpec.IntValue endAutomataCoreWarpPointLimit;
    public final ModConfigSpec.DoubleValue overpoweredAutomataBreakChance;
    private final ModConfigSpec configSpec;

    public MetaphysicsConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

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
    public ModConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public String getFileName() {
        return "metaphysics";
    }

    @Override
    public ModConfig.Type getType() {
        return ModConfig.Type.SERVER;
    }
}
