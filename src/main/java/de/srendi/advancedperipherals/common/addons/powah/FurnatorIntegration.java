package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import owmii.powah.block.furnator.FurnatorTile;

public class FurnatorIntegration implements APGenericPeripheral {

    @Override
    public String getPeripheralType() {
        return "furnator";
    }

    @LuaFunction
    public final boolean isBurning(FurnatorTile blockEntity) {
        return blockEntity.isBurning();
    }

    @LuaFunction(mainThread = true)
    public final double getStoredEnergy(FurnatorTile blockEntity) {
        return blockEntity.getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergy(FurnatorTile blockEntity) {
        return blockEntity.getEnergy().getMaxEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getCarbon(FurnatorTile blockEntity) {
        // Technically getCarbon is thread safe, but perCent is not atomic
        return blockEntity.getCarbon().perCent();
    }

    @LuaFunction(mainThread = true)
    public final Object getFuelSlot(FurnatorTile blockEntity) {
        return LuaConverter.itemStackToLua(blockEntity.getInventory().getStackInSlot(1));
    }
}
