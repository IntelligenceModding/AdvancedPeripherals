package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BlazeBurnerIntegration extends BlockEntityIntegrationPeripheral<BlazeBurnerBlockEntity> {

    public BlazeBurnerIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "blaze_burner";
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getInfo() {
        Map<String, Object> data = new HashMap<>();
        data.put("fuelType", blockEntity.getActiveFuel().toString().toLowerCase());
        data.put("heatLevel", blockEntity.getHeatLevelFromBlock().getSerializedName());
        data.put("remainingBurnTime", blockEntity.getRemainingBurnTime());
        data.put("isCreative", blockEntity.isCreative());
        return data;
    }

}
