package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import owmii.powah.block.thermo.ThermoTile;

public class ThermoIntegration implements APGenericPeripheral {

    @Override
    public String getPeripheralType() {
        return "thermo";
    }

    @LuaFunction(mainThread = true)
    public final String getName(ThermoTile blockEntity) {
        return "Thermo generator";
    }

    @LuaFunction(mainThread = true)
    public final double getEnergy(ThermoTile blockEntity) {
        return blockEntity.getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergy(ThermoTile blockEntity) {
        return blockEntity.getEnergy().getMaxEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getCoolantInTank(ThermoTile blockEntity) {
        return blockEntity.getTank().getFluidAmount();
    }

}
