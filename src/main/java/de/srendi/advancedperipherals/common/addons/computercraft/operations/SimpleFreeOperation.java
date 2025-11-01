package de.srendi.advancedperipherals.common.addons.computercraft.operations;

import de.srendi.advancedperipherals.common.setup.DataComponents;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.Map;

public enum SimpleFreeOperation implements IPeripheralOperation<Object> {
    CHAT_MESSAGE(1000),
    SADDLE_CAPTURE(5000);

    private final int defaultCooldown;
    private ModConfigSpec.IntValue cooldown;

    SimpleFreeOperation(int defaultCooldown) {
        this.defaultCooldown = defaultCooldown;
    }

    @Override
    public void addToConfig(ModConfigSpec.Builder builder) {
        cooldown = builder.defineInRange(settingsName() + "Cooldown", defaultCooldown, 0, Integer.MAX_VALUE);
    }

    @Override
    public int getInitialCooldown() {
        return cooldown.get();
    }

    @Override
    public int getCooldown(Object context) {
        return cooldown.get();
    }

    @Override
    public int getCost(Object context) {
        return 0;
    }

    @Override
    public Map<String, Object> computerDescription() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", settingsName());
        data.put("type", getClass().getSimpleName());
        data.put("cooldown", cooldown.get());
        return data;
    }

    @Override
    public DataComponentType<Long> dataComponentType() {
        return DataComponents.SIMPLE_FREE_OPERATION.get();
    }
}
