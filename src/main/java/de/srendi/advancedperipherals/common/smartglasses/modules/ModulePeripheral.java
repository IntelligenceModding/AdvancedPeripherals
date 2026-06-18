package de.srendi.advancedperipherals.common.smartglasses.modules;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.IDynamicLuaObject;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.BoundMethod;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ModulePeripheral extends BasePeripheral<ModulePeripheralOwner> {
    public static final String PERIPHERAL_TYPE = "smart_glasses";
    private static final int BUILTIN_METHODS = 2;

    private volatile SortedMap<ResourceLocation, CompiledModule> moduleMap = new TreeMap<>();

    public ModulePeripheral(SmartGlassesComputer computer) {
        super(PERIPHERAL_TYPE, new ModulePeripheralOwner(computer));
    }

    public void updateModules(Collection<@NotNull IModule> modules) {
        SortedMap<ResourceLocation, CompiledModule> moduleMap = new TreeMap<>();
        SmartGlassesSideAccess smartGlassesModuleAccess = getPeripheralOwner().getComputer().getSmartGlassesModuleAccess();
        for (IModule module : modules) {
            IModuleFunctions functions = module.getFunctions(smartGlassesModuleAccess);
            moduleMap.put(module.getId(), new CompiledModule(module.getId(), module.getLuaAlias(), functions));
        }
        this.moduleMap = moduleMap;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @LuaFunction
    public final String[] getModules() {
        return this.moduleMap.keySet().stream().map(ResourceLocation::toString).toArray(String[]::new);
    }

    @LuaFunction
    public final boolean hasModule(String moduleId) {
        ResourceLocation id = ResourceLocation.tryParse(moduleId);
        if (id == null) {
            return false;
        }
        return this.moduleMap.containsKey(id);
    }

    @LuaFunction
    public final BindedModule getModule(IComputerAccess computerAccess, String moduleId) {
        ResourceLocation id = ResourceLocation.tryParse(moduleId);
        if (id == null) {
            return null;
        }
        CompiledModule module = this.moduleMap.get(id);
        if (module == null) {
            return null;
        }
        return new BindedModule(computerAccess, module);
    }

    private static final class CompiledModule {
        private final ResourceLocation id;
        private final @Nullable String alias;
        private final List<BoundMethod> methods;
        private final String[] methodNames;

        private CompiledModule(ResourceLocation id, @Nullable String alias, @Nullable IModuleFunctions functions) {
            this.id = id;
            this.alias = alias;
            this.methods = functions == null ? List.of() : functions.getMethods();
            this.methodNames = new String[BUILTIN_METHODS + this.methods.size()];
            this.methodNames[0] = "getId";
            this.methodNames[1] = "getAlias";
            for (int i = 0; i < this.methods.size(); i++) {
                this.methodNames[BUILTIN_METHODS + i] = this.methods.get(i).getName();
            }
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public String[] getMethodNames() {
            return this.methodNames;
        }
    }

    private static final class BindedModule implements IDynamicLuaObject {
        private final IComputerAccess computer;
        private final CompiledModule compiled;

        private BindedModule(IComputerAccess computer, CompiledModule compiled) {
            this.computer = computer;
            this.compiled = compiled;
        }

        @Override
        public String[] getMethodNames() {
            return this.compiled.getMethodNames();
        }

        @Override
        public MethodResult callMethod(ILuaContext context, int index, IArguments args) throws LuaException {
            return switch (index) {
                case 0 -> MethodResult.of(this.compiled.getId().toString());
                case 1 -> MethodResult.of(this.compiled.alias);
                default -> this.compiled.methods.get(index - BUILTIN_METHODS).apply(this.computer, context, args);
            };
        }
    }
}
