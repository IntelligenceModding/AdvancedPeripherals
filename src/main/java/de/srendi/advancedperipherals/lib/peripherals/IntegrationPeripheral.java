package de.srendi.advancedperipherals.lib.peripherals;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public abstract class IntegrationPeripheral implements IDynamicPeripheral {

    protected final List<IComputerAccess> connectedComputers = new ArrayList<>();
    protected final List<BoundMethod> pluggedMethods = new ArrayList<>();
    protected boolean initialized = false;
    protected List<IPeripheralPlugin> plugins = null;
    protected String[] methodNames = new String[0];

    protected void buildPlugins() {
        if (!initialized) {
            initialized = true;
            this.pluggedMethods.clear();
            if (plugins != null) plugins.forEach(plugin -> {
                if (plugin.isSuitable(this))
                    pluggedMethods.addAll(plugin.getMethods());
            });
            this.methodNames = pluggedMethods.stream().map(BoundMethod::getName).toArray(String[]::new);
        }
    }

    protected void addPlugin(@NotNull IPeripheralPlugin plugin) {
        if (plugins == null) plugins = new LinkedList<>();
        plugins.add(plugin);
        IPeripheralOperation<?>[] operations = plugin.getOperations();
        if (operations != null) {
            throw new IllegalArgumentException("This is not possible to attach plugin with operations to not operationable owner");
        }
    }

    public List<IComputerAccess> getConnectedComputers() {
        return connectedComputers;
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        connectedComputers.add(computer);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        connectedComputers.remove(computer);
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return Objects.equals(this, iPeripheral);
    }

    @Override
    @NotNull
    public String @NotNull [] getMethodNames() {
        if (!initialized)
            buildPlugins();
        return methodNames;
    }

    @Override
    @NotNull
    public MethodResult callMethod(@NotNull IComputerAccess access, @NotNull ILuaContext context, int index, @NotNull IArguments arguments) throws LuaException {
        if (!initialized)
            buildPlugins();
        return pluggedMethods.get(index).apply(access, context, arguments);
    }
}
