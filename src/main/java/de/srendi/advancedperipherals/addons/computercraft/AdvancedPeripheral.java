package de.srendi.advancedperipherals.addons.computercraft;

import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.Level;
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

    @Nonnull
    @Override
    public String[] getMethodNames() {
        String[] methods = new String[1];
        methods[0] = "Nice";
        return methods;
    }

    @Nonnull
    @Override
    public MethodResult callMethod(@NotNull IComputerAccess iComputerAccess, @NotNull ILuaContext iLuaContext, int i, @NotNull IArguments iArguments) throws LuaException {
        AdvancedPeripherals.LOGGER.log(Level.ERROR, "It works (:" + iComputerAccess + " " + iLuaContext + " " + i + " " + iArguments);
        return MethodResult.of("eeeeeeeeeeeeee");
    }
}
