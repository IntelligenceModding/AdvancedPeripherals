package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.screens.KeyboardScreen;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class KeyboardMouseCapturePacket implements IPacket {

    private final boolean enable;

    public KeyboardMouseCapturePacket(boolean enable) {
        this.enable = enable;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(NetworkEvent.Context context) {
        if (!(Minecraft.getInstance().screen instanceof KeyboardScreen screen)) {
            return;
        }
        screen.setCaptureMouse(this.enable);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.enable);
    }

    public static KeyboardMouseCapturePacket decode(FriendlyByteBuf buffer) {
        return new KeyboardMouseCapturePacket(buffer.readBoolean());
    }
}
