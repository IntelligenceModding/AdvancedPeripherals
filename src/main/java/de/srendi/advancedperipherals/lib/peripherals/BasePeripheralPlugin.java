package de.srendi.advancedperipherals.lib.peripherals;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.OperationAbility;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.PeripheralOwnerAbility;
import org.jetbrains.annotations.Nullable;

public abstract class BasePeripheralPlugin implements IPeripheralPlugin {
    protected final IPeripheralOwner owner;

    public BasePeripheralPlugin(IPeripheralOwner owner) {
        this.owner = owner;
    }

    public IPeripheralOwner getPeripheralOwner() {
        return this.owner;
    }

    protected <T> MethodResult withOperation(IPeripheralOperation<T> operation, T context, @Nullable IPeripheralCheck<T> check, IPeripheralFunction<T, MethodResult> method, @Nullable IPeripheralOperation.Successor<T> successCallback) throws LuaException {
        return withOperation(operation, context, check, method, successCallback, null);
    }

    protected <T> MethodResult withOperation(IPeripheralOperation<T> operation, T context, @Nullable IPeripheralCheck<T> check, IPeripheralFunction<T, MethodResult> method, @Nullable IPeripheralOperation.Successor<T> successCallback, @Nullable IPeripheralOperation.Failer failCallback) throws LuaException {
        OperationAbility operationAbility = this.owner.getAbility(PeripheralOwnerAbility.OPERATION);
        if (operationAbility == null) throw new IllegalArgumentException("This shouldn't happen at all");
        return operationAbility.performOperation(operation, context, check, method, successCallback, failCallback);
    }
}
