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

public class KeyboardMouseScrollPacket implements IPacket {

    private final int delta;

    public KeyboardMouseScrollPacket(int delta) {
        this.delta = delta;
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
                    computer.queueEvent("player_mouse_scroll", new Object[]{delta});
                    break;
                }
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(delta);
    }

    public static KeyboardMouseScrollPacket decode(FriendlyByteBuf buffer) {
        return new KeyboardMouseScrollPacket(buffer.readVarInt());
    }
}
