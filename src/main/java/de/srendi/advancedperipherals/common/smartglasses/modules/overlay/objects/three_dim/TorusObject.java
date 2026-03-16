package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.TorusRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;
import java.util.UUID;

public class TorusObject extends ThreeDimensionalObject {
    public static final int TYPE_ID = 7;

    private static final IThreeDObjectRenderer RENDERER = new TorusRenderer();

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
        reflectivelyMapProperties(arguments);
    }

    public TorusObject(UUID player) {
        super(player);
    }

    @LuaFunction
    public final void setMinorRadius(float radius) {
        this.minorRadius = radius;
        getModule().update(this);
    }

    @LuaFunction
    public final float getMinorRadius() {
        return minorRadius;
    }

    @LuaFunction
    public final void setMajorRadius(float radius) {
        this.majorRadius = radius;
        getModule().update(this);
    }

    @LuaFunction
    public final float getMajorRadius() {
        return majorRadius;
    }

    @LuaFunction
    public final void setSides(int sides) {
        this.sides = sides;
        getModule().update(this);
    }

    @LuaFunction
    public final int getSides() {
        return sides;
    }

    @LuaFunction
    public final void setRings(int rings) {
        this.rings = rings;
        getModule().update(this);
    }

    @LuaFunction
    public final int getRings() {
        return rings;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(TYPE_ID);
        super.encode(buffer);
        buffer.writeInt(sides);
        buffer.writeInt(rings);
        buffer.writeFloat(minorRadius);
        buffer.writeFloat(majorRadius);
    }

    public static TorusObject decode(FriendlyByteBuf buffer) {
        Optional<TorusObject> optionalObject = RenderableObject.baseDecode(buffer, TorusObject::new);
        if (optionalObject.isEmpty())
            return null;

        boolean disableDepthTest = buffer.readBoolean();
        boolean disableCulling = buffer.readBoolean();

        int sectors = buffer.readInt();
        int stacks = buffer.readInt();
        float minorRadius = buffer.readFloat();
        float majorRadius = buffer.readFloat();

        TorusObject clientObject = optionalObject.get();
        clientObject.disableDepthTest = disableDepthTest;
        clientObject.disableCulling = disableCulling;
        clientObject.sides = sectors;
        clientObject.rings = stacks;
        clientObject.minorRadius = minorRadius;
        clientObject.majorRadius = majorRadius;

        return clientObject;
    }

    @Override
    public IThreeDObjectRenderer getObjectRenderer() {
        return RENDERER;
    }
}
