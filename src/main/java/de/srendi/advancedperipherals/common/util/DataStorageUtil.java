package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static de.srendi.advancedperipherals.common.setup.APDataComponents.ROTATION_CHARGE_SETTING;

public class DataStorageUtil {
    public static CompoundTag getDataStorage(@NotNull IPeripheralTileEntity tileEntity) {
        return tileEntity.getPeripheralSettings();
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
        public static int get(@NotNull ITurtleAccess access, @NotNull TurtleSide side) {
            Optional<? extends Integer> rotationCharge = access.getUpgradeData(side).get(ROTATION_CHARGE_SETTING.get());
            return rotationCharge != null && rotationCharge.isPresent() ? rotationCharge.get() : 0;
        }

        public static boolean consume(@NotNull ITurtleAccess access, @NotNull TurtleSide side) {
            PatchedDataComponentMap patch = PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, access.getUpgradeData(side));
            int currentCharge = get(access, side);
            if (currentCharge > 0) {
                patch.set(ROTATION_CHARGE_SETTING.get(), Math.max(0, get(access, side) - 1));
                access.setUpgradeData(side, patch.asPatch());
                return true;
            }
            return false;
        }

        public static void addCycles(IPeripheralOwner owner, int count) {
            PatchedDataComponentMap patch = owner.getPatchedDataStorage();
            Integer currentCharge = patch.get(ROTATION_CHARGE_SETTING.get());
            if (currentCharge == null || currentCharge < 0) {
                currentCharge = 0;
            }
            patch.set(ROTATION_CHARGE_SETTING.get(), currentCharge + count * ROTATION_STEPS);
            owner.putDataStorage(patch.asPatch());
        }
    }
}
