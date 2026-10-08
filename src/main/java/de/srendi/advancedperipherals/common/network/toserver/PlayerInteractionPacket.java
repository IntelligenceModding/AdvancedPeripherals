package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.UUID;

public class PlayerInteractionPacket implements IAPPacket {

    private final int button;
    private final BlockPos hitBlock;
    private final Direction hitBlockFace;
    private final UUID hitEntity;

    public PlayerInteractionPacket(int button, BlockPos hitBlock, Direction hitBlockFace, UUID hitEntity) {
        this.button = button;
        this.hitBlock = hitBlock;
        this.hitBlockFace = hitBlockFace;
        this.hitEntity = hitEntity;
    }

    public PlayerInteractionPacket(FriendlyByteBuf buffer) {
        this.button = buffer.readVarInt();
        int blockFace = buffer.readByte();
        if (blockFace != 0) {
            this.hitBlockFace = Direction.from3DDataValue(blockFace - 1);
            this.hitBlock = buffer.readBlockPos();
        } else {
            this.hitBlock = null;
            this.hitBlockFace = null;
        }
        this.hitEntity = buffer.readNullable(FriendlyByteBuf::readUUID);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();

        ItemStack smartGlasses = SmartGlassesItem.getEquipped(player);
        if (smartGlasses.isEmpty()) {
            return;
        }
        SmartGlassesComputer computer = SmartGlassesItem.getServerComputer(player.server, smartGlasses);
        if (computer == null) {
            return;
        }
        Map<String, Object> blockData = null;
        if (this.hitBlock != null) {
            blockData = LuaConverter.blockStateToLua(player.level().getBlockState(this.hitBlock), player.level(), this.hitBlock);
            blockData.put("face", this.hitBlockFace.getSerializedName());
        }
        computer.queueEvent(CCEvents.PLAYER_INTERACTION, new Object[]{
            button,
            blockData,
            this.hitEntity == null ? null : this.hitEntity.toString(),
        });
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(button);
        if (this.hitBlock != null) {
            buffer.writeByte(this.hitBlockFace.get3DDataValue() + 1);
            buffer.writeBlockPos(this.hitBlock);
        } else {
            buffer.writeByte(0);
        }
        buffer.writeNullable(this.hitEntity, FriendlyByteBuf::writeUUID);
    }
}
