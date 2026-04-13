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

public class BoxObject extends ThreeDimensionalObject {
    @FloatingNumberProperty
    public float sizeX = 1;

    @FloatingNumberProperty
    public float sizeY = 1;

    @FloatingNumberProperty
    public float sizeZ = 1;

    public BoxObject(OverlayModule module) {
        super(module);
    }

    public BoxObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public OverlayObjectType<BoxObject> getType() {
        return APOverlayObjects.BOX.get();
    }

    @LuaFunction
    public MethodResult getSizes() {
        return MethodResult.of(this.sizeX, this.sizeY, this.sizeZ);
    }

    @LuaFunction
    public void setSizes(double x, double y, double z) {
        this.sizeX = (float) Math.max(x, 0);
        this.sizeY = (float) Math.max(y, 0);
        this.sizeZ = (float) Math.max(z, 0);
        this.tryAutoUpdate();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeFloat(this.sizeX);
        buffer.writeFloat(this.sizeY);
        buffer.writeFloat(this.sizeZ);
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.sizeX = buffer.readFloat();
        this.sizeY = buffer.readFloat();
        this.sizeZ = buffer.readFloat();
    }
}
