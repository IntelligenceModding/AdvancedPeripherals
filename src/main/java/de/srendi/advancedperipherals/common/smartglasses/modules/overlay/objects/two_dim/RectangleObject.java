package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.RectangleRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;

import java.util.UUID;

/**
 * Just a rectangle
 */
public class RectangleObject extends RenderableObject {
    public static final int TYPE_ID = 0;

    private static final RectangleRenderer RENDERER = new RectangleRenderer();

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
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public IObjectRenderer getObjectRenderer() {
        return RENDERER;
    }
}
