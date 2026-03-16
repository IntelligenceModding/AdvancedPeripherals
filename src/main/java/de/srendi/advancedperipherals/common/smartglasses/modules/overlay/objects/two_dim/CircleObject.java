package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.CircleRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;
import java.util.UUID;

public class CircleObject extends RenderableObject {
    public static final int TYPE_ID = 1;

    private static final IObjectRenderer RENDERER = new CircleRenderer();

    @FixedPointNumberProperty(min = -32767, max = 32767)
    public int radius = 0;

    @BooleanProperty
    public boolean filled = true;

    @BooleanProperty
    public boolean pixelated = false;

    @FixedPointNumberProperty(min = 0, max = 32767)
    public int borderWidth = 4;

    @FixedPointNumberProperty(min = 0, max = 100)
    public int segments = 25;

    public CircleObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public CircleObject(UUID player) {
        super(player);
    }

    @LuaFunction
    public int getRadius() {
        return radius;
    }

    @LuaFunction
    public void setRadius(int radius) {
        this.radius = radius;
        getModule().update(this);
    }

    @LuaFunction
    public boolean isFilled() {
        return filled;
    }

    @LuaFunction
    public void setFilled(boolean filled) {
        this.filled = filled;
        getModule().update(this);
    }

    @LuaFunction
    public boolean isPixelated() {
        return pixelated;
    }

    @LuaFunction
    public void setPixelated(boolean pixelated) {
        this.pixelated = pixelated;
        getModule().update(this);
    }

    @LuaFunction
    public int getBorderWidth() {
        return borderWidth;
    }

    @LuaFunction
    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        getModule().update(this);
    }

    @LuaFunction
    public int getSegments() {
        return segments;
    }

    @LuaFunction
    public void setSegments(int segments) {
        this.segments = segments;
        getModule().update(this);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(TYPE_ID);
        super.encode(buffer);
        buffer.writeInt(radius);
        buffer.writeBoolean(filled);
        buffer.writeBoolean(pixelated);
        buffer.writeInt(borderWidth);
        buffer.writeInt(segments);
    }

    public static CircleObject decode(FriendlyByteBuf buffer) {
        Optional<CircleObject> optionalObject = RenderableObject.baseDecode(buffer, CircleObject::new);
        if (optionalObject.isEmpty())
            return null;

        int radius = buffer.readInt();
        boolean filled = buffer.readBoolean();
        boolean pixelated = buffer.readBoolean();
        int borderWidth = buffer.readInt();
        int segments = buffer.readInt();

        CircleObject clientObject = optionalObject.get();
        clientObject.radius = radius;
        clientObject.filled = filled;
        clientObject.pixelated = pixelated;
        clientObject.borderWidth = borderWidth;
        clientObject.segments = segments;

        return clientObject;
    }

    @Override
    public IObjectRenderer getObjectRenderer() {
        return RENDERER;
    }

    @Override
    public String toString() {
        return "Circle{" +
                "radius=" + radius +
                ", opacity=" + opacity +
                ", color=" + color +
                ", x=" + x +
                ", y=" + y +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                '}';
    }
}
