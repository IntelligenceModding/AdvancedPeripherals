package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.BoxRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BoxObject extends ThreeDimensionalObject {
    public static final int TYPE_ID = 4;

    private static final BoxRenderer RENDERER = new BoxRenderer();

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
    public String getType() {
        return "box";
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
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

    @Override
    public IThreeDObjectRenderer getObjectRenderer() {
        return RENDERER;
    }
}
