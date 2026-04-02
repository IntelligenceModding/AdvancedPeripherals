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

import java.util.UUID;

public class CircleObject extends RenderableObject {
    public static final int TYPE_ID = 1;

    private static final CircleRenderer RENDERER = new CircleRenderer();

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
    }

    public CircleObject(UUID player) {
        super(player);
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @LuaFunction
    public int getRadius() {
        return radius;
    }

    @LuaFunction
    public void setRadius(int radius) {
        this.radius = radius;
        this.sendUpdate();
    }

    @LuaFunction
    public boolean isFilled() {
        return filled;
    }

    @LuaFunction
    public void setFilled(boolean filled) {
        this.filled = filled;
        this.sendUpdate();
    }

    @LuaFunction
    public boolean isPixelated() {
        return pixelated;
    }

    @LuaFunction
    public void setPixelated(boolean pixelated) {
        this.pixelated = pixelated;
        this.sendUpdate();
    }

    @LuaFunction
    public int getBorderWidth() {
        return borderWidth;
    }

    @LuaFunction
    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        this.sendUpdate();
    }

    @LuaFunction
    public int getSegments() {
        return segments;
    }

    @LuaFunction
    public void setSegments(int segments) {
        this.segments = segments;
        this.sendUpdate();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeInt(radius);
        buffer.writeBoolean(filled);
        buffer.writeBoolean(pixelated);
        buffer.writeInt(borderWidth);
        buffer.writeInt(segments);
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.radius = buffer.readInt();
        this.filled = buffer.readBoolean();
        this.pixelated = buffer.readBoolean();
        this.borderWidth = buffer.readInt();
        this.segments = buffer.readInt();
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
