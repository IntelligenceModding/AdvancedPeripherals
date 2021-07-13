package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.api.peripheral.IPeripheralOwner;
import de.srendi.advancedperipherals.api.peripheral.IPeripheralTileEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class DataStorageUtil {
    /**
     This class is for persistent data sharing between peripherals and another part of systems
     Like, for example, for ModelTransformingTurtle logic, because it executed on client side where
     not peripheral is available!
     **/

    public static class RotationCharge {
        /**
         * Used for gear rotation animation
         */
        private final static String ROTATION_CHARGE_SETTING = "rotationCharge";
        public final static int ROTATION_STEPS = 36;

        public static int get(@Nonnull ITurtleAccess access, @Nonnull TurtleSide side) {
            return getDataStorage(access, side).getInt(ROTATION_CHARGE_SETTING);
        }

        public static void consume(@Nonnull ITurtleAccess access, @Nonnull TurtleSide side) {
            CompoundNBT data = getDataStorage(access, side);
            int currentCharge = data.getInt(ROTATION_CHARGE_SETTING);
            if (currentCharge > 0) {
                data.putInt(ROTATION_CHARGE_SETTING, Math.max(0, data.getInt(ROTATION_CHARGE_SETTING) - 1));
            }
        }

        public static void addCycles(IPeripheralOwner owner, int count) {
            CompoundNBT data = owner.getDataStorage();
            data.putInt(ROTATION_CHARGE_SETTING, Math.max(0, data.getInt(ROTATION_CHARGE_SETTING)) + count * ROTATION_STEPS);
        }

    }

    public static CompoundNBT getDataStorage(@Nonnull ITurtleAccess access, @Nonnull TurtleSide side) {
        return access.getUpgradeNBTData(side);
    }

    public static CompoundNBT getDataStorage(@Nonnull IPeripheralTileEntity tileEntity) {
        return tileEntity.getApSettings();
    }

    public static CompoundNBT getDataStorage(@Nonnull IPocketAccess pocket) {
        return pocket.getUpgradeNBTData();
    }
}
