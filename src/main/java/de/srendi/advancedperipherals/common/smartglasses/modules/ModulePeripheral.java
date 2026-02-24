package de.srendi.advancedperipherals.common.smartglasses.modules;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import org.jetbrains.annotations.NotNull;

public class ModulePeripheral extends BasePeripheral<ModulePeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "smart_glasses";

    public ModulePeripheral(SmartGlassesComputer computer) {
        super(PERIPHERAL_TYPE, new ModulePeripheralOwner(computer));
    }

    public void updateModules() {
        // We need to set the initialization to false so the dynamic peripheral re-builds the plugins
        clearAllPlugins();

        SmartGlassesComputer computer = getPeripheralOwner().getComputer();
        SmartGlassesSideAccess smartGlassesModuleAccess = computer.getSmartGlassesModuleAccess();
        for (IModule module : computer.getModules()) {
            IModuleFunctions functions = module.getFunctions(smartGlassesModuleAccess);
            if (functions != null) {
                addPlugin(functions);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @LuaFunction(mainThread = true)
    public final String[] getModules() {
        return getPeripheralOwner().getComputer().getModules().stream().map(module -> module.getName().toString()).toArray(String[]::new);
    }

    @LuaFunction(mainThread = true)
    public final boolean hasModule(@NotNull String module) {
        return getPeripheralOwner().getComputer().getModules().stream().anyMatch(m -> m.getName().toString().equals(module));
    }
}
