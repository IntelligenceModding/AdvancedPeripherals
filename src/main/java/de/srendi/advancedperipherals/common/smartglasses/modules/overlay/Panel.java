package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * A panel is the standard panel which can contain multiple render-able objects in it.
 */
public class Panel extends RenderableObject {

    public Panel(String id, OverlayModule module, IArguments arguments) throws LuaException {
        super(id, module, arguments);
    }

    /**
     * constructor for the client side initialization
     *
     * @param id     id of the object
     * @param player the target player
     */
    public Panel(String id, UUID player) {
        super(id, player);
    }

    @Override
    protected void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeInt(color);
        buffer.writeDouble(opacity);
    }

    @Nullable
    public static Panel decode(FriendlyByteBuf buffer) {
        String id = buffer.readUtf();
        boolean hasValidUUID = buffer.readBoolean();
        if (!hasValidUUID) {
            AdvancedPeripherals.exception("Tried to decode a buffer for an OverlayObject but without a valid player as target.", new IllegalArgumentException());
            return null;
        }
        UUID player = buffer.readUUID();
        int color = buffer.readInt();
        double opacity = buffer.readDouble();

        Panel clientPanel = new Panel(id, player);
        clientPanel.color = color;
        clientPanel.opacity = opacity;

        return clientPanel;
    }

    @Override
    protected void handle(NetworkEvent.Context context) {
        super.handle(context);
    }
}
