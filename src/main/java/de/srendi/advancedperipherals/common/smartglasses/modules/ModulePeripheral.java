package de.srendi.advancedperipherals.common.smartglasses.modules;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;

import java.util.ArrayList;
import java.util.List;

public class ModulePeripheral extends BasePeripheral<ModulePeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "smartGlasses";

    public ModulePeripheral(SmartGlassesComputer computer) {
        super(PERIPHERAL_TYPE, new ModulePeripheralOwner(computer));
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @LuaFunction
    public final String[] getModules() {
        List<String> modules = new ArrayList<>(getPeripheralOwner().getComputer().getModules().size());
        getPeripheralOwner().getComputer().getModules().forEach(module -> modules.add(module.getName().toString()));
        return modules.toArray(new String[0]);
    }
}
