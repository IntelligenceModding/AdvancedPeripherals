package de.srendi.advancedperipherals.common.addons.computercraft.operations;

import com.google.common.math.IntMath;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public enum SphereOperation implements IPeripheralOperation<SphereOperationContext> {
    SCAN_BLOCKS(2_000, 8, 16, 0.17),
    SCAN_ENTITIES(2_000, 8, 16, 0.17);

    private final int defaultCooldown;
    private final int defaultMaxFreeRadius;
    private final int defaultMaxCostRadius;
    private final double defaultExtraBlockCost;
    private ForgeConfigSpec.IntValue cooldown;
    private ForgeConfigSpec.IntValue maxFreeRadius;
    private ForgeConfigSpec.IntValue maxCostRadius;
    private ForgeConfigSpec.DoubleValue extraBlockCost;

    SphereOperation(int defaultCooldown, int defaultMaxFreeRadius, int defaultMaxCostRadius, double defaultExtraBlockCost) {
        this.defaultCooldown = defaultCooldown;
        this.defaultMaxFreeRadius = defaultMaxFreeRadius;
        this.defaultMaxCostRadius = defaultMaxCostRadius;
        this.defaultExtraBlockCost = defaultExtraBlockCost;
    }

    @Override
    public void addToConfig(ForgeConfigSpec.Builder builder) {
        cooldown = builder.defineInRange(settingsName() + "Cooldown", defaultCooldown, 1_000, Integer.MAX_VALUE);
        maxFreeRadius = builder.defineInRange(settingsName() + "MaxFreeRadius", defaultMaxFreeRadius, 1, Integer.MAX_VALUE);
        maxCostRadius = builder.defineInRange(settingsName() + "MaxCostRadius", defaultMaxCostRadius, 1, Integer.MAX_VALUE);
        extraBlockCost = builder.defineInRange(settingsName() + "ExtraBlockCost", defaultExtraBlockCost, 0.1, Double.MAX_VALUE);
    }

    @Override
    public int getInitialCooldown() {
        return cooldown.get();
    }

    @Override
    public int getCooldown(SphereOperationContext context) {
        return cooldown.get();
    }

    @Override
    public int getCost(SphereOperationContext context) {
        if (context.getRadius() <= maxFreeRadius.get()) return 0;
        int freeBlockCount = IntMath.pow(2 * maxFreeRadius.get() + 1, 3);
        int allBlockCount = IntMath.pow(2 * context.getRadius() + 1, 3);
        return (int) Math.floor((allBlockCount - freeBlockCount) * extraBlockCost.get());
    }

    public int getMaxFreeRadius() {
        return maxFreeRadius.get();
    }

    public int getMaxCostRadius() {
        return maxCostRadius.get();
    }

    @Override
    public Map<String, Object> computerDescription() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", settingsName());
        data.put("type", getClass().getName());
        data.put("cooldown", cooldown.get());
        data.put("maxFreeRadius", maxFreeRadius.get());
        data.put("maxCostRadius", maxCostRadius.get());
        data.put("extraBlockCost", extraBlockCost.get());
        return data;
    }

    public SphereOperationContext free() {
        return new SphereOperationContext(getMaxFreeRadius());
    }

    public SphereOperationContext cost() {
        return new SphereOperationContext(getMaxCostRadius());
    }
}
