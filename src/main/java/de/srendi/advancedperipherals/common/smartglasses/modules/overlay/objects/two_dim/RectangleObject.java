package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.RectangleRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

/**
 * Just a rectangle
 */
public class RectangleObject extends RenderableObject {
    public static final int TYPE_ID = 0;

    private final IObjectRenderer renderer = new RectangleRenderer();

    public RectangleObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
    }

    /**
     * constructor for the client side initialization
     *
     * @param player the target player
     */
    public RectangleObject(UUID player) {
        super(player);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(TYPE_ID);
        super.encode(buffer);
    }

    public static RectangleObject decode(FriendlyByteBuf buffer) {
        int objectId = buffer.readInt();
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
        int z = buffer.readInt();
        int sizeX = buffer.readInt();
        int sizeY = buffer.readInt();

        RectangleObject clientObject = new RectangleObject(player);
        clientObject.setId(objectId);
        clientObject.color = color;
        clientObject.opacity = opacity;
        clientObject.x = x;
        clientObject.y = y;
        clientObject.z = z;
        clientObject.maxX = sizeX;
        clientObject.maxY = sizeY;

        return clientObject;
    }

    @Override
    public IObjectRenderer getRenderObject() {
        return renderer;
    }
}
