package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.TorusRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class TorusObject extends ThreeDimensionalObject {
    public static final int TYPE_ID = 7;

    private static final TorusRenderer RENDERER = new TorusRenderer();

    @FixedPointNumberProperty(min = 1, max = 1024)
    public int sides = 32;

    @FixedPointNumberProperty(min = 1, max = 1024)
    public int rings = 16;

    @FloatingNumberProperty(min = 0.001f, max = 128)
    public float minorRadius = 0.1f;

    @FloatingNumberProperty(min = 0.001f, max = 128)
    public float majorRadius = 0.5f;

    public TorusObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
    }

    public TorusObject(UUID player) {
        super(player);
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @LuaFunction
    public final float getMinorRadius() {
        return minorRadius;
    }

    @LuaFunction
    public final void setMinorRadius(float radius) {
        this.minorRadius = radius;
        this.sendUpdate();
    }

    @LuaFunction
    public final float getMajorRadius() {
        return majorRadius;
    }

    @LuaFunction
    public final void setMajorRadius(float radius) {
        this.majorRadius = radius;
        this.sendUpdate();
    }

    @LuaFunction
    public final int getSides() {
        return sides;
    }

    @LuaFunction
    public final void setSides(int sides) {
        this.sides = sides;
        this.sendUpdate();
    }

    @LuaFunction
    public final int getRings() {
        return rings;
    }

    @LuaFunction
    public final void setRings(int rings) {
        this.rings = rings;
        this.sendUpdate();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeInt(sides);
        buffer.writeInt(rings);
        buffer.writeFloat(minorRadius);
        buffer.writeFloat(majorRadius);
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.sides = buffer.readInt();
        this.rings = buffer.readInt();
        this.minorRadius = buffer.readFloat();
        this.majorRadius = buffer.readFloat();
    }

    @Override
    public IThreeDObjectRenderer getObjectRenderer() {
        return RENDERER;
    }
}
