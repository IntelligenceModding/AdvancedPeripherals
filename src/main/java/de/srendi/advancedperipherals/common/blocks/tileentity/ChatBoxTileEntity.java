package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class ChatBoxTileEntity extends PeripheralTileEntity<ChatBoxPeripheral> implements ITickableTileEntity {

    private int tick;

    public ChatBoxTileEntity() {
        this(TileEntityTypes.CHAT_BOX.get());
    }

    public ChatBoxTileEntity(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    @Override
    protected ChatBoxPeripheral createPeripheral() {
        return new ChatBoxPeripheral("chatBox", this);
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