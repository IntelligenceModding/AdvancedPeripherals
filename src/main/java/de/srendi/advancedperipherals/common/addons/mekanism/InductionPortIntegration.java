package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.common.content.matrix.MatrixMultiblockData;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.tile.multiblock.TileEntityInductionPort;
import net.minecraft.tileentity.TileEntity;

public class InductionPortIntegration extends TileEntityIntegrationPeripheral<TileEntityInductionPort> {

    public InductionPortIntegration(TileEntity entity) {
        super(entity);
    }

    @Override
    public String getType() {
        return "inductionMatrix";
    }

    @LuaFunction(mainThread = true)
    public final long getEnergy() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix().getEnergyContainer().getEnergy());
    }

    @LuaFunction(mainThread = true)
    public final long getLastInput() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix().getEnergyContainer().getLastInput());
    }

    @LuaFunction(mainThread = true)
    public final long getLastOutput() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix().getEnergyContainer().getLastOutput());
    }

    @LuaFunction(mainThread = true)
    public final long getEnergyNeeded() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix().getEnergyContainer().getNeeded());
    }

    @LuaFunction(mainThread = true)
    public final double getEnergyFilledPercentage() {
        return getEnergy() / (double) getMaxEnergy();
    }

    @LuaFunction(mainThread = true)
    public final long getTransferCap() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix().getTransferCap());
    }

    @LuaFunction(mainThread = true)
    public final int getInstalledCells() {
        return getMatrix().getCellCount();
    }

    @LuaFunction(mainThread = true)
    public final int getInstalledProviders() {
        return getMatrix().getProviderCount();
    }

    @LuaFunction(mainThread = true)
    public final long getMaxEnergy() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix().getEnergyContainer().getMaxEnergy());
    }

    private MatrixMultiblockData getMatrix() {
        return tileEntity.getMultiblock();
    }
}
