package de.srendi.advancedperipherals.common.addons.computercraft.operations;

import de.srendi.advancedperipherals.lib.metaphysics.IAutomataCoreTier;
import net.neoforged.neoforge.common.ModConfigSpec;

public enum AutomataCoreTier implements IAutomataCoreTier {
    TIER1(2, 2),
    TIER2(4, 3),
    OVERPOWERED_TIER1(4, 3),
    OVERPOWERED_TIER2(6, 4);

    private final int defaultInteractionRadius;
    private final int defaultMaxFuelConsumptionRate;
    private ModConfigSpec.IntValue interactionRadius;
    private ModConfigSpec.IntValue maxFuelConsumptionRate;

    AutomataCoreTier(int defaultInteractionRadius, int defaultMaxFuelConsumptionRate) {
        this.defaultInteractionRadius = defaultInteractionRadius;
        this.defaultMaxFuelConsumptionRate = defaultMaxFuelConsumptionRate;
    }

    @Override
    public int getInteractionRadius() {
        if (interactionRadius == null) return 0;
        return interactionRadius.get();
    }

    @Override
    public int getMaxFuelConsumptionRate() {
        if (maxFuelConsumptionRate == null) return 0;
        return maxFuelConsumptionRate.get();
    }

    @Override
    public void addToConfig(ModConfigSpec.Builder builder) {
        interactionRadius = builder.defineInRange(settingsName() + "InteractionRadius", defaultInteractionRadius, 1, 64);
        maxFuelConsumptionRate = builder.defineInRange(settingsName() + "MaxFuelConsumptionRate", defaultMaxFuelConsumptionRate, 1, 32);
    }

    @Override
    public String settingsPostfix() {
        return "AutomataCore";
    }

}
