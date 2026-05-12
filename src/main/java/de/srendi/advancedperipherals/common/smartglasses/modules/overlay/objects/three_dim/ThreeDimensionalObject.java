package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;

import java.util.UUID;

public abstract class ThreeDimensionalObject extends RenderableObject {

    @BooleanProperty
    public boolean relativePosition = false;

    @BooleanProperty
    public boolean relativeRotation = false;

    @BooleanProperty
    public boolean culling = true;

    @BooleanProperty(getterPrefix = "has")
    public boolean depthTest = true;

    @BooleanProperty(getterPrefix = "has")
    public boolean depthMask = true;

    public ThreeDimensionalObject(OverlayModule module) {
        super(module);
    }

    public ThreeDimensionalObject(UUID player) {
        super(player);
    }
}
