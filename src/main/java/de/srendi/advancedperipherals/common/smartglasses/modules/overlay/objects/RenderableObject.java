package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

import java.util.UUID;

public abstract class RenderableObject extends OverlayObject {
    @BooleanProperty
    public boolean gui = false;

    @BooleanProperty
    public boolean relativePosition = false;

    @BooleanProperty
    public boolean relativeRotation = false;

    @FloatingNumberProperty(min = 0, max = 1, lerp = true)
    public float opacity = 1;

    @FixedPointNumberProperty(min = -1, max = 0xffffff)
    public int color = -1;

    @FloatingNumberProperty(lerp = true)
    public float x = 0;
    private float xLast = 0;

    @FloatingNumberProperty(lerp = true)
    public float y = 0;
    private float yLast = 0;

    @FloatingNumberProperty(lerp = true)
    public float z = 0;
    private float zLast = 0;

    @FloatingNumberProperty(min = 0, max = 360, continous = true, lerp = true)
    public float rotX = 0;
    private float rotXLast = 0;

    @FloatingNumberProperty(min = 0, max = 360, continous = true, lerp = true)
    public float rotY = 0;
    private float rotYLast = 0;

    @FloatingNumberProperty(min = 0, max = 360, continous = true, lerp = true)
    public float rotZ = 0;
    private float rotZLast = 0;

    private Quaternionf cachedRotation = null;
    private float cachedRotX;
    private float cachedRotY;
    private float cachedRotZ;

    public RenderableObject(OverlayModule module) {
        super(module);
    }

    public RenderableObject(UUID player) {
        super(player);
    }

    public float getX(float alpha) {
        return Mth.lerp(alpha, this.xLast, this.x);
    }

    public float getY(float alpha) {
        return Mth.lerp(alpha, this.yLast, this.y);
    }

    public float getZ(float alpha) {
        return Mth.lerp(alpha, this.zLast, this.z);
    }

    public float getRotX(float alpha) {
        return Mth.rotLerp(alpha, this.rotXLast, this.rotX);
    }

    public float getRotY(float alpha) {
        return Mth.rotLerp(alpha, this.rotYLast, this.rotY);
    }

    public float getRotZ(float alpha) {
        return Mth.rotLerp(alpha, this.rotZLast, this.rotZ);
    }

    public Quaternionf getRotation(float alpha) {
        float rotX = this.getRotX(alpha);
        float rotY = this.getRotY(alpha);
        float rotZ = this.getRotZ(alpha);
        if (this.cachedRotation != null && this.cachedRotX == rotX && this.cachedRotY == rotY && this.cachedRotZ == rotZ) {
            return this.cachedRotation;
        }
        this.cachedRotX = rotX;
        this.cachedRotY = rotY;
        this.cachedRotZ = rotZ;
        this.cachedRotation = new Quaternionf()
            .rotationYXZ(
                (float) Math.toRadians(rotY),
                (float) Math.toRadians(rotX),
                (float) Math.toRadians(rotZ)
            );
        return this.cachedRotation;
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
    public void stepFields() {
        this.xLast = this.x;
        this.yLast = this.y;
        this.zLast = this.z;
        this.rotXLast = this.rotX;
        this.rotYLast = this.rotY;
        this.rotZLast = this.rotZ;
        super.stepFields();
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
