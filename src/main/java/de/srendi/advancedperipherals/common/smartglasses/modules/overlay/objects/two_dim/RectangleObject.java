package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaTable;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.RectangleRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Just a rectangle
 */
public class RectangleObject extends RenderableObject {
    public static final int TYPE_ID = 0;

    private static final RectangleRenderer RENDERER = new RectangleRenderer();

    @FloatingNumberProperty(min = 0)
    public float sizeX = 0;

    @FloatingNumberProperty(min = 0)
    public float sizeY = 0;

    public RectangleObject(OverlayModule module, LuaTable<?, ?> initFields) throws LuaException {
        super(module, initFields);
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
    @NotNull
    public String getType() {
        return "rectangle";
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
