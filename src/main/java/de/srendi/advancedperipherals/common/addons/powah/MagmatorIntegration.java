package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import org.jetbrains.annotations.NotNull;
import owmii.powah.block.magmator.MagmatorTile;

public class MagmatorIntegration implements APGenericPeripheral {

    @NotNull
    @Override
    public String getPeripheralType() {
        return "magmator";
    }

    // TODO: remove in the next major version
    @Deprecated(forRemoval = true, since = "1.20.1-0.7.41r")
    @LuaFunction(mainThread = true)
    public final double getEnergy(MagmatorTile blockEntity) {
        return blockEntity.getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getStoredEnergy(MagmatorTile blockEntity) {
        return blockEntity.getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergy(MagmatorTile blockEntity) {
        return blockEntity.getEnergy().getMaxEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final boolean isBurning(MagmatorTile blockEntity) {
        return blockEntity.isBurning();
    }

    @LuaFunction(mainThread = true)
    public final long getTankCapacity(MagmatorTile blockEntity) {
        return blockEntity.getTank().getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getFluidInTank(MagmatorTile blockEntity) {
        return blockEntity.getTank().getFluidAmount();
    }
}
