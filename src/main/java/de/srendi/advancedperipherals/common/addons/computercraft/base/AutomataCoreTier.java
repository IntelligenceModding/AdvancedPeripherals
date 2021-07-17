package de.srendi.advancedperipherals.common.addons.computercraft.base;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum AutomataCoreTier implements IAutomataCoreTier {
    WEAK(2, 2),
    WEAK_UPDATED(4, 3),
    OVERPOWERED_WEAK(4, 3),
    OVERPOWERED_WEAK_UPDATED(6, 4);

    private ForgeConfigSpec.IntValue interactionRadius;
    private ForgeConfigSpec.IntValue maxFuelConsumptionRate;
    private final int defaultInteractionRadius;
    private final int defaultMaxFuelConsumptionRate;

    AutomataCoreTier(int defaultInteractionRadius, int defaultMaxFuelConsumptionRate) {
        this.defaultInteractionRadius = defaultInteractionRadius;
        this.defaultMaxFuelConsumptionRate = defaultMaxFuelConsumptionRate;
    }

    @Override
    public int getInteractionRadius() {
        if (interactionRadius == null)
            return 0;
        return interactionRadius.get();
    }

    @Override
    public int getMaxFuelConsumptionRate() {
        if (maxFuelConsumptionRate == null)
            return 0;
        return maxFuelConsumptionRate.get();
    }

    @Override
    public void addToConfig(ForgeConfigSpec.Builder builder) {
        interactionRadius = builder.defineInRange(
                settingsName() + "InteractionRadius", defaultInteractionRadius, 1, 64
        );
        maxFuelConsumptionRate = builder.defineInRange(
                settingsName() + "MaxFuelConsumptionRate", defaultMaxFuelConsumptionRate, 1, 32
        );
    }

    @Override
    public String settingsPostfix() {
        return "AutomataCore";
    }
}
