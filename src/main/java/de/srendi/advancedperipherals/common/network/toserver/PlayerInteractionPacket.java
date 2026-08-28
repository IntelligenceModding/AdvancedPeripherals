package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class PlayerInteractionPacket implements IAPPacket {

    private final int button;
    private final BlockPos hitBlock;
    private final UUID hitEntity;

    public PlayerInteractionPacket(int button, BlockPos hitBlock, UUID hitEntity) {
        this.button = button;
        this.hitBlock = hitBlock;
        this.hitEntity = hitEntity;
    }

    public PlayerInteractionPacket(FriendlyByteBuf buffer) {
        this.button = buffer.readVarInt();
        this.hitBlock = buffer.readNullable(FriendlyByteBuf::readBlockPos);
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
        computer.queueEvent(CCEvents.PLAYER_INTERACTION, new Object[]{
            button,
            this.hitBlock == null ? null : LuaConverter.blockStateToLua(player.level().getBlockState(this.hitBlock), player.level(), this.hitBlock),
            this.hitEntity == null ? null : this.hitEntity.toString(),
        });
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(button);
        buffer.writeNullable(this.hitBlock, FriendlyByteBuf::writeBlockPos);
        buffer.writeNullable(this.hitEntity, FriendlyByteBuf::writeUUID);
    }
}
