package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BaseTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.events.Events;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ChatBoxTile extends PeripheralTileEntity<ChatBoxPeripheral> {

    private Long lastConsumedMessage;

    public ChatBoxTile(BlockPos pos, BlockState state) {
        super(TileEntityTypes.CHAT_BOX.get(), pos, state);
        lastConsumedMessage = Events.counter - 1;
    }

    @NotNull
    @Override
    protected ChatBoxPeripheral createPeripheral() {
        return new ChatBoxPeripheral("chatBox", this);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (level1, blockPos, blockState, blockEntity) -> {
            serverTick(level, blockPos, blockState, (ChatBoxTile) blockEntity);
        };
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState state, ChatBoxTile blockEntity) {
        blockEntity.lastConsumedMessage = Events.traverseChatMessages(blockEntity.lastConsumedMessage, message -> {
            blockEntity.getConnectedComputers().forEach(computer -> computer.queueEvent("chat", message.username, message.message,
                    message.uuid, message.isHidden));
        });
    }

}