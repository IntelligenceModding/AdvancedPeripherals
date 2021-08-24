package de.srendi.advancedperipherals.common.addons.computercraft.integrations.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.peripheral.generic.data.DataHelpers;
import de.srendi.advancedperipherals.common.addons.computercraft.base.Integration;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.command.builders.Builders;
import mekanism.common.content.evaporation.EvaporationMultiblockData;
import mekanism.common.tile.multiblock.TileEntityThermalEvaporationValve;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EvaporationIntegration extends Integration<TileEntityThermalEvaporationValve> {

    @Override
    protected Class<TileEntityThermalEvaporationValve> getTargetClass() {
        return TileEntityThermalEvaporationValve.class;
    }

    @Override
    public Integration<?> getNewInstance() {
        return new EvaporationIntegration();
    }

    @NotNull
    @Override
    public String getType() {
        return "evaporationTower";
    }

    @LuaFunction
    public final double getHeat() {
        return getMultiblock().getTemp();
    }

    @LuaFunction
    public final int getHeight() {
        return getMultiblock().height();
    }

    @LuaFunction
    public final double getProduction() {
        return (double)Math.round(getMultiblock().lastGain * 100.0D) / 100.0D;
    }

    @LuaFunction
    public final Map<String, Object> getInputTank() {
        Map<String, Object> result = new HashMap<>(3);
        BasicFluidTank inputTank = getMultiblock().inputTank;
        result.put("name", inputTank.getFluidAmount() > 0 ? inputTank.getFluid().getFluid().getRegistryName().toString() : null);
        result.put("amount", inputTank.getFluidAmount());
        return result;
    }

    @LuaFunction
    public final Map<String, Object> getOutputTank() {
        Map<String, Object> result = new HashMap<>(3);
        BasicFluidTank outputTank = getMultiblock().outputTank;
        result.put("name", outputTank.getFluidAmount() > 0 ? outputTank.getFluid().getFluid().getRegistryName().toString() : null);
        result.put("amount", outputTank.getFluidAmount());
        return result;
    }

    private EvaporationMultiblockData getMultiblock() {
        return getTileEntity().getMultiblock();
    }
}
