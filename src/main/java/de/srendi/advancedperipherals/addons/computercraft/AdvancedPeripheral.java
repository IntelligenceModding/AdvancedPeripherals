package de.srendi.advancedperipherals.addons.computercraft;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CopyOnWriteArrayList;

public class AdvancedPeripheral implements IDynamicPeripheral, ComputerEventManager.IComputerEventSender{

    private final ILuaMethodProvider provider;
    private final CopyOnWriteArrayList<IComputerAccess> attachedComputers = new CopyOnWriteArrayList<>();

    public AdvancedPeripheral(ILuaMethodProvider provider) {
        this.provider = provider;
    }

    @Nonnull
    @Override
    public String[] getMethodNames() {
        return provider.getLuaMethodRegistry().getMethodNames();
    }

    @Nonnull
    @Override
    public MethodResult callMethod(@Nonnull IComputerAccess computer, @Nonnull ILuaContext ctx, int method, @Nonnull IArguments args) throws LuaException {
        try {
            return MethodResult.of(provider.getLuaMethodRegistry().getMethod(method).call(args.getAll()));
        } catch (Exception e) {
            throw new LuaException(e.getMessage());
        }
    }

    @Nonnull
    @Override
    public String getType() {
        return provider.getPeripheralType();
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return this == other;
    }

    @Override
    public void attach(@Nonnull IComputerAccess computer) {
        attachedComputers.add(computer);
    }

    @Override
    public void detach(@Nonnull IComputerAccess computer) {
        attachedComputers.remove(computer);
    }

    @Override
    public void sendEvent(TileEntity te, String name, Object... params) {
        attachedComputers.forEach(a -> a.queueEvent(name, params));
    }
}
