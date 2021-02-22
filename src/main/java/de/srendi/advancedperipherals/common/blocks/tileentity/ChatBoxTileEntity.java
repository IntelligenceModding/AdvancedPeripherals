package de.srendi.advancedperipherals.common.blocks.tileentity;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.BasePeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public class ChatBoxTileEntity extends PeripheralTileEntity<ChatBoxPeripheral> implements ITickableTileEntity {

    private int tick;

    public ChatBoxTileEntity() {
        this(TileEntityTypes.CHAT_BOX.get());
    }

    @Override
    protected ChatBoxPeripheral createPeripheral() {
        return new ChatBoxPeripheral("chatBox", this);
    }

    public ChatBoxTileEntity(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    @Override
    public void tick() {
        if (peripheral.getTick() == 0) {
            tick = 0; //Sync the tick of the peripherals and the tile entity
        }
        tick++;
        peripheral.setTick(tick);
    }
}