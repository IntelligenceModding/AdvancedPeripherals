package de.srendi.advancedperipherals.lib.peripherals;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.AttachedComputerSet;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.OperationAbility;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.PeripheralOwnerAbility;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class BasePeripheral<O extends IPeripheralOwner> implements IBasePeripheral<O>, IDynamicPeripheral {

    protected final AttachedComputerSet attachedComputers = new AttachedComputerSet();
    protected final String type;
    @NotNull
    protected final O owner;
    protected final List<IPeripheralPlugin> plugins = new ArrayList<>();
    protected boolean initialized = false;
    protected final List<BoundMethod> pluggedMethods = new ArrayList<>();
    protected String[] methodNames = new String[0];

    protected BasePeripheral(String type, @NotNull O owner) {
        this.type = type;
        this.owner = owner;
    }

    protected void tryBuildPlugins() {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        this.pluggedMethods.clear();
        this.plugins.stream()
            .filter(plugin -> plugin.isSuitable(this))
            .forEach(plugin -> this.pluggedMethods.addAll(plugin.getMethods()));
        this.owner.getAbilities().forEach(ability -> {
            if (ability instanceof IPeripheralPlugin peripheralPlugin) {
                this.pluggedMethods.addAll(peripheralPlugin.getMethods());
            }
        });
        this.methodNames = this.pluggedMethods.stream().map(BoundMethod::getName).toArray(String[]::new);
    }

    protected void addPlugin(@NotNull IPeripheralPlugin plugin) {
        this.plugins.add(plugin);
        IPeripheralOperation<?>[] operations = plugin.getOperations();
        if (operations != null) {
            OperationAbility operationAbility = owner.getAbility(PeripheralOwnerAbility.OPERATION);
            if (operationAbility == null) {
                throw new IllegalArgumentException("This is not possible to attach plugin with operations to not operationable owner");
            }
            for (IPeripheralOperation<?> operation : operations) {
                operationAbility.registerOperation(operation);
            }
        }
    }

    @Override
    @Nullable
    public Object getTarget() {
        return owner;
    }

    @Override
    @NotNull
    public String getType() {
        return type;
    }

    @Override
    public void forEachConnectedComputers(Consumer<? super IComputerAccess> consumer) {
        attachedComputers.forEach(consumer);
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        attachedComputers.add(computer);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        attachedComputers.remove(computer);
    }

    @SuppressWarnings("NonOverridingEquals")
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

    @LuaFunction(mainThread = true)
    public final void setName(String name) {
        owner.setCustomName(name);
    }

    protected Map<String, Object> getPeripheralConfiguration() {
        Map<String, Object> data = new HashMap<>();
        owner.getAbilities().forEach(ability -> ability.collectConfiguration(data));
        return data;
    }

    @LuaFunction
    public final Map<String, Object> getConfiguration() {
        return getPeripheralConfiguration();
    }

    public ServerLevel getLevel() {
        return (ServerLevel) owner.getLevel();
    }

    public BlockPos getPos() {
        return owner.getPos();
    }

    public Vec3 getCenterPos() {
        return owner.getCenterPos();
    }

    public boolean isOnShip() {
        return false;
        // return APAddons.isBlockOnShip(owner.getLevel(), owner.getPos());
    }

    public Vec3 getPhysicsPos() {
        return owner.getPhysicsPos();
    }

    public final BlockPos getPhysicsBlockPos() {
        return BlockPos.containing(this.getPhysicsPos());
    }

    @Nullable
    public Direction mapDirection(String direction) {
        return CoordUtil.getDirection(owner.getFrontAndTop(), direction);
    }

    @Override
    @NotNull
    public String @NotNull [] getMethodNames() {
        this.tryBuildPlugins();
        return methodNames;
    }

    @Override
    @NotNull
    public MethodResult callMethod(@NotNull IComputerAccess access, @NotNull ILuaContext context, int index, @NotNull IArguments arguments) throws LuaException {
        this.tryBuildPlugins();
        return pluggedMethods.get(index).apply(access, context, arguments);
    }

    protected <T> MethodResult withOperation(IPeripheralOperation<T> operation, T context, @Nullable IPeripheralCheck<T> check, IPeripheralFunction<T, MethodResult> method, @Nullable IPeripheralOperation.Successor<T> successCallback) throws LuaException {
        return withOperation(operation, context, check, method, successCallback, null);
    }

    protected <T> MethodResult withOperation(IPeripheralOperation<T> operation, T context, @Nullable IPeripheralCheck<T> check, IPeripheralFunction<T, MethodResult> method, @Nullable IPeripheralOperation.Successor<T> successCallback, @Nullable IPeripheralOperation.Failer failCallback) throws LuaException {
        OperationAbility operationAbility = owner.getAbility(PeripheralOwnerAbility.OPERATION);
        if (operationAbility == null) {
            throw new IllegalStateException("This shouldn't happen at all");
        }
        return operationAbility.performOperation(operation, context, check, method, successCallback, failCallback);
    }

    @NotNull
    protected IItemHandler getItemHandler(IComputerAccess computer, String name) throws LuaException {
        if (name.length() >= 1 && name.charAt(0) == '@') {
            Direction dir = this.mapDirection(name.substring(1));
            if (dir == null) {
                throw new LuaException("Target '" + name + "' is an invalid direction");
            }
            IItemHandler inventory = ItemUtil.getHandlerFromDirection(owner, dir);
            if (inventory == null) {
                throw new LuaException("Target '" + name + "' is not an inventory");
            }
            return inventory;
        }
        IPeripheral toPeripheral = computer.getAvailablePeripheral(name);
        if (toPeripheral == null) {
            throw new LuaException("Target '" + name + "' does not exist");
        }
        IItemHandler inventory = ItemUtil.extractHandler(toPeripheral);
        if (inventory == null) {
            throw new LuaException("Target '" + name + "' is not an inventory");
        }
        return inventory;
    }
}
