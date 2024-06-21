package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertyTypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertyTypes.FloatingNumberProperty;

import java.util.UUID;

public abstract class RenderableObject extends OverlayObject {

    @FloatingNumberProperty(min = 0, max = 1)
    public double opacity = 1;

    @FixedPointNumberProperty(min = 0, max = 0xFFFFFF)
    public int color = 0xFFFFFF;

    @LuaFunction
    public double getOpacity() {
        return opacity;
    }

    @LuaFunction
    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    @LuaFunction
    public int getColor() {
        return color;
    }

    @LuaFunction
    public void setColor(int color) {
        this.color = color;
    }

    public RenderableObject(String id, OverlayModule module, IArguments arguments) throws LuaException {
        super(id, module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public RenderableObject(String id, UUID player) {
        super(id, player);
    }
}
