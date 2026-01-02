package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class KeyboardMouseScrollPacket implements IAPPacket {

    public static final Type<GlassesHotkeyPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("keyboard_mouse_scroll"));

    private final int deltaY;
    private final int deltaX;

    public KeyboardMouseScrollPacket(int deltaY, int deltaX) {
        this.deltaY = deltaY;
        this.deltaX = deltaX;
    }

    public KeyboardMouseScrollPacket(FriendlyByteBuf buffer) {
        this(buffer.readVarInt(), buffer.readVarInt());
    }

    @Override
    public void handle(IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            return;
        }

        ItemStack smartGlasses = SmartGlassesItem.getEquipped(player);
        if (smartGlasses.isEmpty()) {
            return;
        }
        SmartGlassesComputer computer = SmartGlassesItem.getServerComputer(player.server, stack);
        if (computer == null) {
            return;
        }
        computer.queueEvent("player_mouse_scroll", new Object[]{this.deltaY, this.deltaX});
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.deltaY);
        buffer.writeVarInt(this.deltaX);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
