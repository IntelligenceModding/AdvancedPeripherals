package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.immersiveengineering;

import blusunrize.immersiveengineering.common.blocks.metal.CapacitorTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;

public class CapacitorIntegration extends ProxyIntegration<CapacitorTileEntity> {
    @Override
    protected Class<CapacitorTileEntity> getTargetClass() {
        return CapacitorTileEntity.class;
    }

    @Override
    public CapacitorIntegration getNewInstance() {
        return new CapacitorIntegration();
    }

    @Override
    protected String getName() {
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
