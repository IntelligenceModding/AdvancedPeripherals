package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import de.srendi.advancedperipherals.common.setup.APOverlayObjects;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CircleObject extends RenderableObject {
    @FixedPointNumberProperty(min = 0)
    public int radius = 0;

    @BooleanProperty
    public boolean filled = true;

    @BooleanProperty
    public boolean pixelated = false;

    @FixedPointNumberProperty(min = 0, max = 32767)
    public int borderWidth = 4;

    @FixedPointNumberProperty(min = 0, max = 100)
    public int segments = 25;

    public CircleObject(OverlayModule module) {
        super(module);
    }

    public CircleObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public OverlayObjectType<CircleObject> getType() {
        return APOverlayObjects.CIRCLE.get();
    }

    @Override
    public String toString() {
        return "Circle{" +
                "radius=" + radius +
                ", opacity=" + opacity +
                ", color=" + color +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
