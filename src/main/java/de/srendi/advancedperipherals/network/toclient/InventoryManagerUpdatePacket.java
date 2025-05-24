package de.srendi.advancedperipherals.network.toclient;

import de.srendi.advancedperipherals.common.blocks.blockentities.InventoryManagerEntity;
import de.srendi.advancedperipherals.network.base.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class InventoryManagerUpdatePacket implements IPacket {

    public boolean hasOwner;
    public UUID owner;
    public BlockPos pos;

    public InventoryManagerUpdatePacket(boolean hasOwner, UUID owner, BlockPos pos) {
        this.hasOwner = hasOwner;
        this.owner = owner;
        this.pos = pos;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        if (blockEntity instanceof InventoryManagerEntity inventoryManagerEntity)
            inventoryManagerEntity.setOwner(hasOwner ? owner : null);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(hasOwner);
        if (hasOwner)
            buffer.writeUUID(owner);
        buffer.writeBlockPos(pos);
    }

    public static InventoryManagerUpdatePacket decode(FriendlyByteBuf buffer) {
        boolean hasOwner = buffer.readBoolean();
        return new InventoryManagerUpdatePacket(hasOwner, hasOwner ? buffer.readUUID() : null, buffer.readBlockPos());
    }
}
