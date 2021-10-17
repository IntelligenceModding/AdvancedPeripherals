package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.peripheral.generic.data.DataHelpers;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.common.capabilities.fluid.FluidTankFluidTank;
import mekanism.common.tile.TileEntityFluidTank;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.Map;

public class FluidTankIntegration extends TileEntityIntegrationPeripheral<TileEntityFluidTank> {

    public FluidTankIntegration(TileEntity entity) {
        super(entity);
    }

    @Override
    public String getType() {
        return "chemicalTank";
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getStored() {
        Map<String, Object> result = new HashMap<>(3);
        result.put("name", DataHelpers.getId(getTank().getFluid().getFluid()));
        result.put("amount", getTank().getFluidAmount());
        return result;
    }

    @LuaFunction(mainThread = true)
    public final String getTier() {
        return tileEntity.tier.name();
    }

    @LuaFunction(mainThread = true)
    public final long getCapacity() {
        return getTank().getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final double getFilledPercentage() {
        return getTank().getFluidAmount() / (double) getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getNeeded() {
        return getTank().getNeeded();
    }

    private FluidTankFluidTank getTank() {
        return tileEntity.fluidTank;
    }

}
