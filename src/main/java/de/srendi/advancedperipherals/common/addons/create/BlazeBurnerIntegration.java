package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

public class BlazeBurnerIntegration implements APGenericPeripheral {
    @Override
    @NotNull
    public String getPeripheralType() {
        return "blaze_burner";
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> info(BlazeBurnerBlockEntity blockEntity) {
        return Map.of(
            "fuelType", blockEntity.getActiveFuel().toString().toLowerCase(Locale.ROOT),
            "heatLevel", blockEntity.getHeatLevelFromBlock().getSerializedName(),
            "remainingBurnTime", blockEntity.getRemainingBurnTime(),
            "isCreative", blockEntity.isCreative()
        );
    }
}
