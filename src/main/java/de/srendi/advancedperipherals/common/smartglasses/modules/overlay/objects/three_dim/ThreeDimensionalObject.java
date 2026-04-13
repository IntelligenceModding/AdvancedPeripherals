package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public abstract class ThreeDimensionalObject extends RenderableObject {

    @BooleanProperty
    public boolean relativePosition = false;

    @BooleanProperty
    public boolean relativeRotation = false;

    @BooleanProperty(getterPrefix = "has")
    public boolean depthTest = true;

    @BooleanProperty
    public boolean culling = true;

    public ThreeDimensionalObject(OverlayModule module) {
        super(module);
    }

    public ThreeDimensionalObject(UUID player) {
        super(player);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeBoolean(this.relativePosition);
        buffer.writeBoolean(this.relativeRotation);
        buffer.writeBoolean(this.depthTest);
        buffer.writeBoolean(this.culling);
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        super.decode(buffer);
        this.relativePosition = buffer.readBoolean();
        this.relativeRotation = buffer.readBoolean();
        this.depthTest = buffer.readBoolean();
        this.culling = buffer.readBoolean();
    }
}
