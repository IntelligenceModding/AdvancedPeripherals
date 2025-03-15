package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class KeyboardMouseMovePacket implements IPacket {

    private final double dx;
    private final double dy;

    public KeyboardMouseMovePacket(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        ServerPlayer serverPlayer = context.getSender();
        if (serverPlayer == null) {
            return;
        }

        for (ItemStack stack : serverPlayer.getAllSlots()) {
            if (stack.getItem() instanceof SmartGlassesItem) {
                SmartGlassesComputer computer = SmartGlassesItem.getServerComputer(server, stack);
                if (computer != null) {
                    computer.queueEvent("player_mouse_move", new Object[]{dx, dy});
                    break;
                }
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeDouble(dx);
        buffer.writeDouble(dy);
    }

    public static KeyboardMouseMovePacket decode(FriendlyByteBuf buffer) {
        return new KeyboardMouseMovePacket(buffer.readDouble(), buffer.readDouble());
    }
}
