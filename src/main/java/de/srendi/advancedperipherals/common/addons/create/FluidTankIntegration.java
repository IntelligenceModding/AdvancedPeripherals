package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.contraptions.fluids.tank.FluidTankTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FluidTankIntegration extends BlockEntityIntegrationPeripheral<FluidTankTileEntity> {

    public FluidTankIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "fluidTank";
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getInfo() {
        return new HashMap<>() {{
            put("capacity", blockEntity.getTankInventory().getCapacity());
            put("amount", blockEntity.getTankInventory().getFluidAmount());
            put("fluid", blockEntity.getTankInventory().getFluid().getFluid().getRegistryName().toString());
            put("isBoiler", blockEntity.boiler.isActive());
        }};
    }
}
