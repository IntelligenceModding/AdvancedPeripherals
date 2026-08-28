package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;
import java.util.UUID;

public class PlayerInteractionPacket implements IAPPacket {

    public static final Type<PlayerInteractionPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("player_interaction"));

    private final int button;
    private final BlockPos hitBlock;
    private final UUID hitEntity;

    public PlayerInteractionPacket(int button, BlockPos hitBlock, UUID hitEntity) {
        this.button = button;
        this.hitBlock = hitBlock;
        this.hitEntity = hitEntity;
    }

    public PlayerInteractionPacket(RegistryFriendlyByteBuf buffer) {
        this.button = buffer.readVarInt();
        this.hitBlock = buffer.readOptional(RegistryFriendlyByteBuf::readBlockPos).orElse(null);
        this.hitEntity = buffer.readOptional(RegistryFriendlyByteBuf::readUUID).orElse(null);
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
        computer.queueEvent(CCEvents.PLAYER_INTERACTION, new Object[]{
            button,
            this.hitBlock == null ? null : LuaConverter.blockStateToLua(player.level().getBlockState(this.hitBlock), this.hitBlock),
            this.hitEntity == null ? null : this.hitEntity.toString(),
        });
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(button);
        buffer.writeOptional(Optional.ofNullable(this.hitBlock), RegistryFriendlyByteBuf::writeBlockPos);
        buffer.writeOptional(Optional.ofNullable(this.hitEntity), RegistryFriendlyByteBuf::writeUUID);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
