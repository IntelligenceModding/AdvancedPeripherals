package de.srendi.advancedperipherals.common.addons.computercraft.integrations.immersiveengineering;

import blusunrize.immersiveengineering.common.blocks.metal.CapacitorTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.Integration;

public class CapacitorIntegration extends Integration<CapacitorTileEntity> {
    @Override
    protected Class<CapacitorTileEntity> getTargetClass() {
        return CapacitorTileEntity.class;
    }

    @Override
    public CapacitorIntegration getNewInstance() {
        return new CapacitorIntegration();
    }

    @Override
    public String getType() {
        return "IECapacitor";
    }

    @LuaFunction
    public final int getStoredEnergy() {
        return getTileEntity().getFluxStorage().getEnergyStored();
    }

    @LuaFunction
    public final int getMaxEnergy() {
        return getTileEntity().getFluxStorage().getMaxEnergyStored();
    }

    @LuaFunction
    public final int getEnergyNeeded() {
        return getMaxEnergy() - getStoredEnergy();
    }

    @LuaFunction
    public final double getEnergyFilledPercentage() {
        return getStoredEnergy() / (double) getMaxEnergy();
    }
}
