package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;

public class SmartGlassesPeripheralOwner extends PocketPeripheralOwner {
    private final SmartGlassesSideAccess access;

    public SmartGlassesPeripheralOwner(SmartGlassesSideAccess access) {
        super(access);
        this.access = access;
    }

    public SmartGlassesComputer getComputer() {
        return this.access.getComputer();
    }

    @Override
    @NotNull
    public Vec3 getCenterPos() {
        Entity owner = this.access.getEntity();
        if (owner != null) {
            return owner.getEyePosition();
        }
        return this.access.getPosition();
    }

    @Override
    @NotNull
    public Matrix3dc getOrientation() {
        Entity owner = this.access.getEntity();
        if (owner == null) {
            return new Matrix3d();
        }
        Vec3 front = owner.getLookAngle();
        Vec3 up = owner.getUpVector(1.0f);
        Vec3 right = front.cross(up);
        return new Matrix3d(
            right.x, right.y, right.z,
            up.x, up.y, up.z,
            front.x, front.y, front.z
        );
    }
}
