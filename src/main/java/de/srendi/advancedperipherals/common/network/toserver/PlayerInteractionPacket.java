package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;
import java.util.UUID;

public class PlayerInteractionPacket implements IAPPacket {

    public static final Type<PlayerInteractionPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("player_interaction"));

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

    public PlayerInteractionPacket(RegistryFriendlyByteBuf buffer) {
        this.button = buffer.readVarInt();
        int blockFace = buffer.readByte();
        if (blockFace != 0) {
            this.hitBlockFace = Direction.BY_ID.apply(blockFace - 1);
            this.hitBlock = buffer.readBlockPos();
        } else {
            this.hitBlock = null;
            this.hitBlockFace = null;
        }
        this.hitEntity = buffer.readNullable(RegistryFriendlyByteBuf::readUUID);
    }

    @Override
    public void handle(IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

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
            blockData = LuaConverter.blockStateToLua(player.level().getBlockState(this.hitBlock), this.hitBlock);
            blockData.put("face", this.hitBlockFace.getSerializedName());
        }
        computer.queueEvent(CCEvents.PLAYER_INTERACTION, new Object[]{
            button,
            blockData,
            this.hitEntity == null ? null : this.hitEntity.toString(),
        });
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(button);
        if (this.hitBlock != null) {
            buffer.writeByte(this.hitBlockFace.get3DDataValue() + 1);
            buffer.writeBlockPos(this.hitBlock);
        } else {
            buffer.writeByte(0);
        }
        buffer.writeNullable(this.hitEntity, RegistryFriendlyByteBuf::writeUUID);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
