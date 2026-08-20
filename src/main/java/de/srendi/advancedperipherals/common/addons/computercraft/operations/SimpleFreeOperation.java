package de.srendi.advancedperipherals.common.addons.computercraft.operations;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public enum SimpleFreeOperation implements IPeripheralOperation<Void> {
    CHAT_MESSAGE(1000),
    SADDLE_CAPTURE(5000);

    private final int defaultCooldown;
    private ForgeConfigSpec.IntValue cooldown;

    SimpleFreeOperation(int defaultCooldown) {
        this.defaultCooldown = defaultCooldown;
    }

    @Override
    public void addToConfig(ForgeConfigSpec.Builder builder) {
        cooldown = builder.defineInRange(settingsName() + "Cooldown", defaultCooldown, 0, Integer.MAX_VALUE);
    }

    @Override
    public int getInitialCooldown() {
        return cooldown.get();
    }

    @Override
    public int getCooldown(Void context) {
        return cooldown.get();
    }

    @Override
    public int getCost(Void context) {
        return 0;
    }

    @Override
    public MethodResult getCostLua(IArguments args) throws LuaException {
        return MethodResult.of(0);
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
