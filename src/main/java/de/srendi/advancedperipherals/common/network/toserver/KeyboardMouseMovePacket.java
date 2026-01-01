package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class KeyboardMouseMovePacket implements IAPPacket {

    public static final Type<GlassesHotkeyPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("keyboard_mouse_move"));

    private final double dx;
    private final double dy;

    public KeyboardMouseMovePacket(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public KeyboardMouseMovePacket(FriendlyByteBuf buffer) {
        this.dx = buffer.readDouble();
        this.dy = buffer.readDouble();
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
        computer.queueEvent("player_mouse_move", new Object[]{dx, dy});
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeDouble(dx);
        buffer.writeDouble(dy);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
