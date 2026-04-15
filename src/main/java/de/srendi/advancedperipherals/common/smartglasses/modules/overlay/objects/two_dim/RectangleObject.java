package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.setup.APOverlayObjects;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Just a rectangle
 */
public class RectangleObject extends RenderableObject {
    @FloatingNumberProperty(min = 0)
    public float sizeX = 0;

    @FloatingNumberProperty(min = 0)
    public float sizeY = 0;

    public RectangleObject(OverlayModule module) {
        super(module);
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
    public OverlayObjectType<RectangleObject> getType() {
        return APOverlayObjects.RECTANGLE.get();
    }

    @LuaFunction
    public MethodResult getSizes() {
        return MethodResult.of(this.sizeX, this.sizeY);
    }

    @LuaFunction
    public void setSizes(double x, double y) {
        this.sizeX = (float) Math.max(x, 0);
        this.sizeY = (float) Math.max(y, 0);
        this.markAndTryUpdate("sizeX", "sizeY");
    }
}
