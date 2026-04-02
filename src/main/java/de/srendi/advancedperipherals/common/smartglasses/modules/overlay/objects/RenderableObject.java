package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public abstract class RenderableObject extends OverlayObject {

    @FloatingNumberProperty(min = 0, max = 1)
    public float opacity = 1;

    @FixedPointNumberProperty(min = 0, max = Integer.MAX_VALUE)
    public int color = 0xFFFFFF;

    @FloatingNumberProperty(min = -32767, max = 32767)
    public float x = 0;

    @FloatingNumberProperty(min = -32767, max = 32767)
    public float y = 0;

    @FloatingNumberProperty(min = -32767, max = 32767)
    public float z = 0;

    @FloatingNumberProperty(min = -32767, max = 32767)
    public float maxX = 0;

    @FloatingNumberProperty(min = -32767, max = 32767)
    public float maxY = 0;

    @FloatingNumberProperty(min = -32767, max = 32767)
    public float maxZ = 0;

    @FloatingNumberProperty(min = 0, max = 360)
    public float rotX = 0f;

    @FloatingNumberProperty(min = 0, max = 360)
    public float rotY = 0f;

    @FloatingNumberProperty(min = 0, max = 360)
    public float rotZ = 0f;

    public RenderableObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
    }

    public RenderableObject(UUID player) {
        super(player);
    }

    // TODO: switch to Registry
    public abstract int getTypeId();

    public void sendUpdate() {
        this.getModule().update(this);
    }

    @LuaFunction
    public final float getOpacity() {
        return opacity;
    }

    @LuaFunction
    public final void setOpacity(double opacity) {
        this.opacity = (float) opacity;
        this.sendUpdate();
    }

    @LuaFunction
    public final int getColor() {
        return color;
    }

    @LuaFunction
    public final void setColor(int color) {
        this.color = color;
        this.sendUpdate();
    }

    @LuaFunction
    public final double getX() {
        return x;
    }

    @LuaFunction
    public final void setX(double x) {
        this.x = (float) x;
        this.sendUpdate();
    }

    @LuaFunction
    public final double getY() {
        return y;
    }

    @LuaFunction
    public final void setY(double y) {
        this.y = (float) y;
        this.sendUpdate();
    }

    @LuaFunction
    public final float getZ() {
        return z;
    }

    @LuaFunction
    public final void setZ(double z) {
        this.z = (float) z;
        this.sendUpdate();
    }

    @LuaFunction
    public final double getMaxX() {
        return maxX;
    }

    @LuaFunction
    public final void setMaxX(double maxX) {
        this.maxX = (float) maxX;
        this.sendUpdate();
    }

    @LuaFunction
    public final double getMaxY() {
        return maxY;
    }

    @LuaFunction
    public final void setMaxY(double maxY) {
        this.maxY = (float) maxY;
        this.sendUpdate();
    }

    @LuaFunction
    public final double getMaxZ() {
        return maxZ;
    }

    @LuaFunction
    public final void setMaxZ(double maxZ) {
        this.maxZ = (float) maxZ;
        this.sendUpdate();
    }

    @LuaFunction
    public final double getRotX() {
        return rotX;
    }

    @LuaFunction
    public final void setRotX(double xRot) {
        this.rotX = (float) xRot;
        this.sendUpdate();
    }

    @LuaFunction
    public final double getRotY() {
        return rotY;
    }

    @LuaFunction
    public final void setRotY(double yRot) {
        this.rotY = (float) yRot;
        this.sendUpdate();
    }

    @LuaFunction
    public final double getRotZ() {
        return rotZ;
    }

    @LuaFunction
    public final void setRotZ(double zRot) {
        this.rotZ = (float) zRot;
        this.sendUpdate();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeInt(color);
        buffer.writeFloat(opacity);

        buffer.writeFloat(x);
        buffer.writeFloat(y);
        buffer.writeFloat(z);
        buffer.writeFloat(maxX);
        buffer.writeFloat(maxY);
        buffer.writeFloat(maxZ);
        buffer.writeFloat(rotX);
        buffer.writeFloat(rotY);
        buffer.writeFloat(rotZ);
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.color = buffer.readInt();
        this.opacity = buffer.readFloat();

        this.x = buffer.readFloat();
        this.y = buffer.readFloat();
        this.z = buffer.readFloat();
        this.maxX = buffer.readFloat();
        this.maxY = buffer.readFloat();
        this.maxZ = buffer.readFloat();
        this.rotX = buffer.readFloat();
        this.rotY = buffer.readFloat();
        this.rotZ = buffer.readFloat();
    }

    public abstract IObjectRenderer getObjectRenderer();

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
                ", sizeZ=" + maxZ +
                '}';
    }
}
