package de.srendi.advancedperipherals.common.addons.computercraft.integrations.mekanism;

import dan200.computercraft.api.lua.GenericSource;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import mekanism.common.content.matrix.MatrixMultiblockData;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.tile.multiblock.TileEntityInductionPort;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class InductionPortIntegration implements GenericSource {

    @NotNull
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "inductionMatrix");
    }

    @LuaFunction
    public static long getEnergy(TileEntityInductionPort tileEntity) {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix(tileEntity).getEnergyContainer().getEnergy());
    }

    @LuaFunction
    public static long getLastInput(TileEntityInductionPort tileEntity) {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix(tileEntity).getEnergyContainer().getLastInput());
    }

    @LuaFunction
    public static long getLastOutput(TileEntityInductionPort tileEntity) {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix(tileEntity).getEnergyContainer().getLastOutput());
    }

    @LuaFunction
    public static long getEnergyNeeded(TileEntityInductionPort tileEntity) {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix(tileEntity).getEnergyContainer().getNeeded());
    }

    @LuaFunction
    public static double getEnergyFilledPercentage(TileEntityInductionPort tileEntity) {
        return getEnergy(tileEntity) / (double) getMaxEnergy(tileEntity);
    }

    @LuaFunction
    public static long getTransferCap(TileEntityInductionPort tileEntity) {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix(tileEntity).getTransferCap());
    }

    @LuaFunction
    public static int getInstalledCells(TileEntityInductionPort tileEntity) {
        return getMatrix(tileEntity).getCellCount();
    }

    @LuaFunction
    public static int getInstalledProviders(TileEntityInductionPort tileEntity) {
        return getMatrix(tileEntity).getProviderCount();
    }

    @LuaFunction
    public static long getMaxEnergy(TileEntityInductionPort tileEntity) {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getMatrix(tileEntity).getEnergyContainer().getMaxEnergy());
    }

    private static MatrixMultiblockData getMatrix(TileEntityInductionPort tileEntity) {
        return tileEntity.getMultiblock();
    }
}
