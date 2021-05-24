package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import mekanism.common.content.matrix.MatrixMultiblockData;
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
        return "inductionMatrix";
    }

    @LuaFunction
    public final long getEnergy() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix().getEnergyContainer().getEnergy());
    }

    @LuaFunction
    public final long getLastInput() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix().getEnergyContainer().getLastInput());
    }

    @LuaFunction
    public final long getLastOutput() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix().getEnergyContainer().getLastOutput());
    }

    @LuaFunction
    public final long getEnergyNeeded() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix().getEnergyContainer().getNeeded());
    }

    @LuaFunction
    public final double getEnergyFilledPercentage() {
        return getEnergy() / (double) getMaxEnergy();
    }

    @LuaFunction
    public final long getTransferCap() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix().getTransferCap());
    }

    @LuaFunction
    public final int getInstalledCells() {
        return getMatrix().getCellCount();
    }

    @LuaFunction
    public final int getInstalledProviders() {
        return getMatrix().getProviderCount();
    }

    @LuaFunction
    public final long getMaxEnergy() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix().getEnergyContainer().getMaxEnergy());
    }

    private MatrixMultiblockData getMatrix() {
        return getTileEntity().getMultiblock();
    }
}
