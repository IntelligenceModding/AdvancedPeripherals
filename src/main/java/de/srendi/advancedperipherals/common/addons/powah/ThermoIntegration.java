package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import owmii.powah.block.thermo.ThermoTile;

public class ThermoIntegration implements APGenericPeripheral {

    @Override
    public String getPeripheralType() {
        return "thermo";
    }

    @LuaFunction(mainThread = true)
    public final double getStoredEnergy(ThermoTile blockEntity) {
        return blockEntity.getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergy(ThermoTile blockEntity) {
        return blockEntity.getEnergy().getMaxEnergyStored();
    }

    @LuaFunction
    public final Object getCoolantTank(ThermoTile blockEntity) {
        return LuaConverter.fluidStackToObject(blockEntity.getTank().getFluid());
    }
}
