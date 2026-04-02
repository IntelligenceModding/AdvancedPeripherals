package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.SphereRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class SphereObject extends ThreeDimensionalObject {
    public static final int TYPE_ID = 6;

    private static final SphereRenderer RENDERER = new SphereRenderer();

    @FixedPointNumberProperty(min = 1, max = 1024)
    public int sectors = 16;

    @FixedPointNumberProperty(min = 1, max = 1024)
    public int stacks = 16;

    @FloatingNumberProperty(min = 0.001f, max = 128)
    public float radius = 1;

    public SphereObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
    }

    public SphereObject(UUID player) {
        super(player);
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @LuaFunction
    public final float getRadius() {
        return radius;
    }

    @LuaFunction
    public final void setRadius(float radius) {
        this.radius = radius;
        this.sendUpdate();
    }

    @LuaFunction
    public final int getSectors() {
        return sectors;
    }

    @LuaFunction
    public final void setSectors(int sectors) {
        this.sectors = sectors;
        this.sendUpdate();
    }

    @LuaFunction
    public final int getStacks() {
        return stacks;
    }

    @LuaFunction
    public final void setStacks(int stacks) {
        this.stacks = stacks;
        this.sendUpdate();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeInt(sectors);
        buffer.writeInt(stacks);
        buffer.writeFloat(radius);
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.sectors = buffer.readInt();
        this.stacks = buffer.readInt();
        this.radius = buffer.readFloat();
    }

    @Override
    public IThreeDObjectRenderer getObjectRenderer() {
        return RENDERER;
    }
}
