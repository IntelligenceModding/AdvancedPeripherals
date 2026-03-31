package de.srendi.advancedperipherals.common.addons.computercraft.operations;

import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntUnaryOperator;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;

public enum SingleOperation implements IPeripheralOperation<SingleOperationContext> {
    DIG(1000, 1),
    USE_ON_BLOCK(5000, 1),
    UPDATE_BLOCK(500, 1),
    SUCK(1000, 1),
    USE_ON_ANIMAL(2500, 10),
    CAPTURE_ANIMAL(50_000, 100),
    WARP(1000, DistancePolicy.IGNORED, CountPolicy.MULTIPLY, 1, DistancePolicy.SQRT, CountPolicy.MULTIPLY),
    ACCURE_PLACE(1000, DistancePolicy.IGNORED, CountPolicy.MULTIPLY, 1, DistancePolicy.LINEAR, CountPolicy.MULTIPLY),
    PREPARE_PORTAL(3_000, 600),
    ACTIVE_PORTAL(60_000, 1),
    MOUNT_SHIP(1000, 1);

    private final int defaultCooldown;
    private final DistancePolicy distanceCooldownPolicy;
    private final CountPolicy countCooldownPolicy;
    private final int defaultCost;
    private final DistancePolicy distanceCostPolicy;
    private final CountPolicy countCostPolicy;
    private ModConfigSpec.IntValue cooldown;
    private ModConfigSpec.IntValue cost;

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
    public MethodResult getCostLua(IArguments args) throws LuaException {
        return MethodResult.of(this.getCost(new SingleOperationContext(args.getInt(0), args.getInt(1))));
    }

    @Override
    public Map<String, Object> computerDescription() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", settingsName());
        data.put("type", "single_operation");
        data.put("baseCooldown", cooldown.get());
        data.put("baseCost", cost.get());
        data.put("distanceCooldownPolicy", distanceCooldownPolicy.getName());
        data.put("countCooldownPolicy", countCooldownPolicy.getName());
        data.put("distanceCostPolicy", distanceCostPolicy.getName());
        data.put("countCostPolicy", countCostPolicy.getName());
        return data;
    }

    @Override
    public void addToConfig(ModConfigSpec.Builder builder) {
        builder.push(settingsName());
        cooldown = builder.defineInRange("Cooldown", defaultCooldown, 0, Integer.MAX_VALUE);
        cost = builder.defineInRange("Cost", defaultCost, 0, Integer.MAX_VALUE);
        builder.pop();
    }

    public enum DistancePolicy {
        IGNORED("ignored", d -> 1),
        LINEAR("linear", d -> d),
        SQRT("sqrt", d -> (int) Math.sqrt(d));

        private final String name;
        private final IntUnaryOperator factorFunction;

        DistancePolicy(String name, IntUnaryOperator factorFunction) {
            this.name = name;
            this.factorFunction = factorFunction;
        }

        public String getName() {
            return this.name;
        }

        public int getFactor(int distance) {
            return factorFunction.applyAsInt(distance);
        }
    }

    public enum CountPolicy {
        IGNORED("ignored", c -> 1),
        MULTIPLY("multiply", c -> c);

        private final String name;
        private final IntUnaryOperator factorFunction;

        CountPolicy(String name, IntUnaryOperator factorFunction) {
            this.name = name;
            this.factorFunction = factorFunction;
        }

        public String getName() {
            return this.name;
        }

        public int getFactor(int count) {
            return factorFunction.applyAsInt(count);
        }
    }
}
