package de.srendi.advancedperipherals.common.smartglasses.modules;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;

public class ModulePeripheral extends BasePeripheral<ModulePeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "smartGlasses";

    public ModulePeripheral(SmartGlassesComputer computer) {
        super(PERIPHERAL_TYPE, new ModulePeripheralOwner(computer));
        getPeripheralOwner().getComputer().getModules().values().forEach(module -> {
            IModuleFunctions functions = module.getFunctions(computer.getSmartGlassesAccess());
            if (functions != null)
                addPlugin(functions);
        });
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @LuaFunction(mainThread = true)
    public final String[] getModules() {
        return getPeripheralOwner().getComputer().getModules().values().stream().map(module -> module.getName().toString()).toArray(String[]::new);
    }
}
