 package de.srendi.advancedperipherals.common.addons.computercraft.operations;

import com.google.common.math.IntMath;
import de.srendi.advancedperipherals.api.peripherals.IPeripheralOperation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public enum SphereOperation implements IPeripheralOperation<SphereOperationContext> {
    SCAN_BLOCKS(2_000, 8, 16, 0.17),
    SCAN_ENTITIES(2_000, 8, 16, 0.17);

    private ForgeConfigSpec.IntValue cooldown;
    private ForgeConfigSpec.IntValue max_free_radius;
    private ForgeConfigSpec.IntValue max_cost_radius;
    private ForgeConfigSpec.DoubleValue extra_block_cost;
    private final int defaultCooldown;
    private final int defaultMaxFreeRadius;
    private final int defaultMaxCostRadius;
    private final double defaultExtraBlockCost;

    SphereOperation(int defaultCooldown, int defaultMaxFreeRadius, int defaultMaxCostRadius, double defaultExtraBlockCost) {
        this.defaultCooldown = defaultCooldown;
        this.defaultMaxFreeRadius = defaultMaxFreeRadius;
        this.defaultMaxCostRadius = defaultMaxCostRadius;
        this.defaultExtraBlockCost = defaultExtraBlockCost;
    }

    @Override
    public void addToConfig(ForgeConfigSpec.Builder builder) {
        cooldown = builder.defineInRange(
                settingsName() + "Cooldown", defaultCooldown, 1_000, Integer.MAX_VALUE
        );
        max_free_radius = builder.defineInRange(
                settingsName() + "MaxFreeRadius", defaultMaxFreeRadius, 1, 64
        );
        max_cost_radius = builder.defineInRange(
                settingsName() + "MaxCostRadius", defaultMaxCostRadius, 1, 64
        );
        extra_block_cost = builder.defineInRange(
                settingsName() + "ExtraBlockCost", defaultExtraBlockCost, 0.1, Double.MAX_VALUE
        );
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
        if (context.getRadius() <= max_free_radius.get())
            return 0;
        int freeBlockCount = IntMath.pow(2 * max_free_radius.get() + 1, 3);
        int allBlockCount = IntMath.pow(2 * context.getRadius() + 1, 3);
        return (int) Math.floor((allBlockCount - freeBlockCount) * extra_block_cost.get());
    }

    public int getMaxFreeRadius() {
        return max_free_radius.get();
    }

    public int getMaxCostRadius() {
        return max_cost_radius.get();
    }

    @Override
    public Map<String, Object> computerDescription() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", settingsName());
        data.put("type", getClass().getName());
        data.put("cooldown", cooldown.get());
        data.put("maxFreeRadius", max_free_radius.get());
        data.put("maxCostRadius", max_cost_radius.get());
        data.put("extraBlockCost", extra_block_cost.get());
        return data;
    }

    public SphereOperationContext free() {
        return new SphereOperationContext(getMaxFreeRadius());
    }

    public SphereOperationContext cost() {
        return new SphereOperationContext(getMaxCostRadius());
    }
}
