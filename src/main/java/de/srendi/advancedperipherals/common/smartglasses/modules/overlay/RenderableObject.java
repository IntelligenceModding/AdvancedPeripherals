package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertyTypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertyTypes.FloatingNumberProperty;
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
    public int maxX = 0;

    @FixedPointNumberProperty(min = -32767, max = 32767)
    public int maxY = 0;

    @LuaFunction
    public float getOpacity() {
        return opacity;
    }

    @LuaFunction
    public void setOpacity(float opacity) {
        this.opacity = opacity;
        getModule().update(this);
    }

    @LuaFunction
    public int getColor() {
        return color;
    }

    @LuaFunction
    public void setColor(int color) {
        this.color = color;
        getModule().update(this);
    }

    @LuaFunction
    public void setMaxX(int maxX) {
        this.maxX = maxX;
        getModule().update(this);
    }

    @LuaFunction
    public void setMaxY(int maxY) {
        this.maxY = maxY;
        getModule().update(this);
    }

    @LuaFunction
    public void setX(int x) {
        this.x = x;
        getModule().update(this);
    }

    @LuaFunction
    public void setY(int y) {
        this.y = y;
        getModule().update(this);
    }

    public RenderableObject(String id, OverlayModule module, IArguments arguments) throws LuaException {
        super(id, module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public RenderableObject(String id, UUID player) {
        super(id, player);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeInt(color);
        buffer.writeFloat(opacity);

        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(maxX);
        buffer.writeInt(maxY);
    }

    public static RenderableObject decode(FriendlyByteBuf buffer) {
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

        RenderableObject clientObject = new RenderableObject(id, player);
        clientObject.color = color;
        clientObject.opacity = opacity;
        clientObject.x = x;
        clientObject.y = y;
        clientObject.maxX = sizeX;
        clientObject.maxY = sizeY;

        return clientObject;
    }

    @Override
    public String toString() {
        return "RenderableObject{" +
                "opacity=" + opacity +
                ", color=" + color +
                ", x=" + x +
                ", y=" + y +
                ", sizeX=" + maxX +
                ", sizeY=" + maxY +
                '}';
    }
}
