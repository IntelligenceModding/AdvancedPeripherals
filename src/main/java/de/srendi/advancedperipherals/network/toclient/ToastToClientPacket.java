package de.srendi.advancedperipherals.network.toclient;

import de.srendi.advancedperipherals.network.base.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

public class ToastToClientPacket implements IPacket {

    private final Component title;
    private final Component component;

    public ToastToClientPacket(Component title, Component component) {
        this.title = title;
        this.component = component;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        Minecraft minecraft = Minecraft.getInstance();

        minecraft.getToasts().addToast(SystemToast.multiline(minecraft,
                SystemToast.SystemToastIds.PERIODIC_NOTIFICATION, title, component));
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeComponent(this.title);
        buffer.writeComponent(this.component);
    }

    public static ToastToClientPacket decode(FriendlyByteBuf buffer) {
        return new ToastToClientPacket(buffer.readComponent(), buffer.readComponent());
    }
}
