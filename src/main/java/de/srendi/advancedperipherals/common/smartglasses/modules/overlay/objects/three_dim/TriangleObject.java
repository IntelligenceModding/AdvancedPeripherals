package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.setup.APOverlayObjects;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.network.FriendlyByteBuf;
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
        this.tryAutoUpdate();
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
        this.tryAutoUpdate();
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
        this.tryAutoUpdate();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeFloat(this.x1);
        buffer.writeFloat(this.y1);
        buffer.writeFloat(this.z1);
        buffer.writeFloat(this.x2);
        buffer.writeFloat(this.y2);
        buffer.writeFloat(this.z2);
        buffer.writeFloat(this.x3);
        buffer.writeFloat(this.y3);
        buffer.writeFloat(this.z3);
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.x1 = buffer.readFloat();
        this.y1 = buffer.readFloat();
        this.z1 = buffer.readFloat();
        this.x2 = buffer.readFloat();
        this.y2 = buffer.readFloat();
        this.z2 = buffer.readFloat();
        this.x3 = buffer.readFloat();
        this.y3 = buffer.readFloat();
        this.z3 = buffer.readFloat();
    }
}
