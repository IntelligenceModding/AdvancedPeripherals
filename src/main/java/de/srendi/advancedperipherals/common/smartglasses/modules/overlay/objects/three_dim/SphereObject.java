package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.SphereRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.CircleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;
import java.util.UUID;

public class SphereObject extends ThreeDimensionalObject {
    public static final int TYPE_ID = 6;

    private static final IThreeDObjectRenderer RENDERER = new SphereRenderer();

    @FixedPointNumberProperty(min = 1, max = 1024)
    public int sectors = 16;

    @FixedPointNumberProperty(min = 1, max = 1024)
    public int stacks = 16;

    @FloatingNumberProperty(min = 0.001f, max = 128)
    public float radius = 1;

    public SphereObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public SphereObject(UUID player) {
        super(player);
    }

    @LuaFunction
    public final void setRadius(float radius) {
        this.radius = radius;
        getModule().update(this);
    }

    @LuaFunction
    public final float getRadius() {
        return radius;
    }

    @LuaFunction
    public final void setSectors(int sectors) {
        this.sectors = sectors;
        getModule().update(this);
    }

    @LuaFunction
    public final int getSectors() {
        return sectors;
    }

    @LuaFunction
    public final void setStacks(int stacks) {
        this.stacks = stacks;
        getModule().update(this);
    }

    @LuaFunction
    public final int getStacks() {
        return stacks;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(TYPE_ID);
        super.encode(buffer);
        buffer.writeInt(sectors);
        buffer.writeInt(stacks);
        buffer.writeFloat(radius);
    }

    public static SphereObject decode(FriendlyByteBuf buffer) {
        Optional<SphereObject> optionalObject = RenderableObject.baseDecode(buffer, SphereObject::new);
        if (optionalObject.isEmpty())
            return null;

        boolean disableDepthTest = buffer.readBoolean();
        boolean disableCulling = buffer.readBoolean();

        int sectors = buffer.readInt();
        int stacks = buffer.readInt();
        float radius = buffer.readFloat();

        SphereObject clientObject = optionalObject.get();
        clientObject.disableDepthTest = disableDepthTest;
        clientObject.disableCulling = disableCulling;
        clientObject.sectors = sectors;
        clientObject.stacks = stacks;
        clientObject.radius = radius;

        return clientObject;
    }

    @Override
    public IThreeDObjectRenderer getObjectRenderer() {
        return RENDERER;
    }
}
