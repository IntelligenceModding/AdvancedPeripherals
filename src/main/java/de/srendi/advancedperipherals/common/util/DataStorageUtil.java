package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class DataStorageUtil {

    public static CompoundTag getDataStorage(@NotNull ITurtleAccess access, @NotNull TurtleSide side) {
        return access.getUpgradeNBTData(side);
    }

    public static CompoundTag getDataStorage(@NotNull IPeripheralTileEntity tileEntity) {
        return tileEntity.getPeripheralSettings();
    }

    public static CompoundTag getDataStorage(@NotNull IPocketAccess pocket) {
        return pocket.getUpgradeNBTData();
    }

    /**
     * This class is for persistent data sharing between peripherals and another part of systems
     * Like, for example, for ModelTransformingTurtle logic, because it's executed on the client where
     * aren't any peripherals available
     **/

    public static class RotationCharge {
        public static final int ROTATION_STEPS = 36;
        /**
         * Used for gear rotation animation
         */
        private static final String ROTATION_CHARGE_SETTING = "rotationCharge";

        public static int get(@NotNull ITurtleAccess access, @NotNull TurtleSide side) {
            return getDataStorage(access, side).getInt(ROTATION_CHARGE_SETTING);
        }

        public static boolean consume(@NotNull ITurtleAccess access, @NotNull TurtleSide side) {
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
