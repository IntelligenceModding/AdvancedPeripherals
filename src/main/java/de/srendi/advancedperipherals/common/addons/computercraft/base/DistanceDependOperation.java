package de.srendi.advancedperipherals.common.addons.computercraft.base;

import net.minecraftforge.common.ForgeConfigSpec;

public enum DistanceDependOperation implements IPeripheralOperation<Integer> {
    WARP(1000);

    private ForgeConfigSpec.IntValue cooldown;
    private final int defaultCooldown;

    DistanceDependOperation(int defaultCooldown) {
        this.defaultCooldown = defaultCooldown;
    }

    @Override
    public int getCooldown(Integer distance) {
        return cooldown.get();
    }

    @Override
    public int getCost(Integer distance) {
        return (int) Math.sqrt(distance);
    }

    @Override
    public void addToConfig(ForgeConfigSpec.Builder builder) {
        cooldown = builder.defineInRange(
                settingsName() + "Cooldown", defaultCooldown, 1_000, Integer.MAX_VALUE
        );
    }
}
