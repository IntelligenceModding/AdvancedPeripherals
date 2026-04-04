package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.TorusRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

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

    public TorusObject(OverlayModule module) {
        super(module);
    }

    public TorusObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public String getType() {
        return "torus";
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
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
