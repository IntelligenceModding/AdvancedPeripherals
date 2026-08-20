package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import static de.srendi.advancedperipherals.common.setup.APDataComponents.ROTATION_CHARGE_SETTING;

public class DataStorageUtil {
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
        public static int get(@NotNull ITurtleAccess access, @NotNull TurtleSide side) {
            return access.getUpgradeNBTData(side).getCompound("data").getInt(ROTATION_CHARGE_SETTING);
        }

        public static boolean consume(@NotNull ITurtleAccess access, @NotNull TurtleSide side) {
            CompoundTag data = access.getUpgradeNBTData(side).getCompound("data");
            int currentCharge = get(access, side);
            if (currentCharge <= 0) {
                return false;
            }
            data.putInt(ROTATION_CHARGE_SETTING, Math.max(0, get(access, side) - 1));
            access.getUpgradeNBTData(side).put("data", data);
            access.updateUpgradeNBTData(side);
            return true;
        }

        public static void addCycles(IPeripheralOwner owner, int count) {
            CompoundTag data = owner.getDataStorage();
            int currentCharge = data.getInt(ROTATION_CHARGE_SETTING);
            data.putInt(ROTATION_CHARGE_SETTING, currentCharge + count * ROTATION_STEPS);
            owner.putDataStorage(data);
        }
    }
}
