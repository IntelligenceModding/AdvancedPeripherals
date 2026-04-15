package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import org.joml.Quaternionf;

import java.util.UUID;

public abstract class RenderableObject extends OverlayObject {

    @FloatingNumberProperty(min = 0, max = 1)
    public float opacity = 1;

    @FixedPointNumberProperty(min = 0, max = 0xffffff)
    public int color = 0xffffff;

    @FloatingNumberProperty
    public float x = 0;

    @FloatingNumberProperty
    public float y = 0;

    @FloatingNumberProperty
    public float z = 0;

    @FloatingNumberProperty(min = 0, max = 360)
    public float rotX = 0f;

    @FloatingNumberProperty(min = 0, max = 360)
    public float rotY = 0f;

    @FloatingNumberProperty(min = 0, max = 360)
    public float rotZ = 0f;

    public RenderableObject(OverlayModule module) {
        super(module);
    }

    public RenderableObject(UUID player) {
        super(player);
    }

    public Quaternionf getRotation() {
        return new Quaternionf()
            .rotationYXZ(
                (float) Math.toRadians(this.rotY),
                (float) Math.toRadians(this.rotX),
                (float) Math.toRadians(this.rotZ)
            );
    }

    @LuaFunction
    public MethodResult getPos() {
        return MethodResult.of(this.x, this.y, this.z);
    }

    @LuaFunction
    public void setPos(double x, double y, double z) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
        this.markAndTryUpdate("x", "y", "z");
    }

    @Override
    public String toString() {
        return "RenderableObject{" +
                "opacity=" + opacity +
                ", color=" + color +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
