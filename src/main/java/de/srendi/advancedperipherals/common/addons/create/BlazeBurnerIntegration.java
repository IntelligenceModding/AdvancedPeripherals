package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BlazeBurnerIntegration implements APGenericPeripheral {
    @NotNull
    @Override
    public String getPeripheralType() {
        return "blazeBurner";
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getInfo(BlazeBurnerBlockEntity blockEntity) {
        Map<String, Object> data = new HashMap<>();
        data.put("fuelType", blockEntity.getActiveFuel().toString().toLowerCase());
        data.put("heatLevel", blockEntity.getHeatLevelFromBlock().getSerializedName());
        data.put("remainingBurnTime", blockEntity.getRemainingBurnTime());
        data.put("isCreative", blockEntity.isCreative());
        return data;
    }

}
