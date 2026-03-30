package de.srendi.advancedperipherals.common.addons.computercraft.operations;

import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
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
        data.put("type", "simple_free_operation");
        data.put("defaultCooldown", this.cooldown.get());
        return data;
    }
}
