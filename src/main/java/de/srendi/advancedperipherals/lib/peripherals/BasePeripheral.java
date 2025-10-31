package de.srendi.advancedperipherals.lib.peripherals;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.OperationAbility;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.PeripheralOwnerAbility;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class BasePeripheral<O extends IPeripheralOwner> implements IBasePeripheral<O>, IDynamicPeripheral {

    protected final Set<IComputerAccess> connectedComputers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    protected final String type;
    protected final O owner;
    protected final List<BoundMethod> pluggedMethods = new ArrayList<>();
    protected boolean initialized = false;
    protected List<IPeripheralPlugin> plugins = null;
    protected String[] methodNames = new String[0];

    protected BasePeripheral(String type, O owner) {
        this.type = type;
        this.owner = owner;
    }

    protected void buildPlugins() {
        if (!initialized) {
            initialized = true;
            this.pluggedMethods.clear();
            if (plugins != null) plugins.forEach(plugin -> {
                if (plugin.isSuitable(this))
                    pluggedMethods.addAll(plugin.getMethods());
            });
            owner.getAbilities().forEach(ability -> {
                if (ability instanceof IPeripheralPlugin peripheralPlugin)
                    pluggedMethods.addAll(peripheralPlugin.getMethods());
            });
            this.methodNames = pluggedMethods.stream().map(BoundMethod::getName).toArray(String[]::new);
        }
    }

    protected void addPlugin(@NotNull IPeripheralPlugin plugin) {
        if (plugins == null) plugins = new LinkedList<>();
        plugins.add(plugin);
        IPeripheralOperation<?>[] operations = plugin.getOperations();
        if (operations != null) {
            OperationAbility operationAbility = owner.getAbility(PeripheralOwnerAbility.OPERATION);
            if (operationAbility == null)
                throw new IllegalArgumentException("This is not possible to attach plugin with operations to not operationable owner");
            for (IPeripheralOperation<?> operation : operations)
                operationAbility.registerOperation(operation);
        }
    }

    @Override
    public Iterable<IComputerAccess> getConnectedComputers() {
        return connectedComputers;
    }

    @Nullable
    @Override
    public Object getTarget() {
        return owner;
    }

    @NotNull
    @Override
    public String getType() {
        return type;
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
    public O getPeripheralOwner() {
        return owner;
    }

    @LuaFunction
    public final String getName() {
        return owner.getCustomName();
    }

    public Map<String, Object> getPeripheralConfiguration() {
        Map<String, Object> data = new HashMap<>();
        owner.getAbilities().forEach(ability -> ability.collectConfiguration(data));
        return data;
    }

    @LuaFunction
    public final Map<String, Object> getConfiguration() {
        return getPeripheralConfiguration();
    }

    protected BlockPos getPos() {
        return owner.getPos();
    }

    protected Level getLevel() {
        return owner.getLevel();
    }

    protected Direction validateSide(String direction) throws LuaException {
        return CoordUtil.getDirection(owner.getOrientation(), direction);
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

    protected <T> MethodResult withOperation(IPeripheralOperation<T> operation, T context, @Nullable IPeripheralCheck<T> check, IPeripheralFunction<T, MethodResult> method, @Nullable Consumer<T> successCallback) throws LuaException {
        return withOperation(operation, context, check, method, successCallback, null);
    }

    protected <T> MethodResult withOperation(IPeripheralOperation<T> operation, T context, @Nullable IPeripheralCheck<T> check, IPeripheralFunction<T, MethodResult> method, @Nullable Consumer<T> successCallback, @Nullable BiConsumer<MethodResult, OperationAbility.FailReason> failCallback) throws LuaException {
        OperationAbility operationAbility = owner.getAbility(PeripheralOwnerAbility.OPERATION);
        if (operationAbility == null) throw new IllegalArgumentException("This shouldn't happen at all");
        return operationAbility.performOperation(operation, context, check, method, successCallback, failCallback);
    }
}
