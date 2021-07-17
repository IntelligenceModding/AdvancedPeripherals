package de.srendi.advancedperipherals.common.addons.computercraft.base;

import net.minecraftforge.common.ForgeConfigSpec;

public enum CountDependOperations implements IPeripheralOperation<Integer> {
    DIG(1000, 1),
    USE_ON_BLOCK(5000, 1),
    SUCK(1000, 1),
    USE_ON_ANIMAL(2500, 10),
    CAPTURE_ANIMAL(50_000, 100);

    private ForgeConfigSpec.IntValue cooldown;
    private ForgeConfigSpec.IntValue cost;
    private final int defaultCooldown;
    private final int defaultCost;

    CountDependOperations(int defaultCooldown, int defaultCost) {
        this.defaultCooldown = defaultCooldown;
        this.defaultCost = defaultCost;
    }

    @Override
    public int getCooldown(Integer count) {
        return cooldown.get() * count;
    }

    @Override
    public int getCost(Integer count) {
        return cost.get() * count;
    }

    @Override
    public void addToConfig(ForgeConfigSpec.Builder builder) {
        cooldown = builder.defineInRange(
                settingsName() + "Cooldown", defaultCooldown, 1_000, Integer.MAX_VALUE
        );
        cost = builder.defineInRange(
                settingsName() + "Cost", defaultCost, 1, Integer.MAX_VALUE
        );
    }
}
