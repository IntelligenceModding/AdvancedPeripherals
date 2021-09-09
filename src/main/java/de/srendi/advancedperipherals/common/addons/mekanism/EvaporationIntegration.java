package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.content.evaporation.EvaporationMultiblockData;
import mekanism.common.tile.multiblock.TileEntityThermalEvaporationValve;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EvaporationIntegration extends TileEntityIntegrationPeripheral<TileEntityThermalEvaporationValve> {

    public EvaporationIntegration(TileEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "evaporationTower";
    }

    @LuaFunction(mainThread = true)
    public final double getHeat() {
        return getMultiblock().getTemp();
    }

    @LuaFunction(mainThread = true)
    public final int getHeight() {
        return getMultiblock().height();
    }

    @LuaFunction(mainThread = true)
    public final double getProduction() {
        return (double)Math.round(getMultiblock().lastGain * 100.0D) / 100.0D;
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getInputTank() {
        Map<String, Object> result = new HashMap<>(3);
        BasicFluidTank inputTank = getMultiblock().inputTank;
        result.put("name", inputTank.getFluidAmount() > 0 ? inputTank.getFluid().getFluid().getRegistryName().toString() : null);
        result.put("amount", inputTank.getFluidAmount());
        return result;
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getOutputTank() {
        Map<String, Object> result = new HashMap<>(3);
        BasicFluidTank outputTank = getMultiblock().outputTank;
        result.put("name", outputTank.getFluidAmount() > 0 ? outputTank.getFluid().getFluid().getRegistryName().toString() : null);
        result.put("amount", outputTank.getFluidAmount());
        return result;
    }

    private EvaporationMultiblockData getMultiblock() {
        return tileEntity.getMultiblock();
    }
}
