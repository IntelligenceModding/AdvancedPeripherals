package de.srendi.advancedperipherals.common.addons.immersiveengineering;

import blusunrize.immersiveengineering.common.blocks.metal.CapacitorTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import net.minecraft.tileentity.TileEntity;

public class CapacitorIntegration extends TileEntityIntegrationPeripheral<CapacitorTileEntity> {

    public CapacitorIntegration(TileEntity entity) {
        super(entity);
    }

    @Override
    public String getType() {
        return "IECapacitor";
    }

    @LuaFunction(mainThread = true)
    public final int getStoredEnergy() {
        return tileEntity.getFluxStorage().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final int getMaxEnergy() {
        return tileEntity.getFluxStorage().getMaxEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final int getEnergyNeeded() {
        return getMaxEnergy() - getStoredEnergy();
    }

    @LuaFunction(mainThread = true)
    public final double getEnergyFilledPercentage() {
        return getStoredEnergy() / (double) getMaxEnergy();
    }
}
