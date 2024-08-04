package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.CircleRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertyTypes.FixedPointNumberProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class Circle extends RenderableObject {

    public static final int ID = 1;

    @FixedPointNumberProperty(min = -32767, max = 32767)
    public int radius = 0;

    public Circle(String id, OverlayModule module, IArguments arguments) throws LuaException {
        super(id, module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public Circle(String id, UUID player) {
        super(id, player);
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

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(ID);
        super.encode(buffer);
        buffer.writeInt(radius);
    }

    public static Circle decode(FriendlyByteBuf buffer) {
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
        int maxX = buffer.readInt();
        int maxY = buffer.readInt();
        int radius = buffer.readInt();

        Circle clientObject = new Circle(id, player);
        clientObject.color = color;
        clientObject.opacity = opacity;
        clientObject.x = x;
        clientObject.y = y;
        clientObject.maxX = maxX;
        clientObject.maxY = maxY;
        clientObject.radius = radius;

        return clientObject;
    }

    @Override
    public IObjectRenderer getRenderObject() {
        return new CircleRenderer();
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
