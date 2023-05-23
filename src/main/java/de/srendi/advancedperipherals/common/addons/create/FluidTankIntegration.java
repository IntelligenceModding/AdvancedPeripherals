package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FluidTankIntegration extends BlockEntityIntegrationPeripheral<FluidTankBlockEntity> {

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
        Map<String, Object> data = new HashMap<>();
        data.put("capacity", blockEntity.getControllerBE().getTankInventory().getCapacity());
        data.put("amount", blockEntity.getControllerBE().getTankInventory().getFluidAmount());
        data.put("fluid", blockEntity.getControllerBE().getTankInventory().getFluid().getFluid().getRegistryName().toString());
        data.put("isBoiler", blockEntity.getControllerBE().boiler.isActive());
        return data;
    }
}
