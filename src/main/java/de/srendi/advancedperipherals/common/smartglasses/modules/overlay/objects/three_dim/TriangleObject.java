package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.setup.APOverlayObjects;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TriangleObject extends ThreeDimensionalObject {
    @FloatingNumberProperty
    public float x1 = 0;

    @FloatingNumberProperty
    public float y1 = 0;

    @FloatingNumberProperty
    public float z1 = 0;

    @FloatingNumberProperty
    public float x2 = 0;

    @FloatingNumberProperty
    public float y2 = 0;

    @FloatingNumberProperty
    public float z2 = 0;

    @FloatingNumberProperty
    public float x3 = 0;

    @FloatingNumberProperty
    public float y3 = 0;

    @FloatingNumberProperty
    public float z3 = 0;

    public TriangleObject(OverlayModule module) {
        super(module);
    }

    public TriangleObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public OverlayObjectType<TriangleObject> getType() {
        return APOverlayObjects.TRIANGLE.get();
    }

    @LuaFunction
    public MethodResult getPos1() {
        return MethodResult.of(this.x1, this.y1, this.z1);
    }

    @LuaFunction
    public void setPos1(double x, double y, double z) {
        this.x1 = (float) x;
        this.y1 = (float) y;
        this.z1 = (float) z;
        this.markAndTryUpdate("x1", "y1", "z1");
    }

    @LuaFunction
    public MethodResult getPos2() {
        return MethodResult.of(this.x2, this.y2, this.z2);
    }

    @LuaFunction
    public void setPos2(double x, double y, double z) {
        this.x2 = (float) x;
        this.y2 = (float) y;
        this.z2 = (float) z;
        this.markAndTryUpdate("x2", "y2", "z2");
    }

    @LuaFunction
    public MethodResult getPos3() {
        return MethodResult.of(this.x3, this.y3, this.z3);
    }

    @LuaFunction
    public void setPos3(double x, double y, double z) {
        this.x3 = (float) x;
        this.y3 = (float) y;
        this.z3 = (float) z;
        this.markAndTryUpdate("x3", "y3", "z3");
    }
}
