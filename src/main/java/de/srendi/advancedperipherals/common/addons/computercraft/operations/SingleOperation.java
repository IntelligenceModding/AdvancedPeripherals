package de.srendi.advancedperipherals.common.addons.computercraft.operations;

import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public enum SingleOperation implements IPeripheralOperation<SingleOperationContext> {
    DIG(1000, 1),
    USE_ON_BLOCK(5000, 1),
    SUCK(1000, 1),
    USE_ON_ANIMAL(2500, 10),
    CAPTURE_ANIMAL(50_000, 100),
    WARP(1000, DistancePolicy.IGNORED, CountPolicy.MULTIPLY, 1, DistancePolicy.SQRT, CountPolicy.MULTIPLY),
    PREPARE_PORTAL(3_000, 600),
    ACTIVE_PORTAL(60_000, 1);

    private final int defaultCooldown;
    private final DistancePolicy distanceCooldownPolicy;
    private final CountPolicy countCooldownPolicy;
    private final int defaultCost;
    private final DistancePolicy distanceCostPolicy;
    private final CountPolicy countCostPolicy;
    private ForgeConfigSpec.IntValue cooldown;
    private ForgeConfigSpec.IntValue cost;

    SingleOperation(int defaultCooldown, DistancePolicy distanceCooldownPolicy, CountPolicy countCooldownPolicy, int defaultCost, DistancePolicy distanceCostPolicy, CountPolicy countCostPolicy) {
        this.defaultCooldown = defaultCooldown;
        this.defaultCost = defaultCost;
        this.distanceCooldownPolicy = distanceCooldownPolicy;
        this.countCooldownPolicy = countCooldownPolicy;
        this.distanceCostPolicy = distanceCostPolicy;
        this.countCostPolicy = countCostPolicy;
    }

    SingleOperation(int defaultCooldown, int defaultCost) {
        this(defaultCooldown, DistancePolicy.IGNORED, CountPolicy.IGNORED, defaultCost, DistancePolicy.IGNORED, CountPolicy.IGNORED);
    }

    @Override
    public int getInitialCooldown() {
        return cooldown.get() * countCooldownPolicy.getFactor(5) * distanceCooldownPolicy.getFactor(2);
    }

    @Override
    public int getCooldown(SingleOperationContext context) {
        return cooldown.get() * countCooldownPolicy.getFactor(context.getCount()) * distanceCooldownPolicy.getFactor(context.getDistance());
    }

    @Override
    public int getCost(SingleOperationContext context) {
        return cost.get() * countCostPolicy.getFactor(context.getCount()) * distanceCostPolicy.getFactor(context.getDistance());
    }

    @Override
    public Map<String, Object> computerDescription() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", settingsName());
        data.put("type", getClass().getName());
        data.put("baseCooldown", cooldown.get());
        data.put("baseCost", cost.get());
        data.put("distanceCooldownPolicy", distanceCooldownPolicy.name().toLowerCase());
        data.put("countCooldownPolicy", countCooldownPolicy.name().toLowerCase());
        data.put("distanceCostPolicy", distanceCostPolicy.name().toLowerCase());
        data.put("countCostPolicy", countCostPolicy.name().toLowerCase());
        return data;
    }

    @Override
    public void addToConfig(ForgeConfigSpec.Builder builder) {
        cooldown = builder.defineInRange(settingsName() + "Cooldown", defaultCooldown, 1_000, Integer.MAX_VALUE);
        cost = builder.defineInRange(settingsName() + "Cost", defaultCost, 0, Integer.MAX_VALUE);
    }

    public enum DistancePolicy {
        IGNORED(d -> 1),
        SQRT(d -> (int) Math.sqrt(d));

        private final UnaryOperator<Integer> factorFunction;

        DistancePolicy(UnaryOperator<Integer> factorFunction) {
            this.factorFunction = factorFunction;
        }

        public int getFactor(int distance) {
            return factorFunction.apply(distance);
        }
    }

    public enum CountPolicy {
        IGNORED(c -> 1),
        MULTIPLY(c -> c);

        private final UnaryOperator<Integer> factorFunction;

        CountPolicy(UnaryOperator<Integer> factorFunction) {
            this.factorFunction = factorFunction;
        }

        public int getFactor(int count) {
            return factorFunction.apply(count);
        }
    }
}
