package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralBlockEntity;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static de.srendi.advancedperipherals.common.setup.APDataComponents.ROTATION_CHARGE_SETTING;

public class DataStorageUtil {
    public static DataComponentPatch getDataStorage(@NotNull IPeripheralBlockEntity tileEntity) {
        CompoundTag tag = tileEntity.getPeripheralSettings();
        if (tag.isEmpty()) {
            return DataComponentPatch.EMPTY;
        }
        return DataComponentPatch.CODEC
            .parse(NbtOps.INSTANCE, tag)
            .resultOrPartial()
            .orElse(DataComponentPatch.EMPTY);
    }

    public static void putDataStorage(@NotNull IPeripheralBlockEntity tileEntity, DataComponentPatch patch) {
        if (patch.isEmpty() && tileEntity.getPeripheralSettings().isEmpty()) {
            return;
        }
        tileEntity.setPeripheralSettings((CompoundTag) DataComponentPatch.CODEC
            .encodeStart(NbtOps.INSTANCE, patch)
            .getOrThrow());
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
            if (currentCharge <= 0) {
                return false;
            }
            patch.set(ROTATION_CHARGE_SETTING.get(), Math.max(0, get(access, side) - 1));
            access.setUpgradeData(side, patch.asPatch());
            return true;
        }

        public static void addCycles(IPeripheralOwner owner, int count) {
            PatchedDataComponentMap patch = owner.getPatchedDataStorage();
            int currentCharge = patch.getOrDefault(ROTATION_CHARGE_SETTING.get(), 0);
            patch.set(ROTATION_CHARGE_SETTING.get(), currentCharge + count * ROTATION_STEPS);
            owner.putDataStorage(patch.asPatch());
        }
    }
}
