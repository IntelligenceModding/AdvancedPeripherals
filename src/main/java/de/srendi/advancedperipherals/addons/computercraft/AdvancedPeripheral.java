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
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;

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
    @Override
    public String[] @NotNull getMethodNames() {
        String[] methods = new String[1];
        methods[0] = "";
        return methods;
    }

    @NotNull
    @Override
    public MethodResult callMethod(@NotNull IComputerAccess iComputerAccess, @NotNull ILuaContext iLuaContext, int i, @NotNull IArguments iArguments) throws LuaException {
        return null;
    }
}
