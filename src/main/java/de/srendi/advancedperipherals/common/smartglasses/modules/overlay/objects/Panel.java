package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.PanelRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

/**
 * A panel is the standard panel which can contain multiple render-able objects in it.
 */
public class Panel extends RenderableObject {
    public static final int ID = 0;

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
        buffer.writeInt(ID);
        super.encode(buffer);
    }

    public static Panel decode(FriendlyByteBuf buffer) {
        String id = buffer.readUtf();
        boolean hasValidUUID = buffer.readBoolean();
        if (!hasValidUUID) {
            AdvancedPeripherals.exception("Tried to decode a buffer for an OverlayObject but without a valid player as target.", new IllegalArgumentException());
            return null;
        }
        UUID player = buffer.readUUID();
        int color = buffer.readInt();
        float opacity = buffer.readFloat();

        int x = buffer.readInt();
        int y = buffer.readInt();
        int sizeX = buffer.readInt();
        int sizeY = buffer.readInt();

        Panel clientObject = new Panel(id, player);
        clientObject.color = color;
        clientObject.opacity = opacity;
        clientObject.x = x;
        clientObject.y = y;
        clientObject.maxX = sizeX;
        clientObject.maxY = sizeY;

        return clientObject;
    }

    @Override
    public IObjectRenderer getRenderObject() {
        return new PanelRenderer();
    }
}
