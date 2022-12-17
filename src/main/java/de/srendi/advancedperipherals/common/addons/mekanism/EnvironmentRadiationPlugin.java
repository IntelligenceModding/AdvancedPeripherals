package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralPlugin;

public class EnvironmentRadiationPlugin implements IPeripheralPlugin {
    private final IPeripheralOwner owner;

    public EnvironmentRadiationPlugin(IPeripheralOwner owner) {
        this.owner = owner;
    }

    @LuaFunction(mainThread = true)
    public final Object getRadiation() {
        return Mekanism.getRadiation(owner.getLevel(), owner.getPos());
    }

    @LuaFunction(mainThread = true)
    public final double getRadiationRaw() {
        return Mekanism.getRadiationRaw(owner.getLevel(), owner.getPos());
    }

}
