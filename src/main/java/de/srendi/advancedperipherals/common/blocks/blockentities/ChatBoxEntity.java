package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ChatBoxEntity extends PeripheralBlockEntity<ChatBoxPeripheral> {

    public ChatBoxEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.CHAT_BOX.get(), pos, state);
    }

    @Override
    @NotNull
    protected ChatBoxPeripheral buildPeripheral() {
        return new ChatBoxPeripheral(this);
    }
}
