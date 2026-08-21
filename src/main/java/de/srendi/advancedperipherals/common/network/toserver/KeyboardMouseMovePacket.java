package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class KeyboardMouseMovePacket implements IAPPacket {

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
        computer.queueEvent(CCEvents.PLAYER_MOUSE_MOVE, new Object[]{dx, dy});
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeDouble(dx);
        buffer.writeDouble(dy);
    }
}
