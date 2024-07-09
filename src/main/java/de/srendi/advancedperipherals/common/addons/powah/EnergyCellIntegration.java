package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import owmii.powah.block.energycell.EnergyCellTile;

public class EnergyCellIntegration implements APGenericPeripheral {

    @Override
    public String getPeripheralType() {
        return "energyCell";
    }

    @LuaFunction(mainThread = true)
    public final String getName() {
        return "Energy Cell";
    }

    // TODO: remove in the next major version
    @Deprecated(forRemoval = true, since = "1.20.1-0.7.41r")
    @LuaFunction(mainThread = true)
    public final double getEnergy(EnergyCellTile blockEntity) {
        return blockEntity.getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getStoredEnergy(EnergyCellTile blockEntity) {
        return blockEntity.getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergy(EnergyCellTile blockEntity) {
        return blockEntity.getEnergy().getMaxEnergyStored();
    }
}
