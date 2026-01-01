package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class KeyboardMouseClickPacket implements IAPPacket {

    public static final Type<GlassesHotkeyPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("keyboard_mouse_click"));

    private final int button;
    private final boolean isRelease;

    public KeyboardMouseClickPacket(int button, boolean isRelease) {
        this.button = button;
        this.isRelease = isRelease;
    }

    public KeyboardMouseClickPacket(FriendlyByteBuf buffer) {
        this.button = buffer.readVarInt();
        this.isRelease = buffer.readBoolean();
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
        computer.queueEvent(isRelease ? "player_mouse_up" : "player_mouse_click", new Object[]{button});
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(button);
        buffer.writeBoolean(isRelease);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
