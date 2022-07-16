package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BlazeBurnerIntegration extends BlockEntityIntegrationPeripheral<BlazeBurnerTileEntity> {

    public BlazeBurnerIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "blazeBurner";
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getInfo() {
        return new HashMap<>() {{
            put("fuelType", blockEntity.getActiveFuel().toString().toLowerCase());
            put("heatLevel", blockEntity.getHeatLevelFromBlock().getSerializedName());
            put("remainingBurnTime", blockEntity.getRemainingBurnTime());
            put("isCreative", blockEntity.isCreative());
        }};
    }

}
