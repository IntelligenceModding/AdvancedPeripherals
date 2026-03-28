package de.srendi.advancedperipherals.common.smartglasses.modules;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class ModulePeripheral extends BasePeripheral<ModulePeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "smart_glasses";

    private volatile List<ResourceLocation> modules = List.of();

    public ModulePeripheral(SmartGlassesComputer computer) {
        super(PERIPHERAL_TYPE, new ModulePeripheralOwner(computer));
    }

    public void updateModules(Collection<@NotNull IModule> modules) {
        // We need to set the initialization to false so the dynamic peripheral re-builds the plugins
        this.initialized = false;

        SmartGlassesSideAccess smartGlassesModuleAccess = getPeripheralOwner().getComputer().getSmartGlassesModuleAccess();
        for (IModule module : modules) {
            IModuleFunctions functions = module.getFunctions(smartGlassesModuleAccess);
            if (functions != null) {
                addPlugin(functions);
            }
        }
        this.modules = modules.stream().map(IModule::getId).sorted().toList();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @LuaFunction
    public final String[] getModules() {
        return this.modules.stream().map(ResourceLocation::toString).toArray(String[]::new);
    }

    @LuaFunction
    public final boolean hasModule(String moduleId) {
        ResourceLocation id = ResourceLocation.tryParse(moduleId);
        return id == null ? false : this.modules.stream().anyMatch(id::equals);
    }
}
