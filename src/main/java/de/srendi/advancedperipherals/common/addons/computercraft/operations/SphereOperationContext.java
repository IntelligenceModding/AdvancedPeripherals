package de.srendi.advancedperipherals.common.addons.computercraft.operations;

import java.io.Serializable;

public class SphereOperationContext implements Serializable {
    private final int radius;

    public SphereOperationContext(int radius) {
        this.radius = radius;
    }

    public static SphereOperationContext of(int radius) {
        return new SphereOperationContext(radius);
    }

    public int getRadius() {
        return radius;
    }
}
