package de.srendi.advancedperipherals.blocks.tileentity;

import com.google.common.collect.Lists;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.addons.computercraft.ComputerEventManager;
import de.srendi.advancedperipherals.setup.ModTileEntityTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatBoxTileEntity extends TileEntity implements ITickableTileEntity, ComputerEventManager.IComputerEventSender {

    private int tick;

    public ChatBoxTileEntity() {
        this(ModTileEntityTypes.CHAT_BOX.get());
    }

    public ChatBoxTileEntity(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    @Override
    public void tick() {
        tick++;
        if(tick == 40) {
            AdvancedPeripherals.LOGGER.log(Level.ERROR, "noice");
            sendEvent(this, "tickEvent", Arrays.asList("Noice"));
            tick = 0;
        }
    }

    @Override
    public void sendEvent(TileEntity te, String name, Object... params) {

    }

}
