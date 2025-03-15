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

public class KeyboardMouseClickPacket implements IPacket {

    private final int button;
    private final boolean isRelease;

    public KeyboardMouseClickPacket(int button, boolean isRelease) {
        this.button = button;
        this.isRelease = isRelease;
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
                    computer.queueEvent(isRelease ? "player_mouse_up" : "player_mouse_click", new Object[]{button});
                    break;
                }
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(button);
        buffer.writeBoolean(isRelease);
    }

    public static KeyboardMouseClickPacket decode(FriendlyByteBuf buffer) {
        return new KeyboardMouseClickPacket(buffer.readVarInt(), buffer.readBoolean());
    }
}
