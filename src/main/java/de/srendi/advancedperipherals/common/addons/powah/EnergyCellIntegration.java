package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import owmii.powah.block.energycell.EnergyCellTile;

public class EnergyCellIntegration implements APGenericPeripheral {

    @Override
    public String getPeripheralType() {
        return "energy_cell";
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
