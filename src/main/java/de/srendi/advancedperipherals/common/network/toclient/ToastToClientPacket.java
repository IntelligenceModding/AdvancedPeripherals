package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.util.ToastUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

public class ToastToClientPacket implements IAPPacket {

    private final Component title;
    private final Component component;

    public ToastToClientPacket(Component title, Component component) {
        this.title = title;
        this.component = component;
    }

    public ToastToClientPacket(FriendlyByteBuf buffer) {
        this.title = buffer.readComponent();
        this.component = buffer.readComponent();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeComponent(this.title);
        buffer.writeComponent(this.component);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        System.out.println("displaying toast " + context.getPacketHandled());
        new Exception().printStackTrace(System.out);
        ToastUtil.displayToast(this.title, this.component);
    }
}
