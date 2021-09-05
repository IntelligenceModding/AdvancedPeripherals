package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BlazeBurnerIntegration extends TileEntityIntegrationPeripheral<BlazeBurnerTileEntity> {

    public BlazeBurnerIntegration(TileEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "blazeBurner";
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getInfo() {
        return new HashMap<String, Object>() {{
            put("fuelType", tileEntity.getActiveFuel().toString().toLowerCase());
            put("heatLevel", tileEntity.getHeatLevelFromBlock().getSerializedName());
            put("remainingBurnTime", tileEntity.getRemainingBurnTime());
            put("isCreative", tileEntity.isCreative());
        }};
    }
}
