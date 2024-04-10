package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import owmii.powah.block.solar.SolarTile;

public class SolarPanelIntegration implements APGenericPeripheral {

    @Override
    public String getPeripheralType() {
        return "solarPanel";
    }

    @LuaFunction(mainThread = true)
    public final String getName(SolarTile blockEntity) {
        return "Solar Panel";
    }

    @LuaFunction(mainThread = true)
    public final double getEnergy(SolarTile blockEntity) {
        return blockEntity.getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergy(SolarTile blockEntity) {
        return blockEntity.getEnergy().getMaxEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final boolean canSeeSky(SolarTile blockEntity) {
        return blockEntity.canSeeSky();
    }

}
