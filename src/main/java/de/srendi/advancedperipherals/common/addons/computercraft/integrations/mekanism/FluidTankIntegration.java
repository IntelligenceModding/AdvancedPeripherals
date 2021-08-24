package de.srendi.advancedperipherals.common.addons.computercraft.integrations.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.peripheral.generic.data.DataHelpers;
import de.srendi.advancedperipherals.common.addons.computercraft.base.Integration;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.common.capabilities.fluid.FluidTankFluidTank;
import mekanism.common.tile.TileEntityChemicalTank;
import mekanism.common.tile.TileEntityFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.HashMap;
import java.util.Map;

public class FluidTankIntegration extends Integration<TileEntityFluidTank> {

    @Override
    protected Class<TileEntityFluidTank> getTargetClass() {
        return TileEntityFluidTank.class;
    }

    @Override
    public FluidTankIntegration getNewInstance() {
        return new FluidTankIntegration();
    }

    @Override
    public String getType() {
        return "chemicalTank";
    }

    @LuaFunction
    public final Map<String, Object> getStored() {
        Map<String, Object> result = new HashMap<>(3);
        result.put("name", DataHelpers.getId(getTank().getFluid().getFluid()));
        result.put("amount", getTank().getFluidAmount());
        return result;
    }

    @LuaFunction
    public final String getTier() {
        return getTileEntity().tier.name();
    }

    @LuaFunction
    public final long getCapacity() {
        return getTank().getCapacity();
    }

    @LuaFunction
    public final double getFilledPercentage() {
        return getTank().getFluidAmount() / (double) getCapacity();
    }

    @LuaFunction
    public final long getNeeded() {
        return getTank().getNeeded();
    }

    private FluidTankFluidTank getTank() {
        TileEntityFluidTank mergedTank = getTileEntity();
        return mergedTank.fluidTank;
    }

}
