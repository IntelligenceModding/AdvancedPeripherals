package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import owmii.powah.block.magmator.MagmatorTile;

public class MagmatorIntegration implements APGenericPeripheral {

    @Override
    public String getPeripheralType() {
        return "magmator";
    }

    @LuaFunction(mainThread = true)
    public final double getStoredEnergy(MagmatorTile blockEntity) {
        return blockEntity.getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergy(MagmatorTile blockEntity) {
        return blockEntity.getEnergy().getMaxEnergyStored();
    }

    @LuaFunction
    public final boolean isBurning(MagmatorTile blockEntity) {
        return blockEntity.isBurning();
    }

    // getTank is thread safe
    @LuaFunction
    public final Object getFuelTank(MagmatorTile blockEntity) {
        return LuaConverter.fluidStackToLua(FluidStackHooksForge.toForge(blockEntity.getTank().getFluid()));
    }
}
