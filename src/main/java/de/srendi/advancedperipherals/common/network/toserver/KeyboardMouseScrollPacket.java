package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class KeyboardMouseScrollPacket implements IAPPacket {

    public static final Type<GlassesHotkeyPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("keyboard_mouse_scroll"));

    private final int delta;

    public KeyboardMouseScrollPacket(int delta) {
        this.delta = delta;
    }

    public KeyboardMouseScrollPacket(FriendlyByteBuf buffer) {
        this(buffer.readVarInt());
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
        computer.queueEvent("player_mouse_scroll", new Object[]{delta});
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(delta);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
