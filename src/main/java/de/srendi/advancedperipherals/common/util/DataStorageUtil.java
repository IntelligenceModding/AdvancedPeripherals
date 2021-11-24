package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;

public class DataStorageUtil {

    public static CompoundTag getDataStorage(@Nonnull ITurtleAccess access, @Nonnull TurtleSide side) {
        return access.getUpgradeNBTData(side);
    }

    public static CompoundTag getDataStorage(@Nonnull IPeripheralTileEntity tileEntity) {
        return tileEntity.getPeripheralSettings();
    }

    public static CompoundTag getDataStorage(@Nonnull IPocketAccess pocket) {
        return pocket.getUpgradeNBTData();
    }

    /**
     * This class is for persistent data sharing between peripherals and another part of systems
     * Like, for example, for ModelTransformingTurtle logic, because it executed on client side where
     * not peripheral is available!
     **/

    public static class RotationCharge {
        public final static int ROTATION_STEPS = 36;
        /**
         * Used for gear rotation animation
         */
        private final static String ROTATION_CHARGE_SETTING = "rotationCharge";

        public static int get(@Nonnull ITurtleAccess access, @Nonnull TurtleSide side) {
            return getDataStorage(access, side).getInt(ROTATION_CHARGE_SETTING);
        }

        public static boolean consume(@Nonnull ITurtleAccess access, @Nonnull TurtleSide side) {
            CompoundTag data = getDataStorage(access, side);
            int currentCharge = data.getInt(ROTATION_CHARGE_SETTING);
            if (currentCharge > 0) {
                data.putInt(ROTATION_CHARGE_SETTING, Math.max(0, data.getInt(ROTATION_CHARGE_SETTING) - 1));
                access.updateUpgradeNBTData(side);
                return true;
            }
            return false;
        }

        public static void addCycles(IPeripheralOwner owner, int count) {
            CompoundTag data = owner.getDataStorage();
            data.putInt(ROTATION_CHARGE_SETTING, Math.max(0, data.getInt(ROTATION_CHARGE_SETTING)) + count * ROTATION_STEPS);
            owner.markDataStorageDirty();
        }

    }
}
