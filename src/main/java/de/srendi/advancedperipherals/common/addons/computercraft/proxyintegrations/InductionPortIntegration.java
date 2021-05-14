package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.tile.multiblock.TileEntityInductionPort;

public class InductionPortIntegration extends ProxyIntegration<TileEntityInductionPort> {
    @Override
    protected Class<TileEntityInductionPort> getTargetClass() {
        return TileEntityInductionPort.class;
    }

    @Override
    public InductionPortIntegration getNewInstance() {
        return new InductionPortIntegration();
    }

    @Override
    protected String getName() {
        return "inductionMaxtrix";
    }

    @LuaFunction
    public final long getEnergy() {
        return getTileEntity().getEnergy(0).getValue();
    }

    @LuaFunction
    public final long getInputRate() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getTileEntity().getMultiblock().getLastInput());
    }

    @LuaFunction
    public final long getOutputRate() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getTileEntity().getMultiblock().getLastOutput());
    }

    @LuaFunction
    public final long getEnergyNeeded() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getTileEntity().getMultiblock().getNeededEnergy(0));
    }

    @LuaFunction
    public final double getEnergyFilledPercentage() {
        return getEnergy() / (double) getMaxEnergy();
    }

    @LuaFunction
    public final long getTransferCap() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getTileEntity().getMultiblock().getTransferCap());
    }

    @LuaFunction
    public final int getInstalledCells() {
        return getTileEntity().getMultiblock().getCellCount();
    }

    @LuaFunction
    public final int getInstalledProviders() {
        return getTileEntity().getMultiblock().getProviderCount();
    }

    @LuaFunction
    public final long getMaxEnergy() {
        return getTileEntity().getMaxEnergy(0).getValue();
    }

}
