package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import owmii.powah.block.ender.EnderCellTile;

public class EnderCellIntegration implements APGenericPeripheral {

    @Override
    public String getPeripheralType() {
        return "enderCell";
    }

    // TODO: remove in the next major version
    @Deprecated(forRemoval = true, since = "1.20.1-0.7.41r")
    @LuaFunction(mainThread = true)
    public final double getEnergy(EnderCellTile blockEntity) {
        return blockEntity.getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getStoredEnergy(EnderCellTile blockEntity) {
        return blockEntity.getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergy(EnderCellTile blockEntity) {
        return blockEntity.getEnergy().getMaxEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final int getChannel(EnderCellTile blockEntity) {
        // Lua, and generally slots in MC, seem to be 1 based, make the conversion here
        int channel = blockEntity.getChannel().get();
        return channel + 1;
    }

    @LuaFunction(mainThread = true)
    public final void setChannel(EnderCellTile blockEntity, int channel) {
        // Lua, and generally slots in MC, seem to be 1 based, make the conversion here
        channel = channel - 1;
        blockEntity.getChannel().set(channel);
    }

    @LuaFunction(mainThread = true)
    public final int getMaxChannels(EnderCellTile blockEntity) {
        return blockEntity.getMaxChannels();
    }
}
