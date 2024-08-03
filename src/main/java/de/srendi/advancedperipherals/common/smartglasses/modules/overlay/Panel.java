package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

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
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
    }

    @Override
    protected void handle(NetworkEvent.Context context) {
        super.handle(context);
    }
}
