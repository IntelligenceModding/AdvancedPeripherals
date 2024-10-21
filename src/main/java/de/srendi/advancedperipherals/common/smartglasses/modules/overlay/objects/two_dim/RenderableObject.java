package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.ITwoDObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

// TODO: generate setters/getters lua functions out of our FloatingNumberProperty fields
public class RenderableObject extends OverlayObject {

    @FloatingNumberProperty(min = 0, max = 1)
    public float opacity = 1;

    @FixedPointNumberProperty(min = 0, max = 0xFFFFFF)
    public int color = 0xFFFFFF;

    @FixedPointNumberProperty(min = -32767, max = 32767)
    public int x = 0;

    @FixedPointNumberProperty(min = -32767, max = 32767)
    public int y = 0;

    @FixedPointNumberProperty(min = -32767, max = 32767)
    public int z = 0;

    @FixedPointNumberProperty(min = -32767, max = 32767)
    public int maxX = 0;

    @FixedPointNumberProperty(min = -32767, max = 32767)
    public int maxY = 0;

    public RenderableObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public RenderableObject(UUID player) {
        super(player);
    }

    @LuaFunction
    public final float getOpacity() {
        return opacity;
    }

    @LuaFunction
    public final void setOpacity(double opacity) {
        this.opacity = (float) opacity;
        getModule().update(this);
    }

    @LuaFunction
    public final int getColor() {
        return color;
    }

    @LuaFunction
    public final void setColor(int color) {
        this.color = color;
        getModule().update(this);
    }

    @LuaFunction
    public final void setMaxX(int maxX) {
        this.maxX = maxX;
        getModule().update(this);
    }

    @LuaFunction
    public final int getMaxX() {
        return maxX;
    }

    @LuaFunction
    public final void setMaxY(int maxY) {
        this.maxY = maxY;
        getModule().update(this);
    }

    @LuaFunction
    public final int getMaxY() {
        return maxY;
    }

    @LuaFunction
    public final void setX(int x) {
        this.x = x;
        getModule().update(this);
    }

    @LuaFunction
    public final int getX() {
        return x;
    }

    @LuaFunction
    public final void setY(int y) {
        this.y = y;
        getModule().update(this);
    }

    @LuaFunction
    public final int getY() {
        return y;
    }

    @LuaFunction
    public final void setZ(int z) {
        this.z = z;
        getModule().update(this);
    }

    @LuaFunction
    public final int getZ() {
        return z;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeInt(color);
        buffer.writeFloat(opacity);

        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeInt(maxX);
        buffer.writeInt(maxY);
    }

    public IObjectRenderer getRenderObject() {
        return null;
    }

    @Override
    public String toString() {
        return "RenderableObject{" +
                "opacity=" + opacity +
                ", color=" + color +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", sizeX=" + maxX +
                ", sizeY=" + maxY +
                '}';
    }
}
