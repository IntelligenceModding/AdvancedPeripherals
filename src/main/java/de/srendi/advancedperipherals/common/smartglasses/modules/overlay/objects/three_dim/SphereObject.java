package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import de.srendi.advancedperipherals.common.setup.APOverlayObjects;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SphereObject extends ThreeDimensionalObject {
    @FixedPointNumberProperty(min = 1, max = 1024)
    public int sectors = 16;

    @FixedPointNumberProperty(min = 1, max = 1024)
    public int stacks = 16;

    @FloatingNumberProperty(min = 0.001f, max = 128)
    public float radius = 1;

    public SphereObject(OverlayModule module) {
        super(module);
    }

    public SphereObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public OverlayObjectType<SphereObject> getType() {
        return APOverlayObjects.SPHERE.get();
    }
}
