package de.srendi.advancedperipherals.common.addons.computercraft.integrations.immersiveengineering;

import blusunrize.immersiveengineering.common.blocks.metal.CapacitorTileEntity;
import dan200.computercraft.api.lua.GenericSource;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CapacitorIntegration implements GenericSource {

    @NotNull
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "IECapacitor");
    }

    @LuaFunction
    public static int getStoredEnergy(CapacitorTileEntity tileEntity) {
        return tileEntity.getFluxStorage().getEnergyStored();
    }

    @LuaFunction
    public static int getMaxEnergy(CapacitorTileEntity tileEntity) {
        return tileEntity.getFluxStorage().getMaxEnergyStored();
    }

    @LuaFunction
    public static int getEnergyNeeded(CapacitorTileEntity tileEntity) {
        return getMaxEnergy(tileEntity) - getStoredEnergy(tileEntity);
    }

    @LuaFunction
    public static double getEnergyFilledPercentage(CapacitorTileEntity tileEntity) {
        return getStoredEnergy(tileEntity) / (double) getMaxEnergy(tileEntity);
    }
}
