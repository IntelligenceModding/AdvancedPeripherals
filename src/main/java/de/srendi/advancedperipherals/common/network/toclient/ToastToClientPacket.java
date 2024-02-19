package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import de.srendi.advancedperipherals.common.util.ToastUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.loading.FMLEnvironment;
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
        // Should in the theory not happen, but safe is safe.
        if (!FMLEnvironment.dist.isClient()) {
            AdvancedPeripherals.debug("Tried to display toasts on the server, aborting.");
            return;
        }
        ToastUtil.displayToast(title, component);
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
