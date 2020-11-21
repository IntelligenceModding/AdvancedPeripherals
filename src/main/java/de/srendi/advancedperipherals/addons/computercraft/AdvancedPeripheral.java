package de.srendi.advancedperipherals.addons.computercraft;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class AdvancedPeripheral implements IDynamicPeripheral, ComputerEventManager.IComputerEventSender{

    String type;

    public AdvancedPeripheral(String type) {
        this.type = type;
    }

    @Nonnull
    @Override
    public String getType() {
        return type;
    }

    @Override
    public Object getTarget() {
        return null;
    }

    @Override
    public boolean equals(IPeripheral iPeripheral) {
        return this == iPeripheral;
    }

    @Override
    public void sendEvent(TileEntity te, String name, Object... params) {

    }

    @NotNull
    @Nonnull
    @Override
    public String @NotNull [] getMethodNames() {
        return new String[0];
    }

    @NotNull
    @Override
    public MethodResult callMethod(@NotNull IComputerAccess iComputerAccess, @NotNull ILuaContext iLuaContext, int i, @NotNull IArguments iArguments) throws LuaException {
        return null;
    }
}
