package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import de.srendi.advancedperipherals.common.setup.APOverlayObjects;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TriangleObject extends ThreeDimensionalObject {
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

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
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
        this.x2 = buffer.readFloat();
        this.y2 = buffer.readFloat();
        this.z2 = buffer.readFloat();
        this.x3 = buffer.readFloat();
        this.y3 = buffer.readFloat();
        this.z3 = buffer.readFloat();
    }
}
